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
 * NullToken.java
 *
 * Created on December 25, 2003, 12:16 PM
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser.token;

import java.util.Map;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTToken;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


class NullToken extends ASTToken implements Token {
    
    private boolean is_pcdata,is_img,is_link;
    
    /** Creates a new instance of NullToken */
    NullToken(Token prototype) {
        super(prototype.depth(),prototype.code());
        this.is_pcdata = prototype.isPCDATA();
        this.is_img = prototype.isIMG();
        this.is_link = prototype.isLink();
    }
    NullToken(int depth) {
        super(depth,0);
        this.is_pcdata = true;
        this.is_img = false;
        this.is_link = false;
    }
    
    public Map getAttributes() {  return null;   }
    public String getElement() {  return null;   }
    public String getText()    {  return null;   }
    public String getVariantValue() { return null; }
    public boolean isPCDATA() { return is_pcdata; }
    public boolean isIMG()    { return is_img; }
    public boolean isLink()   { return is_link;}
}
