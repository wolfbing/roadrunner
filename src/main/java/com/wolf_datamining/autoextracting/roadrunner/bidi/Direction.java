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
 * Direction.java
 * An index is an integer which refers to an element of a sequence and
 *  a position refers to a "space" between elements of a sequence.
 *  Consider a list of N elements: valid indices are 0,1...N-1; valid positions are 0,1...N;
 *  0 is the position immediately before the first element, and N is the position immediately
 *  after the last one. The (i+1)-th element of the sequence has index i and is located between
 *  position i and i+1 
 * Created on 30 gennaio 2003, 15.52
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.bidi;

public interface Direction {
    
    public int unit() ;
    
    public Direction uTurn();
    
    public Object getFirst(Region region);
    public Object getLast(Region region);
    
    public int posBeforeIndex(int i);
    public int posAfterIndex(int i);
    
    public int indexBeforePos(int p);
    public int indexAfterPos(int p);
    /* positions are absolute within an expression */
    public int getFirstPos(Region region);
    public int getLastPos(Region region);
    public Region subRegion(Region region, int fPos, int lPos);
    public Region subRegionAfter(Region region, int pos);
    public Region subRegionBefore(Region region, int pos);
    
    /* indices are relative to subregions */
    public Region subRegionToNIndex(Region region, int n);
    public Region subRegionBeforeNIndex(Region region, int n);
    public Region subRegionAfterNIndex(Region region, int n);
    public Region subRegionFromNIndex(Region region, int n);
    
    public Region subRegionFinal(Region region, int size);
    public Region subRegionInitial(Region region, int size);
    
    // Direction ->
    final static public Direction LEFT2RIGHT = new Direction() {
        final public int unit()               { return 1;   }
        final public Direction uTurn()        { return Direction.RIGHT2LEFT; }
        final public Object getFirst(Region r) { return r.getFirst(); }
        final public Object getLast(Region r)  { return r.getLast();  }
        final public int posBeforeIndex(int i) { return i;   }
        final public int posAfterIndex(int i)  { return i+1; }
        
        final public int indexBeforePos(int p) { return p-1; }
        final public int indexAfterPos(int p)  { return p;   }
        
        final public int getFirstPos(Region region) { return region.getLeftBorderPos();  }
        final public int getLastPos(Region region)  { return region.getRightBorderPos(); }
        
        final public Region subRegion(Region r, int fp, int lp) {
            return r.subRegion(fp,lp);
        }
        final public Region subRegionAfter(Region r, int pos) {
            return r.subRegion(pos, r.getRightBorderPos());
        }
        public Region subRegionBefore(Region r, int pos) {
            return r.subRegion(r.getLeftBorderPos(),pos);
        }
        
        final public Region subRegionToNIndex(Region r, int i) {
            return r.subRegion(r.getLeftBorderPos(), r.getLeftBorderPos()+i+1);
        }
        
        final public Region subRegionBeforeNIndex(Region r, int i) {
            return r.subRegion(r.getLeftBorderPos(), r.getLeftBorderPos()+i);
        }
        
        final public Region subRegionAfterNIndex(Region r, int i) {
            return r.subRegion(r.getLeftBorderPos()+i+1, r.getRightBorderPos());
        }
        
        final public Region subRegionFromNIndex(Region r, int i) {
            return r.subRegion(r.getLeftBorderPos()+i, r.getRightBorderPos());
        }
        
        final public Region subRegionFinal(Region r, int size) {
            return r.subRegion(r.getRightBorderPos()-size, r.getRightBorderPos());
        }
        final public Region subRegionInitial(Region r, int size) {
            return r.subRegion(r.getLeftBorderPos(), r.getLeftBorderPos()+size);
        }
        
        public String toString() { return "->"; }
    };
    
    // Direction <-
    final static public Direction RIGHT2LEFT = new Direction() {
        final public int unit()               { return -1;  }
        final public Direction uTurn()        { return Direction.LEFT2RIGHT; }
        final public Object getFirst(Region r) { return r.getLast();  }
        final public Object getLast(Region r)  { return r.getFirst(); }
        final public int posBeforeIndex(int i) { return i+1; }
        final public int posAfterIndex(int i)  { return i;   }
        
        final public int indexBeforePos(int p) { return p;   }
        final public int indexAfterPos(int p)  { return p-1; }
        
        final public int getFirstPos(Region region) { return region.getRightBorderPos(); }
        final public int getLastPos(Region region)  { return region.getLeftBorderPos();  }
        
        final public Region subRegion(Region r, int fp, int lp) {
            return r.subRegion(lp,fp);
        }
        final public Region subRegionAfter(Region r, int pos) {
            return r.subRegion(r.getLeftBorderPos(),pos);
        }
        public Region subRegionBefore(Region r, int pos) {
            return r.subRegion(pos, r.getRightBorderPos());
        }
        final public Region subRegionToNIndex(Region r, int i) {
            return r.subRegion(r.getLeftBorderPos()+i, r.getRightBorderPos());
        }
        
        final public Region subRegionBeforeNIndex(Region r, int i) {
            return r.subRegion(r.getLeftBorderPos()+i+1, r.getRightBorderPos());
        }
        
        final public Region subRegionAfterNIndex(Region r, int i) {
            return r.subRegion(r.getLeftBorderPos() , r.getLeftBorderPos()+i);
        }
        
        final public Region subRegionFromNIndex(Region r, int i) {
            return r.subRegion(r.getLeftBorderPos(), r.getLeftBorderPos()+i+1);
        }
        
        final public Region subRegionFinal(Region r, int size) {
            return r.subRegion(r.getLeftBorderPos(), r.getLeftBorderPos()+size);
        }
        final public Region subRegionInitial(Region r, int size) {
            return r.subRegion(r.getRightBorderPos()-size, r.getRightBorderPos());
        }
        
        public String toString() { return "<-"; }
    };
    
}
