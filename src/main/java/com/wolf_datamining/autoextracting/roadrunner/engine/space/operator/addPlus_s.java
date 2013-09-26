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
 * addPlus_s.java
 *
 * Created on 23 marzo 2003, 17.28
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space.operator;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Collapser;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Operator;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.SearchSpace;
import com.wolf_datamining.autoextracting.roadrunner.parser.MismatchPoint;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


class addPlus_s extends addPlus implements Operator {
    
    private static class Factory extends SquareOperatorFactory {

        private Factory(MismatchPoint m) {
            super(m);
        }
        
        Operator createOperator(int k) {
            return new addPlus_s(this.m, getSearcher(), getSquare(k), k);
        }
        
        Region getRegionToSearch() {
            return m.getMismatchingTokenlist();
        }
        
        Token getTagWanted() {
            if (m.getMatchingTokenlist().isEmpty()) return null;
            return m.getLastMatchingToken();
        }
        
        Region getSquare(int k) {
            return getSearcher().getRegionToOccurrence(k-1);
        }
        
    }
    
    static OperatorFactory getFactory(MismatchPoint m) {
        return new Factory(m);
    }
    
    /** Creates a new instance of addPlus_s */
    addPlus_s(MismatchPoint mismatch, Searcher searcher, Region square, int k) {
        super(mismatch,searcher,square,k);
    }
    
    Collapser getAfterCollapser(Expression csquare, SearchSpace space) {
        return new Collapser(csquare, new Expression(getRegionAfterSquare()).asRegion(), dir, space) {
            public int getPosCollapsedTo() {
                return mismatch.getPosAfterLastMatchingNode();
            }
        };
    }
    
}