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
 * BidirectionalList.java
 * This class provides both left to right and right to left view
 * over an underlying java.util.List. This way, it overcomes the
 * limit of the standard interface which impose to the client programmers
 * a left to right view.
 *
 * Created on 30 gennaio 2003, 12.28
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.bidi;

import java.util.*;

/**
 */
public class BidirectionalListFactory {
    
    public static BidirectionalList newListView(Direction dir, final List l) {
        return (dir==Direction.RIGHT2LEFT ?
        (BidirectionalList)new ReverseList(l) :
            (BidirectionalList)new DirectList(l)  );
    }
    
    public static ListIterator newListIterator(List list, Direction dir) {
        BidirectionalList bidilist =  BidirectionalListFactory.newListView(dir,list);
        return bidilist.listIterator();
    }
    
    static private abstract class ListDelegator implements List {
        protected List list;
        protected ListDelegator(final List l) {
            if (l instanceof BidirectionalList)
                throw new RuntimeException("Cannot build a bidirectional view over a bidirectional view!");
            this.list = l;
        }
        public boolean add(Object o)               { return list.add(o); }
        public void add(int index, Object element) { list.add(index,element); }
        public boolean addAll(Collection c)        { return list.addAll(c); }
        public boolean addAll(int index, Collection c) { return list.addAll(index,c); }
        public void clear()                        { list.clear();}
        public boolean contains(Object o)          { return list.contains(o); }
        public boolean containsAll(Collection c)   { return list.containsAll(c); }
        public Object get(int index)              { return list.get(index); }
        public int indexOf(Object o)               { return list.indexOf(o); }
        public boolean isEmpty()           { return list.isEmpty(); }
        public Iterator iterator()         { return listIterator(); }
        public int lastIndexOf(Object o)   { return list.lastIndexOf(o); }
        public ListIterator listIterator() { return list.listIterator(); }
        public ListIterator listIterator(int index) { return list.listIterator(index); }
        public boolean remove(Object o) { return list.remove(o); }
        public Object remove(int index) { return list.remove(index); }
        public boolean removeAll(Collection c) { return list.removeAll(c); }
        public boolean retainAll(Collection c) { return list.retainAll(c); }
        public Object set(int index, Object element) { return list.set(index,element); }
        public int size()                            { return list.size();}
        public List subList(int fromPos, int toPos)  { return list.subList(fromPos,toPos); }
        public Object[] toArray()           { return list.toArray(); }
        public Object[] toArray(Object[] a) { return list.toArray(a); }
    }
    
    static private class DirectList extends ListDelegator implements BidirectionalList {
        private DirectList(List l) {
            super(l);
        }
        public ListIterator listIterator(Direction dir) {
            if (dir==Direction.LEFT2RIGHT) return listIterator();
            return listIterator(dir,list.size());
        }
        public ListIterator listIterator(Direction dir, int pos) {
            if (dir==Direction.LEFT2RIGHT) return listIterator(pos);
            ListIterator lit = list.listIterator(dir.indexBeforePos(pos));
            ReverseIterator rit = new ReverseIterator(lit);
            return rit;
        }
        public List subListFrom(int position) {
            return list.subList(position,list.size());
        }
        public List subListTo(int position) {
            return list.subList(0, position);
        }
        public List subListToIndex(int index) {
            return list.subList(0, index);
        }
        public Object getFirst() {
            return list.get(0);
        }
        public Object getLast() {
            return list.get(list.size()-1);
        }
    }
    
    static private class ReverseList extends ListDelegator implements BidirectionalList {
        private ReverseList(List l) {
            super(l);
        }
        public boolean add(Object o) {
            this.list.add(0,o);
            return true;
        }
        
        public void add(int pos, Object element)       { throw new UnsupportedOperationException(); }
        public boolean addAll(Collection c)            { throw new UnsupportedOperationException(); }
        public boolean addAll(int index, Collection c) { throw new UnsupportedOperationException(); }
        public Iterator iterator()         { return listIterator(); }
        public ListIterator listIterator() { return listIterator(Direction.RIGHT2LEFT); }
        
        public ListIterator listIterator(Direction dir) {
            if (dir==Direction.LEFT2RIGHT) return this.list.listIterator();
            return listIterator(dir, this.list.size());
        }
        
        public ListIterator listIterator(Direction dir, int pos) {
            if (dir==Direction.LEFT2RIGHT) return this.list.listIterator(pos);
            ListIterator lit = this.list.listIterator(dir.indexBeforePos(pos));
            ReverseIterator rit = new ReverseIterator(lit);
            return rit;
        }
        
        public ListIterator listIterator(int index) {
            ListIterator lit = this.list.listIterator(index+1);
            ReverseIterator rit = new ReverseIterator(lit);
            return rit;
        }
        
        public List subListFrom(int position) {
            return this.list.subList(0,position);
        }
        public List subListTo(int position) {
            return this.list.subList(position,this.list.size());
        }
        public List subListToIndex(int index) {
            return this.list.subList(index+1,this.list.size());
        }
        public Object getFirst() {
            return list.get(list.size()-1);
        }
        public Object getLast() {
            return list.get(0);
        }
        
    }
    
    static private class ReverseIterator implements ListIterator {
        private ListIterator lit;
        ReverseIterator(ListIterator lit) { this.lit = lit; }
        
        public void add(Object o)    { throw new UnsupportedOperationException(); }
        public boolean hasNext()     { return this.lit.hasPrevious(); }
        public boolean hasPrevious() { return this.lit.hasNext(); }
        public Object next()        { return this.lit.previous(); }
        public int nextIndex()      { return this.lit.previousIndex(); }
        public Object previous()    { return this.lit.next();  }
        public int previousIndex()  { return this.lit.nextIndex(); }
        public void remove()       { this.lit.remove(); }
        public void set(Object o)  { this.lit.set(o); }
    }
}
