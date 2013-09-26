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

     Questo  programma   software libero;  lecito redistribuirlo  o
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
 * SquareOperatorFactory.java
 *
 * Created on 20 marzo 2003, 11.48
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space.operator;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

import com.wolf_datamining.autoextracting.roadrunner.ast.Matchable;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Operator;
import com.wolf_datamining.autoextracting.roadrunner.parser.MismatchPoint;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


abstract class SquareOperatorFactory extends OperatorFactory {
    
    private Searcher searcher;

    SquareOperatorFactory(MismatchPoint m) {
        super(m);
    }
    
    Searcher getSearcher() { return this.searcher; }
    
    public List createOperators() {
        if (getRegionToSearch().isEmpty()) return Collections.EMPTY_LIST;
        if (getTagWanted()==null) return Collections.EMPTY_LIST;
        
        this.searcher = new Searcher(getRegionToSearch(),getTagWanted(),m.getDir());
        int nOfOcc = searcher.getNumberOfOccurrences();
        List result = new ArrayList(nOfOcc);
        for(int i=0; i<nOfOcc; i++) {
            Region square = getSquare(i+1);
            if (!isWellFormed(square)) break;
            result.add(createOperator(i+1));
        }
        return result;
    }
    
    abstract Operator createOperator(int k);
    
    abstract Region getRegionToSearch();
    abstract Token getTagWanted();
    abstract Region getSquare(int k);
    
    private boolean isWellFormed(Region region) {
        if (region.size()==0) return true; // empty squares are well-formed
        
        Stack stack = new Stack(region.size()); // a small local stack
        Iterator it = region.asList().iterator();
        while (it.hasNext()) {
            
            Object next = it.next();
            if (! (next instanceof Matchable)) continue;
            
            Matchable c = (Matchable)next;
            if (c.code()==0) continue; // text doesn't matter, consider only tags
            
            if (c.code()<0) {
                if (stack.size()==0 || stack.peek()+c.code()!=0) return false;
                else stack.pop();       // close tag scope
            }
            else stack.push(c.code()); // save an open tag
        }
        return (stack.size()==0);
    }
    
    private class Stack {
        
        int stack[];
        int head;
        
        Stack(int size) {
            this.head = 0;
            this.stack = new int[size];
        }
        
        void push(int code) { this.stack[head++]=code;}
        int pop()           {
            int result  = stack[--head];
            stack[head] = 0;
            return result;
        }
        int peek()          { return stack[head-1];   }
        int size()          { return head;            }
    }
    
}