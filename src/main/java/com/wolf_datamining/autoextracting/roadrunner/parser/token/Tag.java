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
 * Tag.java
 *
 * Created on December 25, 2003, 12:39 PM
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser.token;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.wolf_datamining.autoextracting.roadrunner.config.Preferences;


class Tag {
    
    private String element;
    private Map attributes;
    private int code;
    private int hashcode;
    
    /** Creates a new instance of Tag */
    Tag(String element, Map attributes) {
        this.element = element;
        this.attributes = attributes;
        this.code = 0;
        this.hashcode = computeHashCode();
    }
    
    public String getElement() {
        return this.element;
    }
    public Map getAttributes() {
        return this.attributes;
    }
    
    int code() {
        if (this.code==0) this.code = TagFactory.getInstance().getTagCode(this);
        return this.code;
    }
    
    public int hashCode() {
        return this.hashcode;
    }
    
    private int computeHashCode() {
        return this.element.hashCode()+this.attributes.keySet().hashCode();
    }
    
    public boolean equals(Object o) {
        if (this.hashCode()!=o.hashCode()) return false;
        Tag t = (Tag) o;
        return this.element.equals(t.element) && this.attributes.equals(t.attributes);
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer(getElement());
        Iterator attrsIt = getAttributes().entrySet().iterator();
        if (attrsIt.hasNext()) printAttribute(result,(Map.Entry)attrsIt.next());
        while (attrsIt.hasNext()) {
            printAttribute(result,(Map.Entry)attrsIt.next());
        }
        return result.toString();
    }
    
    private void printAttribute(StringBuffer result, Map.Entry entry) {
        result.append(" ");
        String name  = (String)entry.getKey();
        String value = (String)entry.getValue();
        result.append(name + (value!=null ? ":"+value : ""));
    }
}
