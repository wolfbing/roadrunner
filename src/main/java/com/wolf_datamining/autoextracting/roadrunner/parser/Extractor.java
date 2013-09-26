/*
     RoadRunner - an automatic wrapper generation system for Web data sources
     Copyright (C) 2003  Valter Crescenzi - crescenz@dia.uniroma3.it

     This program is  free software;  you can  redistribute it and/or
     modify it  under the terms  of the GNU General Public License as
     published by  the Free Software Foundation;  either version 2 of
     the License, or (at your option) any later version.

     This program is distributed in the hope that it  will be useful,
     but  WITHOUT ANY WARRANTY;  without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
     General Public License for more details.

     You should have received a copy of the GNU General Public License
     along with this program; if not, write to the:

     Free Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

     ----

     RoadRunner - un sistema per la generazione automatica di wrapper su sorgenti Web
     Copyright (C) 2003  Valter Crescenzi - crescenz@dia.uniroma3.it

     Questo  programma   software libero;   lecito redistribuirlo  o
     modificarlo secondo i termini della Licenza Pubblica Generica GNU
     come  pubblicata dalla Free Software Foundation; o la versione 2
     della licenza o (a propria scelta) una versione successiva.

     Questo programma   distribuito nella speranza che sia  utile, ma
     SENZA  ALCUNA GARANZIA;  senza neppure la  garanzia implicita  di
     NEGOZIABILIT�  o di  APPLICABILIT PER  UN PARTICOLARE  SCOPO. Si
     veda la Licenza Pubblica Generica GNU per avere maggiori dettagli.

     Questo  programma deve  essere  distribuito assieme  ad una copia
     della Licenza Pubblica Generica GNU; in caso contrario, se ne pu
     ottenere  una scrivendo  alla:

     Free  Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

*/
/*
 * Extractor.java
 *
 * Created on 23 gennaio 2003, 15.17
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.parser;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.Instance;
import com.wolf_datamining.autoextracting.roadrunner.Sample;
import com.wolf_datamining.autoextracting.roadrunner.Wrapper;
import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;


public class Extractor extends ParserListenerAdapter implements ParserListener {
    
    /** Creates a new instance of Extractor */
    private Wrapper wrapper;
    private IdentityHashMap variants;
    private IdentityHashMap tokens2nodes;
    private ASTBuilder builder;
    private Parser parser;
    private TokenList tokenlist;
    
    public Extractor(Wrapper wrapper) {
        this.wrapper = wrapper;
        this.variants = new IdentityHashMap();
        this.parser = new Parser(wrapper.getExpression());
        this.parser.setParserListener(this);
        findVariantSubtrees(wrapper.getExpression());
    }
    
    private void findVariantSubtrees(Expression exp) {
        // A subtree of given exp is variant iff it has a variant descendant
        final Set variantLeaves = new HashSet();
        new Walker(exp) {
            public boolean visit(ASTVariant variant) {
                variantLeaves.add(getCurrentRelativePath());
                return false;
            }
            public boolean visit(ASTSubtree subtree) {
                variantLeaves.add(getCurrentRelativePath());
                return false;
            }
        }.visit(exp);
        Iterator it = variantLeaves.iterator();
        while (it.hasNext()) {
            addAsVariantsAllNodesOnPath(exp, (Node.Path)it.next());
        }
    }
    
    private void addAsVariantsAllNodesOnPath(Expression exp, Node.Path path) {
        int indices[] = path.indices();
        Node current = exp.getRoot();
        for (int i=0; i<indices.length; i++) {
            this.variants.put(current,null);
            current = current.jjtGetChild(indices[i]);
        }
        this.variants.put(current,null);
    }
    
    public Instance extract(Sample sample) throws BindingException {
        this.tokenlist = sample.getTokenlist();
        this.tokens2nodes = new IdentityHashMap(); // Two token are equal iff they are the same object
        this.builder = new ASTBuilder();
        
        ASTAnd root = builder.createAnd();
        builder.addNode(root);
        builder.startNodeScope(root);
        boolean match = this.parser.parse(this.tokenlist);
        builder.endNodeScope(root);
        if (match)  {
            ASTAnd tree = (ASTAnd)builder.getRoot();
            IdentityHashMap tokens2nodes = this.getTokens2Nodes();
            Instance result = new Instance(this.wrapper,sample,tree,tokens2nodes);
            return result;
        }
        throw new BindingException("Cannot bind: expression and token list do not align "+this.parser.getMismatches());
    }
    
    private IdentityHashMap getTokens2Nodes() {
        return this.tokens2nodes;
    }
    
    public void startNode(Node node) {
        builder.startNodeScope();
    }
    
    public void endNode(Node node, boolean matches) {
        if (matches && variants.containsKey(node)) {
            Node newNode = builder.createNode(node);
            builder.terminateScope(newNode);
            builder.addNode(newNode);
        }
        else builder.discardScope();
    }
    public void endHook(ASTHook hook, boolean matches, int times) {
        // make null-instances if the hook matched 0 times
        if (times==0) makeNullInstance(hook);
        endNode(hook, matches);
    }
    
    private void makeNullInstance(ASTHook hook) {
        hook.jjtGetChild(0).jjtAccept(
        new VisitorPlugger() {
            
            public boolean visitNode(Node node) {
                builder.startNodeScope();
                visitChildren(node);
                if (variants.containsKey(node)) {// what about subtree?
                    Node newNode = builder.createNode(node);
                    builder.terminateScope(newNode);
                    builder.addNode(newNode);
                }
                else builder.discardScope();
                return true;
            }
            public boolean visit(ASTSubtree subtree) {
                Node nullSubtree = builder.createSubtree(null);
                builder.addNode(nullSubtree);
                return true;
            }
            public boolean visit(ASTVariant node) {
                Node nullToken = builder.createNullToken(node.getToken());
                builder.addNode(nullToken);
                tokens2nodes.put(nullToken, node);
                return true;
            }
            
        });
    }
    
    public void startVariant(ASTVariant node) { }
    public void endVariant(ASTVariant node, boolean matches, Token token) {
        if (matches) {
            Node newToken = builder.createTokenNode(token);
            this.builder.addNode(newToken);
            this.tokens2nodes.put(newToken, node);
        }
    }
    public void startSubtree(ASTSubtree node) { }
    public void endSubtree(ASTSubtree node, boolean matches, int startIndex, int endIndex) {
        /* extract TokenListRegion */
        ASTSubtree subtree = this.builder.createSubtree(node.getRootToken());
        subtree.setMatchingTokenList(extractTokenListRegion(startIndex,endIndex));
        this.builder.addNode(subtree);
    }
    
    private TokenListRegion extractTokenListRegion(int startIndex, int endIndex) {
        Direction d = Direction.LEFT2RIGHT;
        TokenListRegion tlr = this.tokenlist.asRegion();
        return (TokenListRegion)d.subRegion(tlr, d.posBeforeIndex(startIndex)-1, d.posAfterIndex(endIndex)+1);
    }
    
}
