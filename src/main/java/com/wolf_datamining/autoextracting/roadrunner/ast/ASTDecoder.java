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

     Questo  programma  software libero; lecito redistribuirlo  o
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
 * ASTDecoder.java
 * Created: 
 *
 * @author  Valter Crescenzi
 * @version
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.*;
import java.util.logging.*;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import com.wolf_datamining.autoextracting.roadrunner.config.Preferences;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.token.TokenFactory;


public class ASTDecoder implements ASTConstants {
    
    static private Logger log = Logger.getLogger(ASTDecoder.class.getName());
    
    private ASTBuilder builder;
    private Map node2action;
    private Document root;
    private TokenFactory factory;
    
    public ASTDecoder(org.w3c.dom.Document root) {
        this.root = root;
        this.builder = new ASTBuilder();
        node2action = new HashMap();
        this.factory = TokenFactory.getInstance();
        installNodeProcessors();
    }
    
    public Expression decode() {
        // Start the decoding  process...
        Element rootElement = (Element)this.root.getElementsByTagName(EXPRESSION).item(0);
        DOMNodeProcessor processor = (DOMNodeProcessor)node2action.get(EXPRESSION);
       
        processor.decodeTree(rootElement);
        Expression ex = new Expression((ASTAnd)this.builder.getRoot());
        return new Expression((ASTAnd)this.builder.getRoot());
    }
    
    private abstract class DOMNodeProcessor {
        void decodeTree(org.w3c.dom.Node node) {
            com.wolf_datamining.autoextracting.roadrunner.ast.Node converted = decodeNode(node);
            builder.addNode(converted);
            builder.startNodeScope(converted);
            visitChildren(node);
            builder.endNodeScope(converted);
        }
        com.wolf_datamining.autoextracting.roadrunner.ast.Node decodeNode(org.w3c.dom.Node node) {
            throw new AbstractMethodError(this.getClass().toString());
        }
        protected void visitChildren(org.w3c.dom.Node node) {
            NodeList children = node.getChildNodes();
            int len = children.getLength();
            for ( int i = 0; i < len; i++ ) {
                Node kid = children.item(i);
                if (kid.getNodeType()!=Document.ELEMENT_NODE)
                    continue; //skip indenting text nodes
                DOMNodeProcessor processor = (DOMNodeProcessor)node2action.get(kid.getNodeName());
                processor.decodeTree(kid);
            }
        }
        
    };
    
    private void installNodeProcessors() {
        
        node2action.put(EXPRESSION, new DOMNodeProcessor() {
            void decodeTree(org.w3c.dom.Node node) {
                visitChildren(node);
            }
        });
        
        node2action.put(AND, new DOMNodeProcessor() {
            com.wolf_datamining.autoextracting.roadrunner.ast.Node decodeNode(org.w3c.dom.Node node) {
                return builder.createAnd();
            }
        });
        
        node2action.put(HOOK, new DOMNodeProcessor() {
            com.wolf_datamining.autoextracting.roadrunner.ast.Node decodeNode(org.w3c.dom.Node node) {
                return builder.createHook();
            }
        });
        
        node2action.put(PLUS, new DOMNodeProcessor() {
            com.wolf_datamining.autoextracting.roadrunner.ast.Node decodeNode(org.w3c.dom.Node node) {
                return builder.createPlus();
            }
        });
        
        node2action.put(SUBTREE, new DOMNodeProcessor() {
            void decodeTree(org.w3c.dom.Node node) {
                builder.addNode(builder.createSubtree(decodeTag(node.getFirstChild())));
            }
        });
        
        node2action.put(TAG, new DOMNodeProcessor() {
            void decodeTree(org.w3c.dom.Node node) {
                builder.addNode(builder.createTokenNode(decodeTag(node)));
            }
        });
        
        node2action.put(PCDATA, new DOMNodeProcessor() {
            void decodeTree(org.w3c.dom.Node node) {
                builder.addNode(builder.createTokenNode(decodeText(node)));
            }
        });
        
        node2action.put(VARIANT, new DOMNodeProcessor() {
            void decodeTree(org.w3c.dom.Node node) {
                builder.addNode(builder.createVariant(decodeToken(node.getFirstChild()), getLabel(node)));
            }
        });
        
    }
    
    private String getLabel(org.w3c.dom.Node node) {
        return ((Element)node).getAttribute(ASTConstants.LABEL);
    }
    
    private Token decodeToken(org.w3c.dom.Node node) {
        if (node.getNodeName().equals(PCDATA))
            return decodeText(node);
        else if (node.getNodeName().equals(TAG))
            return decodeTag(node);
        throw new RuntimeException("Cannot decode Token from DOM node "+node);
    }
    
    private Token decodeTag(org.w3c.dom.Node node) {
        int depth = getAttributeInt(node,DEPTH);
        String element = getAttributeString(node,ELEMENT);
        String c = "";
        Map attributes = getAttributeMap(node,ATTRIBUTES);
        
        if (!element.startsWith("/")) { //open tag
            return factory.createOpenTagToken(element,attributes,depth);
        }
        // close tag
        return factory.createCloseTagToken(element.substring(1),attributes,depth);
    }
    
    private Token decodeText(org.w3c.dom.Node node) {
        int depth = getAttributeInt(node,DEPTH);
        if (node.hasChildNodes()) return factory.createTextToken(node.getFirstChild().getNodeValue(),depth);
        else return factory.createNullTextToken(depth);
    }
    
    //DOM conversion utility functions
    private int getAttributeInt(Node node, String name) {
        return Integer.parseInt(node.getAttributes().getNamedItem(name).getNodeValue());
    }
    private String getAttributeString(Node node, String name) {
        return node.getAttributes().getNamedItem(name).getNodeValue();
    }
    private Map getAttributeMap(Node node, String name) {
        return csv2map(node.getAttributes().getNamedItem(name).getNodeValue());
    }
    
    private Map csv2map(String string) {
        StringTokenizer scanner = new StringTokenizer(string,ATTRIBUTES_SEPARATOR);
        Map result = new HashMap();
        while (scanner.hasMoreTokens()) {
            String name_value = scanner.nextToken();
            StringTokenizer tokenizer = new StringTokenizer(name_value,ATTRIBUTE_NAME_VALUE_SEP);
            String name = tokenizer.nextToken().toLowerCase().trim();
            String value = null;
            if (tokenizer.hasMoreTokens()) value = tokenizer.nextToken();
            result.put(name,value);
        }
        result.remove(null);
        return result;
    }
    
}
