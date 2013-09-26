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
 * ASTEncoder.java
 * Created:
 *
 * @author  Valter Crescenzi
 * @version
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.io.FileWriter;
import java.io.Writer;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.config.Constants;
import com.wolf_datamining.autoextracting.roadrunner.config.Preferences;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;



public class ASTEncoder extends VisitorPlugger implements ASTConstants {
    
    static private Logger log = Logger.getLogger(ASTEncoder.class.getName());
    
    protected PrintWriter out;
    protected Indenter ind;
    
    
    public ASTEncoder(Indenter ind, Writer out) {
        this.ind = ind;
        this.out = new PrintWriter(out);
    }
    
    public void encode(Expression exp) {
        out.println(ind+"<"+EXPRESSION+">");
        encode(exp.getRoot());
        out.println(ind+"</"+EXPRESSION+">");
    }
    
    protected void encode(ASTAnd root) {
        this.visit(root);
    }
    
    public boolean visit(ASTAnd node) {
        return serializeStructureNode(node);
    }
    public boolean visit(ASTPlus node) {
        return serializeStructureNode(node);
    }
    public boolean visit(ASTHook node) {
        return serializeStructureNode(node);
    }
    
    private boolean serializeStructureNode(SimpleNode node) {
        ind.inc();
        out.println(ind+"<"+node.getType()+">");
        node.childrenAccept(this);
        out.println(ind+"</"+node.getType()+">");
        ind.dec();
        return false;
    }
    
    public boolean visit(ASTSubtree node) {
        ind.inc();
        out.print(ind+"<"+node.getType()+">");
        encodeToken(node.getRootToken());
        out.println("</"+node.getType()+">");
        ind.dec();
        return false;
    }
    
    public boolean visit(ASTVariant node) {
        ind.inc();
        out.print(ind+"<"+VARIANT+" "+LABEL+"="+"\""+node.getLabel()+"\">");
        encodeToken(node.getToken());
        out.println("</"+VARIANT+">");
        ind.dec();
        return false;
    }
    
    public boolean visit(ASTToken token) {
        ind.inc();
        out.print(ind);
        encodeToken(token);
        out.println();
        ind.dec();
        return false;
    }
    
    private void encodeToken(Token token) {
        if (token.isTag()) encodeTag(token);
        else encodeText(token);
    }
    
    private void encodeTag(Token tag) {
        out.print("<"+TAG+" "+ELEMENT+"=\""+ (tag.isEndTag()?"/":"")+tag.getElement()+"\"");
        out.print(" "+DEPTH+"=\""+tag.depth()+"\"");
        encodeAttributes(tag);
        out.print("/>");
    }
    
    private void encodeAttributes(Token tag) {
        Map attrs = tag.getAttributes();
        out.print(" "+ATTRIBUTES+"=\"");
        Iterator attrsIt = attrs.entrySet().iterator();
        if (attrsIt.hasNext()) encodeAttribute((Map.Entry)attrsIt.next());
        while (attrsIt.hasNext()) {
            out.print(ATTRIBUTES_SEPARATOR);
            encodeAttribute((Map.Entry)attrsIt.next());
        }
        out.print("\"");
    }
    
    private void encodeAttribute(Map.Entry entry) {
        out.print(entry.getKey().toString());
        if (entry.getValue()!=null)
            out.print(ATTRIBUTE_NAME_VALUE_SEP+entry.getValue().toString());
    }
    
    private void encodeText(Token text) {
        out.print("<"+PCDATA+" "+DEPTH+"=\""+text.depth()+"\">");
        if (text.getText()!=null) {
            //We need CDATA sections because free-text could contain tags and other stuff
            out.print("<![CDATA["+text.getText()+"]]>");
        }
        out.print("</"+PCDATA+">");
    }
    
    
}
