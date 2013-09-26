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
 * Lexer.java
 *
 * Created on
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.parser;

import java.util.*;
import java.util.logging.*;

import java.io.Reader;
import java.io.IOException;


import org.w3c.dom.Node;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.config.Constants;
import com.wolf_datamining.autoextracting.roadrunner.config.Preferences;
import com.wolf_datamining.autoextracting.roadrunner.labeller.Box;
import com.wolf_datamining.autoextracting.roadrunner.parser.token.TokenFactory;
import com.wolf_datamining.autoextracting.roadrunner.util.DOMLoader;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;

public class Lexer implements Constants {
    
    static private Logger log = Logger.getLogger(Lexer.class.getName());
    
    private TokenFactory factory;
    private List tokens;
    private Reader in;
    private Preferences prefs;
    //labelling stuff
    private boolean labelling;
    private IdentityHashMap token2bb;
    private Box box;
    
    public Lexer(Reader in, Preferences prefs) {
        this.factory = TokenFactory.getInstance();
        this.in = in;
        this.prefs = prefs;
        this.tokens = null;
        this.labelling = Config.getPrefs().getBoolean(VISUAL_ENABLED);
        this.token2bb = new IdentityHashMap();
        this.box = null;
    }
    
    public List getTokens() throws SAXException, IOException  {
        this.tokens = new ArrayList();
        Document doc = DOMLoader.parseHTML(this.in);
        if (freetextEnabled()) {
            doc = new FreeTextHunter(doc, this.prefs).getCollapsedDocument();
        }
        getTokens(doc,0);
        return this.tokens;
    }
    
    public IdentityHashMap getTokens2BoundingBoxes() {
        return this.token2bb;
    }
    
    /* DOM Document -> Token List */
    private void getTokens(Node node,int depth) {
        int type = node.getNodeType();
        switch ( type ) {
            
            case Node.DOCUMENT_NODE:
                Document document = (Document)node;
                getTokens(document.getDocumentElement(),depth);
                break;
                
            case Node.ELEMENT_NODE:
                if (isSubtreeToSkip(node)) break;
                
                boolean toSkip = isTagToSkip(node);
                
                Token token = null;
                
                /* open tag */
                if (!toSkip) {
                    token = (Token)factory.createOpenTagToken(node,depth,this.prefs);
                    this.tokens.add(token);
                }
                /* visit subtrees */
                NodeList children = node.getChildNodes();
                if ( children != null ) {
                    int len = children.getLength();
                    for ( int i = 0; i < len; i++ ) {
                        getTokens(children.item(i), toSkip ? depth : depth+1);
                    }
                }
                /* close tag */
                if (!toSkip) {
                    this.tokens.add(factory.createCloseTagToken(token));
                }
                break;
                
            case Node.TEXT_NODE:
                // skip strings of all white spaces
                if (!Util.isAllWhiteSpaceChars(getText(node))) {
                    Token text = factory.createTextToken(node,depth);
                    this.tokens.add(text);
                    if (isVisualLabellingEnabled()) {
                        saveBoundingBoxOf(text);
                    }
                }
                break;
            case Node.COMMENT_NODE:
                String comment = node.getNodeValue();;
                
                if (Box.isBoundingBoxEncoding(comment) && isVisualLabellingEnabled()) {
                    this.box = Box.decode(comment); //Save parsed Box
                }
                break;
                /* skip anything else */
        }
    }
    
    private boolean isTagToSkip(Node element) {
        return this.prefs.getSet(IGNORE_TAGS).contains(getTagElement(element));
    }
    
    private boolean isSubtreeToSkip(Node element) {
        return this.prefs.getSet(IGNORE_TREES).contains(getTagElement(element));
    }
    
    private String getText(Node node) {
        return node.getNodeValue().trim();
    }
    
    private String getTagElement(Node node) {
        return ((Element)node).getNodeName().toLowerCase().trim();
    }
    
    private boolean freetextEnabled() {
        return this.prefs.getBoolean(FREE_TEXT);
    }
    
    private boolean isVisualLabellingEnabled() {
        return labelling;
    }
    
    private void saveBoundingBoxOf(Token token) {
        log.finest("Box of \""+token+ "\" is "+this.box);
        this.token2bb.put(token, this.box);
        this.box = null;
    }
    
    
}
