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
 * DOMLoader.java
 * Class to load DOM Document from HTML and XML sources.
 * Call Tidy to load non well-formed HTML documents.
 *
 * Created on 2 marzo 2003, 20.59
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.util;

import java.io.*;
import java.util.logging.*;
import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

import org.cyberneko.html.parsers.DOMParser;
import org.xml.sax.InputSource;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.cyberneko.html.filters.Identity;
import org.cyberneko.html.filters.Purifier;

public class DOMLoader {
    
    static final private Logger log = Logger.getLogger(DOMLoader.class.getName());
    
    static public Document parseXML(InputStream in) throws SAXException, IOException {
        return parseXML(new InputSource(in));
    }
    
    static public Document parseXML(Reader in) throws SAXException, IOException {
        return parseXML(new InputSource(in));
    }
    
    static public Document parseXML(InputSource in) throws SAXException, IOException {
        return getDocumentBuilder().parse(in);
    }
    
    static public Document parseHTML(Reader in) throws SAXException, IOException {
        DOMParser parser = new DOMParser();
        parser.setFeature("http://xml.org/sax/features/namespaces", false);
        parser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
        XMLDocumentFilter[] filters = { new Purifier() };
        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);
        parser.parse(new InputSource(in));
        return parser.getDocument();
    }
    
    static public DocumentBuilder getDocumentBuilder() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(false);
            factory.setExpandEntityReferences(true);
            factory.setValidating(false);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder parser = factory.newDocumentBuilder();
            parser.setEntityResolver(new EntityResolver() {
                public InputSource resolveEntity(String publicId, String systemId) {
                    if (systemId.endsWith(".dtd"))
                        // this deactivates all DTDs by giving empty XML docs
                        return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='ISO-8859-1'?>".getBytes()));
                    else return null;
                }
            });
            return parser;
        }
        catch (ParserConfigurationException pce) {
            log.severe("DOM Parser Misconfiguration"+pce);
            System.exit(-1);
            return null;
        }
    }
    
}