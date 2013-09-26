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
 * Lumberjack.java
 *
 * Created on 31 luglio 2003, 11.56
 */

package com.wolf_datamining.autoextracting.roadrunner.engine;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTSubtree;
import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.Node;
import com.wolf_datamining.autoextracting.roadrunner.ast.Walker;
import com.wolf_datamining.autoextracting.roadrunner.parser.Binder;
import com.wolf_datamining.autoextracting.roadrunner.parser.BindingException;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;


/**
 *
 * @author  Valter Crescenzi
 */
class Lumberjack {
    
    static private Logger log = Logger.getLogger(Lumberjack.class.getName());
    
    private Set expressions;
    private TokenList[] tokenlists;
    
    /** Creates a new instance of Lumberjack */
    Lumberjack(Set expressions, TokenList[] tl) {
        this.expressions = expressions;
        this.tokenlists = tl;
    }
    
    Set processSubtrees() {
        Set result = new LinkedHashSet();
        Iterator wIt = expressions.iterator();
        while (wIt.hasNext()) {
            try {
                Expression expression = (Expression)wIt.next();
                log.fine("Processing subtrees of expression:\n "+expression.dump("\t"));
                Expression processed = processSubtreesOf(expression);
                result.add(processed);
            }
            catch (BindingException be) {
                log.warning("This expression cannot processed: it does not match with its tokenlists");
            }
        }
        return result;
    }
    
    private Expression processSubtreesOf(Expression expression) throws BindingException {
        Binder binder = new Binder(expression);
        Map tokenlist2subtree = new LinkedHashMap(); // N.B. *must* preserve order
        Map subtree2path = getPathsOfSubtreeNodes(expression);
        SortedMap path2exp = new TreeMap();
        for (int i=0; i<tokenlists.length; i++) {
            binder.setBindings(tokenlists[i]);
            tokenlist2subtree.putAll(binder.getSubtreeBindings());
        }
        Map subtree2tokenlists = new IdentityHashMap();
        //N.B. if order is not fixed somehow, ordered of elements inside IdentityHashMap
        // depends on platform and even on runtime options of JVM
        Util.reverseMapping(subtree2tokenlists, tokenlist2subtree);
        Iterator subIt = subtree2tokenlists.entrySet().iterator();
        log.finer("Found "+subtree2tokenlists.size()+" subtrees to process");
        while (subIt.hasNext()) {
            Map.Entry entry = (Map.Entry)subIt.next();
            ASTSubtree subtree = (ASTSubtree) entry.getKey();
            List tokenlists = (List)entry.getValue();
            Node.Path path = (Node.Path)subtree2path.get(subtree);
            log.fine("Processing: "+subtree+" of path "+path);
            log.fine("Tokenlists associated with "+subtree+" of path "+path+"\n"+tokenlists.toString());
            Expression solution = matchSubtree(subtree, tokenlists);
            if (solution != null) {
                log.fine("Best replacing for subtree "+subtree+" of path "+path);
                log.fine("\n"+solution.dump(subtree.toString()+"\t"));
                path2exp.put(path,solution);
            }
            else {
                log.fine("No solutions: cannot remove "+subtree+" of path "+path);
            }
        }
        return expression.replaceSubtrees(path2exp);
    }
    
    private Expression matchSubtree(ASTSubtree subtree, List tls) {
        TokenList[] tlists = (TokenList[])tls.toArray(new TokenList[tls.size()]);
        Engine engine = new Engine(null, tlists, subtree.getRootToken().depth());
        Set solutions = engine.match();
        if (solutions.isEmpty()) return null;
        
        log.finer("Found "+solutions.size()+" replacing for "+subtree);
        log.finer("Saving the best and discarding all the others");
        Registry registry = new Registry(solutions, tlists);
        SortedSet orderedSolutions = registry.getExpressionsByCompressRatio();
        Expression best = (Expression)orderedSolutions.first();
        log.finer("Best solution CR: "+registry.getMDLEvaluator(best).getCompressRatio());
        return best;
    }
    
    private Map getPathsOfSubtreeNodes(final Expression expression) {
        final Map result = new IdentityHashMap();
        new Walker(expression) {
            public boolean visit(ASTSubtree node) {
                result.put(node, getCurrentAbsolutePath());
                return false;
            }
        }.visit(expression);
        return result;
    }
    
}
