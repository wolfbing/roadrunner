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
 * Region.java
 *
 *An abstract class to save regions of structures such as AST or TokenList.
 * A region object does not depend on the specific AST or TokenList instance 
 * object and can be used to mark regions over clones of the same structure.
 *
 * Created on 25 gennaio 2003, 12.01
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.bidi;

import java.util.List;
import java.util.Iterator;

public abstract class Region {

    private int lborder;    // A couple of integers which delimit the region itself.
    private int rborder;
    
    protected Region(int left, int right) {
        this.lborder = left;
        this.rborder = right;
    }
    
    public Object getFirst() {
        return asList().get(0);
    }

    public Object getLast() {
        return asList().get(size()-1);
    }
    
    public int getLeftBorderPos() {
        return this.lborder;
    }
    
    public int getRightBorderPos() {
        return this.rborder;
    }
    
    public int size() {
        return this.rborder - this.lborder;
    }
    
    public boolean isEmpty() {
        return this.size()==0;
    }
    
    abstract public List asList();
    
    abstract public Region subRegion(int l, int r);
    
    protected static void checkBorders(int size, int left, int right) {
        if (left<0 || right>size || left>right)
            throw new IndexOutOfBoundsException("Bad region delimiters: ("+left+","+right+")"+
            " out of "+size+" elements");
    }
    
    public String toString() {
        return toString(getLeftBorderPos());
    }
    
    public String toString(int fromIndex) {
        StringBuffer result = new StringBuffer(size()*10);
        int index = fromIndex;
        Iterator it = asList().iterator();
        if (it.hasNext()) result.append(index+"."+it.next());
        while (it.hasNext()) {
            index++;
            result.append(", "+index+"."+it.next());
        }
        return result.toString();
    }
    
}
