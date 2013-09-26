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
 * CharacteristicSet.java
 *
 * Created on 21 ottobre 2003, 12.01
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.sampler;

import java.util.Set;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;


public class CharacteristicSet {
    
    private Region region;
    private Direction dir;
    private ListIterator resultsIterator;
    private CharacteristicSampler sampler;
    private boolean alreadySampled; // All samples of the set have been already computed
    private int minDOMdepth;
    
    private List samples;
    
    /** Creates a new instance of CharacteristicSet */
    public CharacteristicSet(Expression exp) {
        this(exp.asRegion());
    }
    public CharacteristicSet(Region region) {
        this(region, Direction.LEFT2RIGHT);
    }
    public CharacteristicSet(Region region, Direction dir) {
        this(region,-1, dir);
    }
    public CharacteristicSet(Region region, int minDepth, Direction dir) {
        this.dir = dir;
        this.region = region;
        this.minDOMdepth = minDOMdepth;
        this.alreadySampled = false;
        this.sampler = new CharacteristicSampler(region, dir);
        this.sampler.setMinDOMdepth(minDepth);
        this.samples = new LinkedList();
        this.resultsIterator = null;
    }
    public CharacteristicSet(Region region, Set characteristicSet) {
        this.region = region;
        this.samples = new LinkedList(characteristicSet);
        this.alreadySampled = true;
    }
    
    public Region getRegion() {
        return this.region;
    }
    
    /**
     *  Lazy evaluation of characteristic samples
     **/
    public ListIterator listIterator() {
        if (this.alreadySampled) return this.samples.listIterator();
        
        this.resultsIterator = this.samples.listIterator();
        //next ListIterator implements lazy evaluation
        return new ListIterator() {
            public Object next() {
                if (!resultsIterator.hasNext())
                    computeNextIfAny();
                return resultsIterator.next();
            }
            public boolean hasNext() {
                if (!resultsIterator.hasNext())
                    computeNextIfAny();
                return resultsIterator.hasNext();
            }
            public int nextIndex() {
                return resultsIterator.nextIndex();
            }
            public Object previous() {
                return resultsIterator.previous();
            }
            public boolean hasPrevious() {
                return resultsIterator.hasPrevious();
            }
            public int previousIndex() {
                return resultsIterator.previousIndex();
            }
            public void add(Object o) { throw new UnsupportedOperationException(); }            
            public void remove()      { throw new UnsupportedOperationException(); }
            public void set(Object o) { throw new UnsupportedOperationException(); }
        };
    }
    
    private void computeNextIfAny() {
        CharacteristicSample sample = this.sampler.computeNext();
        if (sample!=null) {
            this.resultsIterator.add(sample);
            this.resultsIterator.previous(); // move the implicit cursor before the new element
        }
    }
    
    public CharacteristicSample first() {
        return (CharacteristicSample)listIterator().next();
    }
}
