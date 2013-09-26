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
/*
 * InstanceSerializer.java
 *
 * Created on 26 aprile 2003, 17.46
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.marshall;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.w3c.dom.Element;

import com.wolf_datamining.autoextracting.roadrunner.Instance;
import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenListRegion;
import com.wolf_datamining.autoextracting.roadrunner.parser.token.TokenFactory;
import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;


public class InstanceSerializer extends ASTEncoder implements Visitor, InstanceConstants {
    
    /** Creates a new instance of InstanceSerializer */
    private Instance instance;
    
    public InstanceSerializer(Instance instance, Writer writer, Indenter ind) {
        super(ind, writer);
        this.instance = instance;
    }
    
    public void encode() throws IOException {
        ind.inc();
        out.print(ind+"<"+INSTANCE+" ");
        out.print(SOURCE+"=\""+instance.getSample().getURL()+"\" ");
        out.println(NAME+"=\""+instance.getSample().getName()+"\">");
        encode(instance.getRoot());
        out.println(ind+"</"+INSTANCE+">");
        ind.dec();
    }
    
    public boolean visit(ASTSubtree node) {
        ind.inc();
        TokenListRegion tlr = node.getMatchingTokenList();
        if (tlr==null) out.println(ind+"<"+NULL_SUBTREE+"/>");
        else {
            out.println(ind+"<"+node.getType()+">");
            ind.inc();
            // Observe that if we wrap HTML fragments whithin CDATA sections,
            // XSL stylesheet should construct the nodes of the output document
            // by dynamically parsing its content
            //            out.println(ind+"<![CDATA[");
            printTokenListRegion(node.getMatchingTokenList());
            //            out.println(ind+"]]>");
            ind.dec();
            out.println(ind+"</"+node.getType()+">");
        }
        ind.dec();
        return false;
    }
    
    private void printTokenListRegion(TokenListRegion tlr) {
        if (tlr.isEmpty()) return;
        Token tag = (Token)tlr.getFirst();
        Element element = TokenFactory.getInstance().getDOMElement(tag);
        try {
            if (element==null) {
                System.err.println("TokenListRegion "+tlr);
                throw new RuntimeException("Something Wrong!");
            }
            // Prepare the DOM document for writing
            Source source = new DOMSource(element);
            // Prepare the output
            Result result = new StreamResult(out);
            // Write the DOM document
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            xformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
        } catch (TransformerException e) {
        }
    }
    
    public boolean visit(ASTToken token) {
        ind.inc();
        out.print(ind+"<"+ATTRIBUTE+" "+LABEL+"=\""+this.instance.getLabel(token)+"\">");
        printVariantValue(token);
        out.println("</"+ATTRIBUTE+">");
        ind.dec();
        return false;
    }
    
    private void printVariantValue(Token token) {
        if (token.getVariantValue()==null) printNullVariantValue(token);
        else printVariant(token);
    }
    private void printNullVariantValue(Token token) {
        if (token.isPCDATA()) out.print("<"+NULL+"/>");
        else if (token.isIMG()) out.print("<"+NULL_IMAGE+"/>");
        else if (token.isLink()) out.print("<"+NULL_LINK+"/>");
    }
    
    private void printVariant(Token token) {
        String variantValue = Util.escapeChars4XML(token.getVariantValue());
        if (token.isPCDATA()) out.print(variantValue);
        //  HACK to deal with mozilla's "save with contents" that gives different local URLs
        //  for the same remote image. Correct version is:
        //  else if (token.isIMG()) out.print("<"+IMAGE+" "+SOURCE+"=\""+variantValue+"\"/>");
        else if (token.isIMG()) out.print("<"+IMAGE+" "+SOURCE+"=\""+getIMGsrc(token)+"\"/>");
        else if (token.isLink()) out.print("<"+LINK+" "+TARGET+"=\""+variantValue+"\"/>");
    }
    
    private String getIMGsrc(Token token) {
        Element element = TokenFactory.getInstance().getDOMElement(token);
        String imgsrc = element.getAttribute("src");
        return Util.escapeChars4XML(imgsrc);
    }
    
}
