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
     come  pubblicata dalla Free Software Foundation; o la versione 2
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
 * Parser.java
 *
 * Created on 
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.parser;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.bidi.*;


public class Parser extends Walker implements Visitor {
    
    private TokenList tokenlist;
    private List mismatches;
    private int currentOrder;
    private boolean lastSucceed;     // result of last matching
    private Set matchingSquares;     // squares matching
    
    private boolean mismatchStoringEnabled;    // true if mismatches have to be saved
    private boolean lastMatchedStoringEnabled; // true if paths of last matched node have to be saved
    
    private ParserListener listener;
    
    private ListIterator nodesIt;  //Iterator on upper list of nodes
    private ListIterator tokensIt; //Iterator on list of tokens
    
    private LinkedList pathOfFinalNodes; //paths of nodes which matched immediately before tokenlist ended
    
    public Parser(Expression e) {
        this(e,Direction.LEFT2RIGHT);
    }
    public Parser(Expression e, Direction dir) {
        this((ExpressionRegion)e.asRegion(), dir);
    }
    public Parser(Region region, Direction dir) {
        super(region,dir);
        this.mismatches = new LinkedList();
        this.matchingSquares = new LinkedHashSet();
        this.listener = new ParserListenerAdapter();
        this.pathOfFinalNodes = new LinkedList();
    }
    
    public void setParserListener(ParserListener pl) {
        this.listener = pl;
    }
    public boolean parse(TokenList tl) {
        init(tl);
        boolean matches = align(nodesIt,tokensIt);
        this.lastSucceed = matches && !nodesIt.hasNext() && !tokensIt.hasNext();
        if (matches) clearCurrentMismatches();
        return this.lastSucceed;
    }
    public boolean parseAllExpression(TokenList tl) {
        init(tl);
        boolean matches = align(nodesIt,tokensIt);
        this.lastSucceed = matches && !nodesIt.hasNext();
        if (matches) clearCurrentMismatches();
        return this.lastSucceed;
    }
    public boolean parseAllTokenlist(TokenList tl) {
        init(tl);
        boolean matches = align(nodesIt,tokensIt);
        this.lastSucceed = matches && !tokensIt.hasNext();
        if (matches) clearCurrentMismatches();
        return this.lastSucceed;
    }
  /**
     * Return the number of tokens which matched
     **/
    public int parsePrefix(TokenList tl) {
        init(tl);
        this.mismatchStoringEnabled = false;
        this.lastMatchedStoringEnabled = false;
        align(nodesIt,tokensIt);
        this.lastSucceed = true;
        return dir.subRegionBefore(tl.asRegion(), getPosReachedOnTokenlist()).size();
    }
    
    private void init(TokenList tl) {
        this.tokenlist = tl;
        clearCurrentMismatches();
        this.tokensIt =  BidirectionalListFactory.newListIterator(tl.getTokens(),this.dir);
        this.nodesIt = BidirectionalListFactory.newListIterator(region.asList(),this.dir);
        this.matchingSquares.clear();
        this.currentOrder = 0;
        this.mismatchStoringEnabled = true;
        this.lastMatchedStoringEnabled = true;
    }
    
    public List getMismatches() {
        checkLastFailed();
        return this.mismatches;
    }
    
    public ExpressionRegion getMatchingRegion() {
        checkLastSucceed();
        return (ExpressionRegion)this.dir.subRegionBeforeNIndex(this.region, this.nodesIt.nextIndex());
    }
    
    public ExpressionRegion getRemainingRegion() {
        checkLastSucceed();
        return (ExpressionRegion)this.dir.subRegionFromNIndex(this.region, this.nodesIt.nextIndex());
    }
    
    public List getPathsReachedOnExpression() {
        return this.pathOfFinalNodes;
    }
    
    public int getPosReachedOnTokenlist() {
        checkLastSucceed();
        return this.dir.posBeforeIndex(this.tokensIt.nextIndex());
    }
    
    private void checkLastSucceed() {
        if (!lastSucceed) throw new IllegalStateException("Last matching wasn't successful!");
    }
    
    private void checkLastFailed() {
        if (lastSucceed) throw new IllegalStateException("No mismatches: Last matching was successful!");
    }
    
    public boolean visit(ASTAnd node) {
        listener.startAnd(node);
        boolean result = alignAllChildren(node);
        if (result) this.matchingSquares.add(getCurrentRelativePath());
        listener.endAnd(node, result);
        return result;
    }
    
