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
/**
 * TagToken.java
 *
 * Created on December 25, 2003, 12:24 PM
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser.token;


import java.io.File;
import java.util.Map;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTToken;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


class TagToken extends ASTToken implements Token {
    
    private Tag tag;
        
    /** Creates a new instance of TagToken */
    TagToken(Tag tag, int code, int depth) {
        super(depth,code);
        this.tag = tag;
    }
    
    public String getText() { throw new RuntimeException("This object is supposed to be a tag Token!"); }
    public String getElement() { return this.tag.getElement(); }
    public Map getAttributes() { return this.tag.getAttributes(); }
    
    public boolean isIMG()  { return false; }
    public boolean isLink() { return false; }
    
    public String getVariantValue() {
        throw new RuntimeException("This is not a variant!");
    }
    
    /* N.B. equals() and hashCode() must conform to the semantics of ASTToken equals() and hashCode() */
    public String toString()     { return "<"+(isStartTag()?"":"/")+this.tag+">"; }
    
}
