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
 * Binder.java
 *
 * Created on 23 gennaio 2003, 12.13
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.bidi.*;


public class Binder extends ParserListenerAdapter implements ParserListener {
    
    private Parser parser;
    private Stack stack;
    private IdentityHashMap tokenBinds;
    private IdentityHashMap subtreeBinds;
    private TokenList tokenlist;
    
    /** Creates a new instance of Binder */
    public Binder(Expression exp) {
        this.parser = new Parser(exp);
        this.parser.setParserListener(this);
        this.tokenBinds = null;
        this.subtreeBinds = null;
        this.stack = null;
    }
    
    public void setBindings(TokenList tl) throws BindingException {
        this.tokenlist = tl;
        this.stack = new Stack();
        this.tokenBinds = new IdentityHashMap(); //Class Token overrides method equals
        this.subtreeBinds = new IdentityHashMap(); //TokenList must not override equals, preserve order
        if (!parser.parse(tl))
            throw new BindingException(parser.getMismatches());
    }
    
    public IdentityHashMap getTokenBindings() throws BindingException {
        return tokenBinds;
    }
    
    public IdentityHashMap getSubtreeBindings() throws BindingException {
        return subtreeBinds;
    }
    
    public void startAnd(ASTAnd node) {
        this.stack.push(this.tokenBinds);
        this.stack.push(this.subtreeBinds);
        this.tokenBinds = new IdentityHashMap();
        this.subtreeBinds = new IdentityHashMap();
    }
    
    public void endAnd(ASTAnd node, boolean matches) {
        Map squareTokenBinds = this.tokenBinds;
        Map squareSubtreeBinds = this.subtreeBinds;
        this.subtreeBinds = (IdentityHashMap)this.stack.pop();
        this.tokenBinds = (IdentityHashMap)this.stack.pop();
        if (matches) {
            this.tokenBinds.putAll(squareTokenBinds);
            this.subtreeBinds.putAll(squareSubtreeBinds);
        }
    }
    
    public void endVariant(ASTVariant node, boolean matches, Token token) {
        if (matches) this.tokenBinds.put(token,node);
    }
    
    public void endToken(ASTToken node, boolean matches, Token token) {
        if (matches) this.tokenBinds.put(token,node);
    }
    
    
    public void endSubtree(ASTSubtree node, boolean matches, int startIndex, int endIndex) {
        /* extract TokenListRegion */
        if (matches) this.subtreeBinds.put(extractTokenList(startIndex-1,endIndex+1), node);
    }
    
    private TokenList extractTokenList(int startIndex, int endIndex) {
        Direction d = Direction.LEFT2RIGHT;
        TokenListRegion tlr = this.tokenlist.asRegion();
        TokenList result = new TokenList(d.subRegion(tlr, d.posBeforeIndex(startIndex), d.posAfterIndex(endIndex)).asList());
        return result;
    }
    
}