    public boolean visit(ASTPlus node) {
        listener.startPlus(node);
        int times = 0;
        int oldIndex = this.tokensIt.nextIndex();
        while (alignAllChildren(node)) {
            times++;
            oldIndex = this.tokensIt.nextIndex();
        }
        rollBack(this.tokensIt,oldIndex);   /* rollback on first Token of square */
        if (times==0) {
            storeMismatch(this.tokensIt.nextIndex());
            rollBack(this.tokensIt,oldIndex); /* rollback last failing attempt */
        }
        listener.endPlus(node, (times>0), times);
        return (times>0);
    }
    
    public boolean visit(ASTHook node) {
        listener.startHook(node);
        int oldIndex = this.tokensIt.nextIndex();
        boolean result = alignAllChildren(node);
        if (!result) rollBack(this.tokensIt,oldIndex); /* hook has not aligned */
        listener.endHook(node, true, (result?1:0));
        return true;
    }
    
    public boolean visit(ASTSubtree node) {
        listener.startSubtree(node);
        int firstIndex = this.tokensIt.nextIndex();
        Token token = (Token)this.tokensIt.next();
        while (node.matches(token) && this.tokensIt.hasNext()) {
            token = (Token)this.tokensIt.next();
        }
        this.tokensIt.previous();
        int lastIndex = this.tokensIt.previousIndex();
        listener.endSubtree(node, true, firstIndex, lastIndex);
        return true;
    }
    
    public boolean visit(ASTToken node) {
        listener.startToken(node);
        Token token = (Token)this.tokensIt.next();
        boolean result = node.matches(token);
        if (result) discardMatchingSquares();
        else storeMismatch(this.tokensIt.previousIndex());
        listener.endToken(node, result, token);
        return result;
    }
    
    public boolean visit(ASTVariant node) {
        listener.startVariant(node);
        Token token = (Token)this.tokensIt.next();
        boolean result = node.matches(token);
        if (result) discardMatchingSquares();
        else storeMismatch(this.tokensIt.previousIndex());
        listener.endVariant(node, result, token);
        return result;
    }
    
    private final void rollBack(ListIterator lit, int oldIndex) {
        while (lit.nextIndex()!=oldIndex) { lit.previous(); }
    }
    
    protected boolean alignAllChildren(Node node) {
        ListIterator it1 = BidirectionalListFactory.newListIterator(node.jjtGetChildren(), this.dir);
        if (align(it1,this.tokensIt)) {
            clearCurrentMismatches();
            return !it1.hasNext();
        }
        return false;
    }
    
    protected boolean align(ListIterator it1, ListIterator it2) {
        boolean result = true;
        startVisitingList(it1);
        while (result && it1.hasNext() && it2.hasNext()) {
            Node node = (Node)it1.next();
            if (!node.jjtAccept(this)) result = false;
        }
        if (lastMatchedStoringEnabled) {
            if (!it2.hasNext()) this.pathOfFinalNodes.add(getCurrentAbsolutePath());            
        }
        endVisitingList(it1);
        return result;
    }
    
    protected void clearCurrentMismatches() {
        this.currentOrder = 0;
        this.mismatches.clear();
    }
    
    protected void storeMismatch(int tokenIndex) {
        if (!this.mismatchStoringEnabled) return;
        /* Build main mismatch point */
        ExpressionRegion region = (ExpressionRegion)this.region;
        Node.Path mPath = getCurrentAbsolutePath();
        Expression expression = region.getExpression();
        ExpressionRegion base = new ExpressionRegion(expression, mPath.subpath(-1));
        if (isBorderPosition(base, mPath)) return;
        ExpressionRegion mismatchingExp = (ExpressionRegion)this.dir.subRegionFromNIndex(base, mPath.lastIndex());
        ExpressionRegion matchingExp = (ExpressionRegion)this.dir.subRegionBeforeNIndex(base, mPath.lastIndex());
        MismatchPoint m = new MismatchPoint(matchingExp,mismatchingExp,mPath, tokenlist, tokenIndex, dir);
        m.setOrder(++this.currentOrder);
        this.mismatches.add(m);
        // add mismatches for all matching squares immediately before the detected mismatch point
        Iterator it = this.matchingSquares.iterator();
        while (it.hasNext()) {
            Node.Path squarePath = region.getAbsolutePath((Node.Path)it.next());
            ExpressionRegion matchingSquare = new ExpressionRegion(expression, squarePath);
            MismatchPoint mp = new MismatchPoint(matchingSquare, mismatchingExp, mPath, tokenlist, tokenIndex, dir);
            mp.setOrder(this.currentOrder);
            this.mismatches.add(mp);
        }
    }
    
    private boolean isBorderPosition(ExpressionRegion base, Node.Path path) {
        return this.dir.getFirstPos(base)==this.dir.posBeforeIndex(path.lastIndex());
    }
    
    protected void discardMatchingSquares() {
        this.matchingSquares.clear();
    }
    
}