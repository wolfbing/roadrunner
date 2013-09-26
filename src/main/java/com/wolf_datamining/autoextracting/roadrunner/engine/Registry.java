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

     Questo  programma  software libero;   lecito redistribuirlo  o
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
 * Registry.java
 * This class is used to order expression by Compress-Ratio
 * Created 
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.engine;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;


public class Registry {
    
    private Set expressions;
    private TokenList[] tokenlists;
    
    private Map expression2evaluator;
        
    /** Creates a new instance of Registry*/
    Registry(Set expressions, TokenList[] tokenlists) {
        this.expression2evaluator = new HashMap();
        this.expressions = expressions;
        this.tokenlists = tokenlists;
    }
    
    public Set getExpressions()        { return this.expressions; }
    
    public MDLEvaluator getMDLEvaluator(Expression expression) {
        MDLEvaluator evaluator = (MDLEvaluator)expression2evaluator.get(expression);
        if (evaluator==null) evaluator = new MDLEvaluator(expression, this.tokenlists);
        expression2evaluator.put(expression, evaluator);
        return evaluator;
    }
    
    private double getCompressRatio(Expression expression) {
        return getMDLEvaluator(expression).getCompressRatio();
    }

    public SortedSet getExpressionsByCompressRatio() {
        // sort by DL
        SortedSet result = new TreeSet(
        new Comparator() {
            
            public int compare(Object o1, Object o2) {
                Expression exp1 = (Expression)o1, exp2 = (Expression)o2;
                double cr1 = getCompressRatio(exp1), cr2 = getCompressRatio(exp2);
                return (cr1>cr2? +1 : (cr1==cr2 ? exp1.hashCode()-exp2.hashCode() : -1));
            }
            
            public boolean equals(Object obj) { return false; }
        });
        result.addAll(this.getExpressions());
        return result;
    }
    
}
