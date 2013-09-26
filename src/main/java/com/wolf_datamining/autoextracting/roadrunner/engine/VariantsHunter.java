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

     Questo  programma  software libero;  lecito redistribuirlo  o
     modificarlo secondo i termini della Licenza Pubblica Generica GNU
     come � pubblicata dalla Free Software Foundation; o la versione 2
     della licenza o (a propria scelta) una versione successiva.

     Questo programma  distribuito nella speranza che sia  utile, ma
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
 * VariantsHunter.java
 *
 * Created on 12 gennaio 2003, 11.56
 * @author  Valter Crescenzi
 */


package com.wolf_datamining.autoextracting.roadrunner.engine;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTBuilder;
import com.wolf_datamining.autoextracting.roadrunner.ast.ASTToken;
import com.wolf_datamining.autoextracting.roadrunner.ast.ASTVariant;
import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.Node;
import com.wolf_datamining.autoextracting.roadrunner.ast.Walker;
import com.wolf_datamining.autoextracting.roadrunner.parser.Binder;
import com.wolf_datamining.autoextracting.roadrunner.parser.BindingException;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;


/**
 * Look for variants, that is, pcdata, link or images
 * which match with at least two different values.
 * Collect samples of each variant.
 */
public class VariantsHunter {
    
    static private Logger log = Logger.getLogger(VariantsHunter.class.getName());
    
    private Set expressions;
    private Set ambiguous;
    private TokenList[] tokenlists;
    
    public VariantsHunter(Set expressions, TokenList[] tokenlists) {
        this.expressions = expressions;
        this.tokenlists = tokenlists;
        this.ambiguous = null;
    }
    /**
     * @return the list of ambiguous expressions
     **/
    public Set insertVariants() {
        Set result = new LinkedHashSet();
        this.ambiguous = new LinkedHashSet();
        Iterator expIt = this.expressions.iterator();
        while (expIt.hasNext()) {
            Expression exp = (Expression)expIt.next();
            Expression withVariants = insertVariants(exp,  tokenlists);
            if (withVariants!=null) result.add(withVariants);
            else ambiguous.add(exp);
        }
        return result;
    }
    
    Set getAmbiguousExpressions() {
        return this.ambiguous;
    }
    
    
    private Expression insertVariants(Expression expression, TokenList[] tokenlists) {
        //return true if variants have been inserted, false otherwise
        try {
            Map bindings = bindTokens2Nodes(expression,tokenlists);
            Map nodes2tokens = new IdentityHashMap();
            Util.reverseMapping(nodes2tokens, bindings);
            Map path2variants = findPathOfVariants(expression, nodes2tokens);
            return insertASTVariantNodes(expression, path2variants);
        }
        catch (BindingException bindexc) {
            log.fine("Ambiguous expression; detected mismatches: "+bindexc.getMismatches());
        }
        return null;
    }
    
    private Map bindTokens2Nodes(Expression expression, TokenList[] tokenlists) throws BindingException {
        Binder binder = new Binder(expression);
        Map binds = new IdentityHashMap();
        for(int i=0; i<tokenlists.length; i++) {
            binder.setBindings(tokenlists[i]);
            binds.putAll(binder.getTokenBindings());
        }
        return binds;
    }
    
    private Map findPathOfVariants(final Expression expression, final Map nodes2tokens) {
        final Map path2samples = new HashMap();
        new Walker(expression) {
            
            public boolean visit(ASTToken node) {
                if (node.maybeVariant()) {
                    List tokens = (List)nodes2tokens.get(node);
                    if (tokens!=null && tokens.size()>1) {
                        Set variants = new HashSet();
                        Iterator it = tokens.iterator();
                        while (it.hasNext()) {
                            Token token = (Token)it.next();
                            variants.add(token.getVariantValue());
                        }
                        if (variants.size()>1) { //hit variant
                            path2samples.put(getCurrentAbsolutePath(), variants);
                        }
                    }
                }
                return false;
            }
            
        }.visit(expression);
        return path2samples;
    }
    
    private Expression insertASTVariantNodes(Expression exp, Map path2samples) {
        Expression result = exp.clone(true); //modify a new copy of old expression
        ASTBuilder builder = new ASTBuilder();
        Iterator it = path2samples.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Node.Path path = (Node.Path)entry.getKey();
            Set samples = (Set)entry.getValue();
            Token token = (Token)exp.getNode(path);
            ASTVariant variant = builder.createVariant(token);
            variant.setSamples(samples);
            result.replaceNode(path, variant);
        }
        return result;
    }
}