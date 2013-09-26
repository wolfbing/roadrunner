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
package com.wolf_datamining.autoextracting.roadrunner.engine.space.operator;

/**
 * Searcher.java
 *
 *
 * Created: Sun Nov 21 11:02:08 1999
 *
 * @author Valter Crescenzi
 * @version
 */

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Matchable;
import com.wolf_datamining.autoextracting.roadrunner.ast.Node;
import com.wolf_datamining.autoextracting.roadrunner.ast.PrefixHunter;
import com.wolf_datamining.autoextracting.roadrunner.bidi.*;
import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.config.Constants;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


class Searcher {
    
    private BidirectionalList listView;
    private Region where;
    private Token wanted;
    private Direction dir;
    
    private List occurrencesIndices;
    private int numberOfOccurrences;
    
    
    Searcher(Region where, Token wanted, Direction dir) {
        this.listView = BidirectionalListFactory.newListView(dir, where.asList());
        this.where = where;
        this.wanted = wanted;
        this.dir = dir;
        this.occurrencesIndices =  new ArrayList(Math.min(getMaxOccurrencesAllowed(),5));
        findOccurrences();
    }
    

    private int getMaxOccurrencesAllowed() {
        int maxOcc = Config.getPrefs().getInt(Constants.MAX_NUM_OCCUR);;
        return (maxOcc>0 ? maxOcc : Integer.MAX_VALUE);
    }
    
    // Evitare di restituire regioni vuote per quanto detto di seguito:
    // skip the mismatch but leave it in the square
    // otherwise mismatches on aligning token could give empty squareTuple
    
    private void findOccurrences() {
        this.numberOfOccurrences = 0;
        int max = getMaxOccurrencesAllowed();
        
        ListIterator lit = this.listView.listIterator(dir);
        while (lit.hasNext() && this.numberOfOccurrences < max) {
            Object next = PrefixHunter.getFirstLeaf(dir, (Node)lit.next());
            
            if (!(next instanceof Matchable)) continue;
            
            Matchable cursor = (Matchable)next;
            
            if (cursor.depth()<this.wanted.depth()) break;  // too late: the "Matchable" wanted is deeper
            
            if (cursor.matches(this.wanted)) {
                this.occurrencesIndices.add(new Integer(lit.previousIndex()));
                this.numberOfOccurrences++;
            }
        }
    }
    
    
    int getNumberOfOccurrences() {
        return this.numberOfOccurrences;
    }
    
    Region getRegionToOccurrence(int noccurrence) {
        // we want the  occurrence to be in the square; see addPlus
        return dir.subRegionToNIndex(where, getOccurrenceIndex(noccurrence));
    }
    Region getRegionBeforeOccurrence(int noccurrence) {
        // we don't want the occurrence to be in the square; see addHook
        return dir.subRegionBeforeNIndex(where, getOccurrenceIndex(noccurrence));
    }
    Region getRegionAfterOccurrence(int noccurrence) {
        return dir.subRegionAfterNIndex(where, getOccurrenceIndex(noccurrence));
    }
    Region getRegionFromOccurrence(int noccurrence) {
        // used to avoid contiguous hooks othewise introduced by addHook_w
        return dir.subRegionFromNIndex(where, getOccurrenceIndex(noccurrence));
    }
    
    private int getOccurrenceIndex(int noccurrence) {
        return ((Integer)(this.occurrencesIndices.get(noccurrence))).intValue();
    }
    
    
} // Searcher
