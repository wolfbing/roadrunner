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
 * TokenFactory.java
 *
 * Created on 5 febbraio 2003, 12.19
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser.token;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;


class TagFactory {
    // Flyweight Pattern for tag = element + attributes
    
    // Method equals() is used to compute a tag code which depends only on element and attribute *names*, not values
    // However, attribute values are kept since we need them to extract link and images. Therefore 2 tags are mapped
    // to the same object only if they have same, element, attribute names, and attribute values.
    // There may exist 2 different Tag object with the same tag code (it means that they differ for attribute values)
    
    private static TagFactory instance;
    
    static TagFactory getInstance() {
        if (instance==null) instance = new TagFactory();
        return instance;
    }
    
    private int lastcode;
    // first code2tag element is null because it corresponds to code 0 (PCDATA)
    private Map tag2code; // Map: Tag -> code
    
    private static Map element2attributes2tag; // Map:  (String ->  Map:  (Map -> Tag))
    
    private TagFactory() {
        this.lastcode = 0;
        this.tag2code = new HashMap();
        this.element2attributes2tag = new HashMap();
    }
    
    int getNumberOfKnownTags() {
        return lastcode;
    }
    
    Tag createTag(String el, Map attrs) {
        return getFlyweightTag(el, attrs);
    }
    
    private Tag getFlyweightTag(String el, Map attrs) {
        Map attributes2tag = (Map)element2attributes2tag.get(el);
//        Set keyset = attributes2tag.keySet();
//        if ( attributes2tag.containsKey(attrs) )
//        {
//        	System.out.println("have"+attrs);
//        }
        if (attributes2tag==null) return createNewFlyweightTagFromUnknownElement(el,attrs);
        
        Tag flyweight = (Tag)attributes2tag.get(attrs);
        if (flyweight==null) return createNewFlyweightTagFromUnknownAttributes(el,attrs);
        return flyweight; //Recycle objects
    }
    
    private Tag createNewFlyweightTagFromUnknownElement(String element, Map attributes)  {
        Integer code = new Integer(++lastcode);
        Tag result = new Tag(element, attributes);
        Map attributes2tag = new HashMap();
        attributes2tag.put(attributes, result);
        element2attributes2tag.put(element, attributes2tag);
        tag2code.put(result, code);
        return result;
    }
    
    private Tag createNewFlyweightTagFromUnknownAttributes(String element, Map attributes)  {
        Tag result = new Tag(element, attributes);
        Map attributes2tag = (Map)element2attributes2tag.get(element);
        attributes2tag.put(attributes, result);
        // Check whether a code for this tag already exists or not
        Integer known = (Integer)tag2code.get(result);
        if (known==null) { // need new code
            Integer code = new Integer(++lastcode);
            tag2code.put(result, code);
        }
        return result;
    }
    
    int getTagCode(Tag tag) {
        return ((Integer)tag2code.get(tag)).intValue();
    }
}
