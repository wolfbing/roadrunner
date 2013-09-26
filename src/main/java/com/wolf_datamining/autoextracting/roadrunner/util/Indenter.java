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

     Questo  programma �  software libero; �  lecito redistribuirlo  o
     modificarlo secondo i termini della Licenza Pubblica Generica GNU
     come � pubblicata dalla Free Software Foundation; o la versione 2
     della licenza o (a propria scelta) una versione successiva.

     Questo programma  � distribuito nella speranza che sia  utile, ma
     SENZA  ALCUNA GARANZIA;  senza neppure la  garanzia implicita  di
     NEGOZIABILIT�  o di  APPLICABILIT� PER  UN PARTICOLARE  SCOPO. Si
     veda la Licenza Pubblica Generica GNU per avere maggiori dettagli.

     Questo  programma deve  essere  distribuito assieme  ad una copia
     della Licenza Pubblica Generica GNU; in caso contrario, se ne pu�
     ottenere  una scrivendo  alla:

     Free  Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

*/
/*
 * Indenter.java
 * Created on 
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.util;

import java.util.Stack;

/**
 * 排版类
 * @author Admin
 *
 */
public class Indenter {
    
    final private static String INDENT_UNIT = "  ";
    
    public int indent;
    private String prefix;
    private Stack stack;
    private int index;
    private boolean numbering;
    
    public Indenter() {
        this(0,"",false);
    }
    public Indenter(int start) {
        this(start,"",false);
    }
    public Indenter(String prefix) {
        this(0,prefix, false);
    }
    public Indenter(boolean numbering) {
        this(0,"",numbering);
    }
    
    public Indenter(int ind, String prefix, boolean numbering) {
        this.indent = ind;
        this.prefix = prefix;
        this.stack  = new Stack();
        this.index  = 0;
        this.numbering = numbering;
    }
    
    private String indentString(int i) {
        StringBuffer result = new StringBuffer(i*INDENT_UNIT.length());
        for(int k=0; k<i; k++) {
            result.append(INDENT_UNIT);
        }
        return result.toString();
    }
    public String toString() {
        return prefix+indentString(this.indent)+ (numbering? (index++)+"." : "");
    }
    public void inc() {
        if (numbering) {
            this.stack.push(new Integer(index));
            this.index = 0;
        }
        ++this.indent;
    }
    public void dec() {
        if (numbering) {
            Integer integer = (Integer)this.stack.pop();
            this.index = integer.intValue();
        }
        --this.indent;
    }
    
}



