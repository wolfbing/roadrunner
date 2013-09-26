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

     Questo  programma software libero;   lecito redistribuirlo  o
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
 * FreeTextHunter.java
 *
 * Created on
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.parser;

import java.util.Set;
import java.util.HashSet;
import java.util.logging.*;

import org.w3c.dom.*;

import com.wolf_datamining.autoextracting.roadrunner.config.Constants;
import com.wolf_datamining.autoextracting.roadrunner.config.Preferences;
import com.wolf_datamining.autoextracting.roadrunner.labeller.Box;
import com.wolf_datamining.autoextracting.roadrunner.util.DOMLoader;



/** Hunts for free-text chunks
 * A free-text chunck includes a list of sibling sub-trees, at least
 * one of which is either a PCDATA or one of variant tags */

public class FreeTextHunter implements Constants {
    
    static private Logger log = Logger.getLogger(FreeTextHunter.class.getName());
    
    private Document document; //input DOM document
    private Document result;   //output DOM document
    private Preferences prefs; //preferences to config freetext collapsing
    private Set boxes;         //boxes detected during freetext collapsing
    
    private boolean keepTags;
    
    public FreeTextHunter(Document document, Preferences prefs) {
        this.document = document;
        this.prefs = prefs;
        this.keepTags = this.prefs.getBoolean(KEEP_TAGS);
    }
    
    public Document getCollapsedDocument() {
        String namespace = this.document.getNamespaceURI();     
        this.result = this.document.getImplementation().createDocument(namespace,null,null);
        build(this.document.getDocumentElement(), this.result);
        this.result.normalize();
        return this.result;
    }
    
    // Build another DOM with just one text node for each chunk of freetext
    void build(Node input, Node output) {
        if (input==null) return;
        
        if (isFreeTextChunk(input)) {
            // collapse sibling freetext chunks and eventually compute a containing bb
            StringBuffer freetext = new StringBuffer();
            Set bboxes = new HashSet();
            Node node = input;
            do  {
                collapseFreetext(node, freetext, bboxes);
                node = node.getNextSibling();
            } while (node!=null && isFreeTextChunk(node));
            Box containingBox = Box.getContainingBox(bboxes);
            if (containingBox!=null) {
                // encode bb as comment
                output.appendChild(this.result.createComment(Box.encode(containingBox)));
            }
            // freetext chunk in a new freetext node
            output.appendChild(this.result.createTextNode(freetext.toString()));
            // then visit next sibling
            build(node,output);
        }
        else {
            Node clone = this.result.importNode(input,false);
            output.appendChild(clone);
            // first visit childs
            build(input.getFirstChild(), clone);
            // then visit siblings
            build(input.getNextSibling(),output);
        }
    }
    
    private boolean isFreeTextChunk(Node node) {
        int type = node.getNodeType();
        switch (type) {
            case Node.TEXT_NODE:
                return true;
            case Node.COMMENT_NODE:
                return true;  // comments do not prevent from collapsing freetext
            case Node.ELEMENT_NODE:
                NodeList children = node.getChildNodes();
                if (children.getLength()==0) {
                    return isVariantTag(node); /* leaf tag */
                }
                else if (!isPresentationTag(node)) {
                    return false;
                }
                //it is one of freetext tags; consider it  a chunk
                //of freetext iff all children are freetext chunks
                else return allChildrenAreFreeTextChunks(node);
        }
        return false; //all other types of DOM node types stop freetext collapsing
    }
    
    private boolean allChildrenAreFreeTextChunks(Node node) {
        NodeList children = node.getChildNodes();
        int len = children.getLength();
        for (int i=0; i<len; i++) {
            if (!isFreeTextChunk(children.item(i)))
                return false;
        }
        return true;
    }
    
    /** One string for one subtree, recursively. */
    private void collapseFreetext(Node node, StringBuffer freetext, Set boxes) {
        int type = node.getNodeType();
        switch ( type ) {
            case Node.TEXT_NODE:
                freetext.append(node.getNodeValue());
                break;
            case Node.COMMENT_NODE:
                Comment comment = (Comment)node;
                if (Box.isBoundingBoxEncoding(comment.getNodeValue()))
                    boxes.add(Box.decode(comment.getNodeValue()));
                break;
            case Node.ELEMENT_NODE:
                String element = node.getNodeName();
                NodeList children = node.getChildNodes();
                int len = children.getLength();
                if (len>0) {
                    freetext.append(openTag(element));
                    for (int i=0; i<len; i++) {
                        collapseFreetext(children.item(i),freetext,boxes);
                    }
                    freetext.append(closeTag(element));
                }
                else freetext.append(leafTag(element));
                break;
        }
    }
    
    private String openTag(String element) {
        if (!keepTagsEnabled()) return " ";
        return '<'+element+'>';
    }
    private String closeTag(String element) {
        if (!keepTagsEnabled()) return " ";
        return "</"+element+'>';
    }
    private String leafTag(String element) {
        if (!keepTagsEnabled()) return " ";
        return '<'+element+"/>";
    }
    
    private boolean isPresentationTag(Node node) {
        int type = node.getNodeType();
        if (type!=Node.ELEMENT_NODE) return false;
        Set presTags = this.prefs.getSet(FREETEXT_TAGS);
        return presTags.contains(node.getNodeName().toLowerCase());
    }
    
    private boolean isVariantTag(Node node) {
        int type = node.getNodeType();
        if (type!=Node.ELEMENT_NODE) return false;
        Set varTags = this.prefs.getSet(VARIANT_TAGS);
        return varTags.contains(node.getNodeName().toLowerCase());
    }
    
    private boolean keepTagsEnabled() {
        return this.keepTags;
    }
    
}
