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
     come � pubblicata dalla Free Software Foundation; o la versione 2
     della licenza o (a propria scelta) una versione successiva.

     Questo programma   distribuito nella speranza che sia  utile, ma
     SENZA  ALCUNA GARANZIA;  senza neppure la  garanzia implicita  di
     NEGOZIABILIT�  o di  APPLICABILIT� PER  UN PARTICOLARE  SCOPO. Si
     veda la Licenza Pubblica Generica GNU per avere maggiori dettagli.

     Questo  programma deve  essere  distribuito assieme  ad una copia
     della Licenza Pubblica Generica GNU; in caso contrario, se ne pu
     ottenere  una scrivendo  alla:

     Free  Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

*/
/*
 * Expression.java
 *
 * This class implements Abstract Syntax Tree to represent regular expressions.
 * ASTs can be modified only through this class.
 * It maintains the structure based on ASTAnd nodes
 *
 * Created on 
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;
import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;



/* Wrapper    <-> Sample
 * Expression <-> TokenList
 * ASTAnd     <-> List
 **/

public class Expression {
    
    private List variants;
    private List invariants;
    
    private ASTAnd root;
    private int dl;
    
    public Expression(TokenList tl) {
        this(tl.asRegion());
    }

    public Expression(Region region) {
        this(region.asList());
    }
    
    private Expression(List siblings) {
        this(new ASTAnd(siblings));
    }
    
    Expression(ASTAnd root) {
        this.root = root;
        this.dl = -1;
        this.variants = null;
        this.invariants = null;
    }
    
    public Expression clone(boolean deep) {
        return new Expression((ASTAnd)getRoot().clone(deep));
    }
    
    public ASTAnd getRoot() {
        return this.root;
    }
    
    public Node getNode(Node.Path pos) {
        return this.getRoot().jjtGetNode(pos);
    }
    
    public ExpressionRegion asRegion() {
        return new ExpressionRegion(this,Node.EMPTY_PATH);
    }
    public boolean isSingleton() {
        return (this.getRoot().jjtGetNumChildren()==1);
    }
    
    /** method to modify the Abstract Syntax Tree */
    
    /* N.B. none method should be direction-sensitive */
    
    // This is the only method allowed to modify AST without cloning
    public void replaceNode(Node.Path path, Node newNode) {
        ASTAnd parent = (ASTAnd)this.getNode(path.subpath(-1));
        parent.jjtGetChildren().set(path.lastIndex(),newNode);
    }
    
    public Expression addPlus(ExpressionRegion extension, Expression square) {
        // square has to be cloned because some list implementations
        // (e.g. from subList) couldn't support the necessary modify operations
        ASTPlus plus = new ASTPlus((ASTAnd)(square.getRoot().clone(true)));
        return substitute(extension,plus);
    }
    
    public Expression addHook(ExpressionRegion extension, Expression square) {
        // square has to be cloned because some list implementations
        // (e.g. from subList) couldn't support the necessary modify operations
        ASTHook hook = new ASTHook((ASTAnd)(square.getRoot().clone(true)));
        return substitute(extension,hook);
    }
    
    public Expression addSubtree(ExpressionRegion extension) {
        // extension must correctly delimit a subtree
        ASTSubtree subtree = new ASTSubtree((Token)extension.getFirst());
        ExpressionRegion content = (ExpressionRegion)extension.subRegion(extension.getLeftBorderPos()+1,extension.getRightBorderPos()-1);
        Expression result = substitute(content,subtree);
        return result;
    }
    
    public Expression replaceSubtrees(SortedMap path2exp) {
        if (path2exp.isEmpty()) return this;
        Expression result = new Expression((ASTAnd)getRoot().clone(true));
        // Replace subtree from the last backwards
        while (!path2exp.isEmpty()) {
            Node.Path path = (Node.Path)path2exp.lastKey();
            Expression substitute = (Expression)path2exp.get(path);
            List siblings = result.getNode(path.subpath(-1)).jjtGetChildren();
            siblings.subList(path.lastIndex()-1, path.lastIndex()+2).clear();
            siblings.addAll(path.lastIndex()-1, substitute.getRoot().jjtGetChildren());
            path2exp.remove(path);
        }
        return result;
    }
    
    // Questo  l'unico vero metodo che pufare modifiche strutturali creando cloni
    private Expression substitute(ExpressionRegion region, Node node) {
        Expression result = this.clone(true);
        List regionList = region.asList(result);
        regionList.clear();
        regionList.add(node);
        return result;
    }
    
    public List getVariants() {
        //a variant is an ASTVariant node which matches with text
        if (this.variants==null) initVariantsAndInvariants();
        return this.variants;
    }
    
    public List getInvariants() {
        //an invariant is a Token node which matches with text
        if (this.invariants==null) initVariantsAndInvariants();
        return this.invariants;
    }
    
    private void initVariantsAndInvariants() {
        // final Variants nodes and Invariants nodes (TextToken of expression)
        final List variants = new ArrayList();
        final List invariants = new ArrayList();
        new VisitorPlugger() {
            public boolean visit(ASTToken token) {
                if (token.isPCDATA()) invariants.add(token);
                return true;
            }
            public boolean visit(ASTVariant variant) {
                if (variant.getToken().isPCDATA()) variants.add(variant);
                return false;
            }
        }.visit(this.getRoot());
        this.variants = variants;
        this.invariants = invariants;
    }
    
    public ASTVariant getVariant(int i) {
        return (ASTVariant)getVariants().get(i);
    }
    
    public Token getInvariant(int j) {
        return (Token)getInvariants().get(j);
    }
    
    public boolean equals(Object o) {
        if (this==o) return true;
        Expression that = (Expression)o;
        return this.getRoot().equals(that.getRoot());
    }
    public int hashCode() {
        return getRoot().hashCode();
    }
    
    public String dump(String prefix) {
        return this.getRoot().dump(new Indenter(0,prefix,true));
    }
    
    public String toString() {
        return dump("");
    }
}
