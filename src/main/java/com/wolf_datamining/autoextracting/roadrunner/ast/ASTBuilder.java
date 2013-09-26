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

     Questo  programma  software libero;   lecito redistribuirlo  o
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
 * ASTBuilder.java
 *
 * Created on 31 gennaio 2003, 11.22
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.token.TokenFactory;


public class ASTBuilder {
    
    // This class should be rewritten by scratch
    
    /** Creates a new instance of ASTBuilder */
    private Stack stack;
    private ASTAnd root;
    
    private TokenFactory factory;
    
    public ASTBuilder() {
        this.stack = new Stack();
        this.factory = TokenFactory.getInstance();
        this.root = null;
    }
    
    public ASTAnd getRoot() {
        return this.root;
    }
    
    public void startNodeScope(Node node) {
        stack.push(node.jjtGetChildren());
    }
    public void endNodeScope(Node node) {
        Object popped = this.stack.pop();
        if (popped!=node.jjtGetChildren())
            throw new RuntimeException("Popped element is not the expected one!");
    }
    
    public void addNode(Node node) {
        if (root!=null) {
            List cl = (List)stack.peek();
            cl.add(node);
        }
        else root = (ASTAnd)node;
    }
    
    public void startNodeScope() {
        List templist = new LinkedList();
        stack.push(templist);
    }
    
    public void discardScope() {
        stack.pop();
    }
    
    public void terminateScope(Node node) {
        List templist = (List)stack.pop();
        SimpleNode sn = (SimpleNode)node;
        sn.jjtGetChildren().addAll(templist);
        if (stack.isEmpty()) this.root = (ASTAnd)node;
    }
    
    public Node createTokenNode(Token token) {
        return (Node)token;
    }
    
    public Node createNullToken(Token sample) {
        return (Node)factory.createNullToken(sample);
    }
    
    public ASTVariant createVariant(Token token, String name) {
        ASTVariant result = new ASTVariant(token);
        result.setLabel(name);
        return result;
    }
    public ASTVariant createVariant(Token token) {
        ASTVariant result = new ASTVariant(token);
        return result;
    }
    public ASTSubtree createSubtree(Token token) {
        return new ASTSubtree(token);
    }
    public ASTAnd createAnd() {
        return new ASTAnd();
    }
    public ASTPlus createPlus() {
        return new ASTPlus();
    }
    public ASTHook createHook() {
        return new ASTHook();
    }
    public Node createNode(Node node) {
        Node clone = node.clone(false);
        if (clone instanceof SimpleNode) {// what abot a method getComposite()?
            SimpleNode sn = (SimpleNode)clone;
            sn.jjtGetChildren().clear();
        }
        return clone;
    }
    
}
