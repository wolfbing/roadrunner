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

     Questo  programma �  software libero; �  lecito redistribuirlo  o
     modificarlo secondo i termini della Licenza Pubblica Generica GNU
     come � pubblicata dalla Free Software Foundation; o la versione 2
     della licenza o (a propria scelta) una versione successiva.

     Questo programma  � distribuito nella speranza che sia  utile, ma
     SENZA  ALCUNA GARANZIA;  senza neppure la  garanzia implicita  di
     NEGOZIABILIT�  o di  APPLICABILIT� PER  UN PARTICOLARE  SCOPO. Si
     veda la Licenza Pubblica Generica GNU per avere maggiori dettagli.

     Questo  programma deve  essere  distribuito assieme  ad una copia
     della Licenza Pubblica Generica GNU; in caso contrario, se ne pu�
     ottenere  una scrivendo  alla:

     Free  Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

*/
/**
 * Sample.java
 *
 *
 * Created: Mon Feb 19 17:56:13 2001
 *
 * @author Valter Crescenzi
 * @version
 */
package com.wolf_datamining.autoextracting.roadrunner;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import org.xml.sax.SAXException;

import com.wolf_datamining.autoextracting.roadrunner.config.Preferences;
import com.wolf_datamining.autoextracting.roadrunner.parser.Lexer;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;


public class Sample  {
    
    static private Logger log = Logger.getLogger(Sample.class.getName());
    
    private URL url;
    private TokenList tokens;
    private IdentityHashMap token2bb;
    
    
    public Sample(URL url, Preferences options, String encoding) throws IOException, SAXException {
        this.url = url;
        log.fine("Reading "+this.getURL());
        Reader source = new BufferedReader(new InputStreamReader(getURL().openStream(),encoding));
        Lexer lexer = new Lexer(source,options);
        this.tokens = new TokenList(lexer.getTokens());
        this.token2bb = lexer.getTokens2BoundingBoxes();
        source.close();
    }
    
    public URL getURL() {
        return this.url;
    }
    
    public TokenList getTokenlist() {
        return this.tokens;
    }
    
    public String getName() {
        return stripExtension(new File(this.getURL().getFile()).getName());
    }
    
    private String stripExtension(String s) {
        int index = s.lastIndexOf('.');
        return ( index!=-1 ? s.substring(0,index) : s);
    }
    
    public int getDL() {
        return getTokenlist().getDL();
    }
    
    public IdentityHashMap getTokens2BoundingBoxes() {
        return this.token2bb;
    }
    
    public String toString() {
        return this.getURL().toString();
    }
    
} // Sample
