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

     Questo  programma software libero;  lecito redistribuirlo  o
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
 * PathArray.java
 *
 * Created on 
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.*;

public class PathArray implements Node.Path, Comparable {
    
    private int[] indices;
    private int depth;
    
    public PathArray(int[] indices) {
        this.indices = new int[indices.length];
        System.arraycopy(indices, 0, this.indices, 0, indices.length);
        this.depth = indices.length;
    }
    
    public int[] indices() {
        int result[] = new int[this.depth];
        System.arraycopy(this.indices, 0, result, 0, this.depth);
        return result;
    }
    
    public int lastIndex() {
        if (this.depth<1) throw new IllegalArgumentException("This path is empty!");
        return this.indices[this.depth-1];
    }
    
    public int depth() {
        return this.depth;
    }
    
    public Node.Path subpath(int depth) {
        //    depth < 0 means to remove -depth indices from path        
        int d = depth;
        if (Math.abs(depth)>this.depth)
            throw new IllegalArgumentException("A path of depth "+this.depth+" does not contains a subpath of depth "+depth);
        if (depth<0) d = this.depth+depth;
        int[] subindices = new int[d];
        System.arraycopy(this.indices, 0, subindices, 0, d);
        return new PathArray(subindices);
    }
    
    public Node.Path absolute(Node.Path relativePath) {
        // Make the absolute node path of the node reacheable
        // starting from this path by applying a relative path
        int thisIndices[] = this.indices();
        int relativeIndices[] = relativePath.indices();
        int absoluteIndices[] = new int[thisIndices.length+relativeIndices.length];
        System.arraycopy(thisIndices, 0, absoluteIndices, 0, thisIndices.length);
        System.arraycopy(relativeIndices, 0, absoluteIndices, thisIndices.length, relativeIndices.length);
        return new PathArray(absoluteIndices);
    }
    
    public boolean equals(Object o) {
        Node.Path p = (Node.Path)o;
        return Arrays.equals(this.indices(),p.indices());
    }
    
    public int hashCode() {
        int result = 0;
        for(int i=0; i<this.depth; i++)
            result+=this.indices[i];
        return result;
    }
    
    
    public int compareTo(Object o) {
        Node.Path p = (Node.Path)o;
        int min = Math.min(this.depth(), p.depth());
        int[]  i1 = this.indices();
        int[]  i2 = p.indices();
        for(int k=0; k<min; k++) {
            if (i1[k]>i2[k]) return +1;
            else if (i1[k]<i2[k]) return -1;
        }
        if (this.depth()>p.depth()) return +1;
        else if (this.depth()<p.depth()) return -1;
        return 0;
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer();
        for(int i=0; i<this.depth; i++) {
            result.append("."+this.indices[i]);
        }
        return result.toString();
    }
    
    
}
