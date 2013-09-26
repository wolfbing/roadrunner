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
 * Created on 9 marzo 2003, 10.35
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser.token;

import java.util.*;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.wolf_datamining.autoextracting.roadrunner.config.Constants;
import com.wolf_datamining.autoextracting.roadrunner.config.Preferences;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;



public class TokenFactory {
    
    final static private String A    = "a";
    final static private String HREF = "href";
    final static private String IMG  = "img";
    final static private String SRC  = "src";
    
    private static TokenFactory instance = null;
    
    public static TokenFactory getInstance() {
        if (instance==null) instance = new TokenFactory();
        return instance;
    }
    
    private Map tagToken2DOMElement; //useful for display ASTSubtree contents
    
    private TokenFactory() {
        this.tagToken2DOMElement = new IdentityHashMap();
    }
    
    public int getNumberOfKnownTokens() {
        return 2*TagFactory.getInstance().getNumberOfKnownTags()+1;
    }
    
    public TagToken createOpenTagToken(Node node, int depth, Preferences p) {
        String element = getTagElement(node);
        Map attrs = getTagAttributes(node);
        Map processedAttrs = processAttributesByPrefs(getTagAttributes(node), p);
        Tag tag = TagFactory.getInstance().createTag(element, processedAttrs);
        String href = (String)attrs.get(HREF);
        TagToken result = null;
        if (href!=null) result = new LinkToken(href,tag,tag.code(),depth);
        else {
            String src  = (String)attrs.get(SRC);
            if (src!=null)  result = new ImageToken(src,tag,tag.code(),depth);
            else result = new TagToken(tag, tag.code(), depth);
        }
        this.tagToken2DOMElement.put(result,node);
        return result;
    }
    
    public TagToken createOpenTagToken(String element, Map attrs, int depth) {
        Tag tag = TagFactory.getInstance().createTag(element, attrs);
       
        return new TagToken(tag,tag.code(),depth);
    }
    
    public TagToken createCloseTagToken(String element, Map attrs, int depth) {
        Tag tag = TagFactory.getInstance().createTag(element,attrs);
        return new TagToken(tag,-tag.code(),depth);
    }
    
    public TagToken createCloseTagToken(Token token) {
        return createCloseTagToken(token.getElement(), token.getAttributes(), token.depth());
    }
    
    public TextToken createTextToken(Node text,int depth) {
        return new TextToken(getText(text),depth);
    }
    
    public TextToken createTextToken(String text,int depth) {
        return new TextToken(text,depth);
    }
    
    public Token createNullToken(Token sample) {
        return new NullToken(sample);
    }
    
    public Token createNullTextToken(int depth) {
        return new NullToken(depth);
    }
    
    public Element getDOMElement(Token token) {
        return (Element)this.tagToken2DOMElement.get(token);
    }
    
    private String getText(Node node) {
        return node.getNodeValue().trim();
    }
    
    private String getTagElement(Node node) {
        return node.getNodeName().toLowerCase().trim();
    }
    
    private Map getTagAttributes(Node node) {
        /* get tag attributes considering configuration */
        NamedNodeMap nnm = node.getAttributes();
        if (nnm==null) return Collections.EMPTY_MAP;
        Map attributes = new HashMap();
        // N.B. Attributes with a default value cannot be discarded directly on DOM node
        for(int i=0; i<nnm.getLength(); i++) {
            String attName = nnm.item(i).getNodeName().toLowerCase().trim();
            String attValue = nnm.item(i).getNodeValue();
            attributes.put(attName,attValue);
        }
        return attributes;
    }
    private Map processAttributesByPrefs(Map attributes, Preferences options) {
        Map result = new HashMap();
        // attribute names to skip
        Set namesOfAttributeToIgnore  = options.getSet(Constants.IGNORE_ATTS);
        // attribute names whose value is relevant
        Set namesOfAttributeWithValue = options.getSet(Constants.ATTS_VALUES);
        // complement is true if attributeValue property indicate only the names of attr. whose values should be discarded
        boolean complement = namesOfAttributeWithValue.contains(Preferences.NEG);
        Iterator it = attributes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String key = (String)entry.getKey();
            String name = key.toLowerCase().trim();
            String value = (String)entry.getValue();
            if (namesOfAttributeToIgnore.contains(name)) continue;
            if (namesOfAttributeWithValue.contains(name))
                result.put(name, (complement ? null : value ));
            else result.put(name, (complement ? value : null ));
        }
        return result;
    }
    
}
