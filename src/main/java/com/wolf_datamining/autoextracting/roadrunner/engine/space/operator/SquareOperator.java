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
 * SquareOperator.java
 *
 * Created on 23 marzo 2003, 15.09
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space.operator;

import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.engine.sampler.CharacteristicSampler;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Operator;
import com.wolf_datamining.autoextracting.roadrunner.parser.MismatchPoint;
import com.wolf_datamining.autoextracting.roadrunner.parser.Parser;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;
import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;



abstract class SquareOperator extends SimpleOperator implements Operator {
    
    static private Logger log = Logger.getLogger(SquareOperator.class.getName());
    
    private Searcher searcher;
    Region square;
    int k;
    
    int lenOfMatchingBorder;
    
    /** Creates a new instance of SimpleOperator */
    SquareOperator(MismatchPoint mismatch, Searcher searcher, Region square, int k) {
        super(mismatch);
        this.expired = square.isEmpty();
        this.searcher = searcher;
        this.square = square;
        this.k = k;
    }
    
    Searcher getSearcher() {
        return this.searcher;
    }
    
    Region getRegionBeforeSquare() { return mismatch.getMatchingExpression(); }
    abstract Expression getCandidateSquare();
    abstract Region getRegionAfterSquare();
    Region getSquare()             { return this.square;                      }
    abstract ExpressionRegion getExtension(); 
        
    int countMatchingBorder(Direction dir, Region r1, Region r2, int maxLength) {
        if (maxLength==0) return 0;
        int result = countMatchingTokens(dir, r1, r2, maxLength) + countMatchingTokens(dir, r2, r1, maxLength);
        return result;
    }
    
    private int countMatchingTokens(Direction dir, Region r, Region forSampling, int maxLength) {
        CharacteristicSampler sampler = new CharacteristicSampler(forSampling, dir);
        sampler.setMaxSampleLength(maxLength);
        return countMatchingTokens(dir, r, (TokenList)sampler.computeNext());
    }
    
    private int countMatchingTokens(Direction dir, Region r, TokenList tl) {
        Parser parser = new Parser(r, dir);
        return  parser.parsePrefix(tl);
    }
    
    // give priority to smaller squares: this is a problem for nested border pluses (they need a square composed of all sub-squares).
    // otherwise the simpler first-occurrence operators would be pushed after the corresponding k-occurrence ones (k>1)
    public int h() { return 3*mismatch.getOrder()-this.lenOfMatchingBorder+k; }
    
    public String toString(Indenter ind) {
        return ind+"Operator "+getId()+":\t"+getClass().getName()+"^"+k+"\th="+h()+"\n"+
               ind+"Mismatch "+mismatch.getId()+":\t"+mismatch.toString()+"\n"+
               ind+"  Square:\t"+getSquare()+"\n";
    }
    
}
