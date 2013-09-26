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
/**
 * LinkToken.java
 *
 * Created on February 29, 2004, 20:40 PM
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser.token;


import java.io.File;
import java.util.Map;

import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


class LinkToken extends TagToken implements Token {
        
    private String href;
    
    /** Creates a new instance of TagToken */
    LinkToken(String destUrl, Tag tag, int code, int depth) {
        super(tag, code, depth);
        this.href = destUrl;
    }
    
    public String getText() {
        throw new RuntimeException("This object is supposed to be a Link Token!");
    }
    
    public boolean isIMG()    { return false; }
    public String getIMGsrc() {
        throw new RuntimeException("This object is supposed to be a Link Token!");
    }
    public boolean isLink()   { return true;  }
    private String getLinkTarget() {
        return this.href;
    }
    
    public String getVariantValue() {
        return this.getLinkTarget();
    }
    
}
