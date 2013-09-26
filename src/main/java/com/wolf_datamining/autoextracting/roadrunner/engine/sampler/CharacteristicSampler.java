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
/**
 * CharacteristicSampler.java
 *
 *
 * Created: Wed Dec 13 17:49:57 2000
 *
 * @author Valter Crescenzi
 * @version
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.sampler;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.bidi.*;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.token.TokenFactory;


public class CharacteristicSampler extends VisitorPlugger implements Visitor {
        
    static private Logger log = Logger.getLogger(CharacteristicSampler.class.getName());
    
    private Map lowerSampled; // Cardinalities: For Hooks 0; For Plus 1; For Subtree <X/>; For Variant X
    private Map upperSampled; // Cardinalities: For Hooks 1; For Plus 2; For Subtree <Y/>; For Variant X
    
    private CharacteristicSample sample;
    private boolean allComputed;
    private int maxSampleLength;
    private int minDOMdepth;
    private int tokensCounter;
    private Region region;
    private TokenFactory factory;
    
    public CharacteristicSampler(Region region, Direction dir) {
        super(dir);
        this.factory = TokenFactory.getInstance();
        this.region = region;
        this.lowerSampled = new IdentityHashMap();
        this.upperSampled = new IdentityHashMap();
        this.allComputed = false;
        this.sample = null;
        this.maxSampleLength = -1;
        this.minDOMdepth     = -1;
        this.tokensCounter   =  0;
    }
    
    public void setMaxSampleLength(int max) {
        this.maxSampleLength = max;
    }
    
    void setMinDOMdepth(int min) {
        this.minDOMdepth = min;
    }
    
    public CharacteristicSample computeNext() {
        this.sample = null;
        if (!this.allComputed) {
            this.sample = new CharacteristicSample(this.region, this.dir);
            this.tokensCounter = 0;
            visit(this.region);
            this.allComputed = lowerSampled.equals(upperSampled);
        }
        return this.sample;
    }
    
    public boolean visit(ASTPlus node) {
        visitNode(node);
        if (!lowerSampled.containsKey(node)) { //twice ?
            lowerSampled.put(node,null);
        }
        else if (!upperSampled.containsKey(node)) {
            visitNode(node);
            upperSampled.put(node,null);
        }
        return false;
    }
    
    public boolean visit(ASTHook node) {
        if (!upperSampled.containsKey(node)) { //once ?
            visitNode(node);
            upperSampled.put(node,null);
        }
        else if (!lowerSampled.containsKey(node)) {
            lowerSampled.put(node,null);
        }
        else {
            /* Hooks of a Plus require this */
            visitNode(node); //all done: go deeper
        }
        return false;
    }
    
    public boolean visit(ASTSubtree node) {
        Token token = node.getRootToken();
        if (!lowerSampled.containsKey(node)) {
            lowerSampled.put(node,null);
            addToken(createOpenTagToken("X",token));
            addToken(createCloseTagToken("X",token));
        }
        else if (!upperSampled.containsKey(node)) {
            upperSampled.put(node,null);
            addToken(createOpenTagToken("Y",token));
            addToken(createCloseTagToken("Y",token));
        }
        return false;
    }
       
    private Token createOpenTagToken(String el, Token token) {
        return ( dir==Direction.LEFT2RIGHT ? 
                    factory.createOpenTagToken(el, Collections.EMPTY_MAP, token.depth()+1) :
                    factory.createCloseTagToken(el, Collections.EMPTY_MAP, token.depth()+1) );
    }
    private Token createCloseTagToken(String el, Token token) {
        return ( dir==Direction.LEFT2RIGHT ? 
                    factory.createCloseTagToken(el, Collections.EMPTY_MAP, token.depth()+1) :
                    factory.createOpenTagToken(el, Collections.EMPTY_MAP, token.depth()+1) );
    }
    
    public boolean visit(ASTVariant node) {
//        if (!lowerSampled.containsKey(node)) {
//            lowerSampled.put(node,null);//NON VA BENE
//            addToken(node.createSampleToken("X"));
//        }
//        else if (!upperSampled.containsKey(node)) {
//            upperSampled.put(node,null);
//            addToken(node.createSampleToken("Y"));
//        }
//        addToken(node);
        addToken(node.getToken());
        return false;
    }
    
    public boolean visit(ASTToken node) {
        return addToken(node);
    }
        
    protected boolean visitList(List list) {
        // N.B. this is the only method which is invoked on all nodes; visitNode() and visitChildren are not invoked on node of base region
        boolean stop = false;
        ListIterator it = BidirectionalListFactory.newListIterator(list, this.dir);
        while (it.hasNext()) {
            Node node = (Node)it.next();
            if (node.jjtAccept(this)) {
                stop = true;
                break; // Check stop condition
            }
        }
        return stop;
    }
    
    private boolean addToken(Token token) {
        this.tokensCounter++;
        this.sample.addToken(token);
        return (this.tokensCounter==this.maxSampleLength || token.depth()==this.minDOMdepth);
    }
    
} // CharacteristicSampler
