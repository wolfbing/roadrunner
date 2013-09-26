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

     Questo  programma  software libero;  lecito redistribuirlo  o
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
/**
 * Token.java
 * It results: 
 * code of  open tags > 0, close tags < 0 and simmetric
 * code of pcdata = 0,
 * code(<X>)=-code(</X>)
 * Created: Sat Jul  6 18:52:41 2002
 *
 * @author  Valter Crescenzi
 * @version
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import com.wolf_datamining.autoextracting.roadrunner.parser.Token;

public abstract class ASTToken extends NodeAdapter implements Token, Matchable, Node {
    
    protected int depth;
    protected int code;
    
    /* Token with code == 0 is associated with all pcdata */
    /** Token with depth implementation to be reused, not instantiated */
    public ASTToken(int depth, int code) {
        super(ASTConstants.JJTTOKEN);
        this.depth = depth;
        this.code = code;
    }
    
    /** Accept the visitor. **/
    public boolean jjtAccept(Visitor visitor) {
        return visitor.visit((ASTToken)this);
    }
    
    public Node clone(boolean deep) {
        //Flyweight pattern: these nodes are immutable
        return this; // these kind of leaves are shared
    }
    
    
    public int code() { return this.code; }
    
    public int depth() { return this.depth; }
    
    public boolean isPCDATA()  { return (this.code==0); }
    public boolean isTag()     { return (this.code!=0); }
    public boolean isStartTag() { return (this.code>0); }
    public boolean isEndTag()   { return (this.code<0); }
    
    
  /* two PCDATA match iff they have the same depth
     two TAGs match iff they have same element, atts, and depth */
    public boolean matches(Token that) {
        return this.code() == that.code() && this.depth() == that.depth();
    }
    
    public boolean maybeVariant() {
        // only start tags as possible variants
        return (this.isPCDATA()) || (this.isStartTag() && (this.isIMG() || this.isLink()));
    }
    
    public boolean equals(Object o) {
        if (this.hashCode()!=o.hashCode()) return false;
        if (!super.equals(o)) return false;
        ASTToken that  = (ASTToken)o;
        return this.code == that.code && this.depth == that.depth;
    }
    
    public int hashCode() {
        return 1000*depth+code;
    }
    
}
