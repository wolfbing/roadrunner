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
 * MismatchPoint.java
 *
 * Created on 22 gennaio 2003, 14.35
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser;

import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.ast.Node;
import com.wolf_datamining.autoextracting.roadrunner.ast.PrefixHunter;
import com.wolf_datamining.autoextracting.roadrunner.bidi.*;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.SearchSpace;




public class MismatchPoint {
    
    static private Logger log = Logger.getLogger(MismatchPoint.class.getName());
    
    private static int mismatchCounter = 0;
    
    private Direction dir;
    
    private Node.Path pathOfFirstMismatchingNode;
    private ExpressionRegion matchingExpression;
    private ExpressionRegion mismatchingExpression;
    
    private TokenList  tokenlist;
    private int tokenIndex;
    private boolean linear; // a mismatch is linear if matching and mismatching expressions are contiguous
    private int order;
    
    private int id;
    
    private SearchSpace space;
    
    /** Creates a new instance of MismatchPoint */
    MismatchPoint(ExpressionRegion mexp, ExpressionRegion misexp, Node.Path path, TokenList tl, int index, Direction dir) {
        this.dir = dir;
        this.matchingExpression = mexp;
        this.mismatchingExpression = misexp;
        this.pathOfFirstMismatchingNode = path;
        this.tokenlist  = tl;
        this.tokenIndex = index;
        this.linear = (mexp.getBasePath().equals(misexp.getBasePath()));
        this.id = mismatchCounter++;
    }
    
    public Expression getExpression() {
        return this.matchingExpression.getExpression();
    }
    
    public TokenList getTokenlist() {
        return this.tokenlist;
    }
    
    public Direction getDir() {
        return this.dir;
    }
    
    public Node getFirstMismatchingNode() {
        return (Node)this.dir.getFirst(getMismatchingExpression());
    }
    
    public Node.Path getPathOfFirstMismatchingNode() {
        return this.pathOfFirstMismatchingNode;
    }
    
    public int getPosBeforeFirstMismatchingNode() {
        return this.dir.getFirstPos(getMismatchingExpression());
    }
    
    public int getPosAfterLastMatchingNode() {
        return this.dir.getLastPos(getMatchingExpression());
    }
    
    public Token getFirstMismatchingToken() {
        return this.tokenlist.getToken(tokenIndex);
    }
    
    public int getTokenIndex() {
        return this.tokenIndex;
    }
    
    public Node getLastMatchingNode() {
        if (getMatchingExpression().isEmpty()) return null;
        return (Node)this.dir.getLast(getMatchingExpression());
    }
    
    public Token getLastMatchingToken() {
        if (getMatchingTokenlist().isEmpty()) return null;
        return (Token)this.dir.getLast(getMatchingTokenlist());
    }
    
    public ExpressionRegion getEmptyBorderRegion() {
        //  int pos = getPosBeforeFirstMismatchingNode();
        int pos = getPosAfterLastMatchingNode();
        return (ExpressionRegion)this.dir.subRegion(getBaseRegion(), pos, pos);
    }
    
    public ExpressionRegion getMatchingExpression() {
        return this.matchingExpression;
    }
    
    public ExpressionRegion getMismatchingExpression() {
        return this.mismatchingExpression;
    }
    
    public TokenListRegion getMatchingTokenlist() {
        return (TokenListRegion)this.dir.subRegionBeforeNIndex(getTokenlist().asRegion(), getTokenIndex());
    }
    
    public TokenListRegion getMismatchingTokenlist() {
        return (TokenListRegion)this.dir.subRegionFromNIndex(getTokenlist().asRegion(), getTokenIndex());
    }
    
    public Node.Path getBasePath() {
        return getMatchingExpression().getBasePath();
    }
    
    // The base region is the region encompassing the matching expression
    public ExpressionRegion getBaseRegion() {
        return new ExpressionRegion(getExpression(),getBasePath());
    }
    
    public boolean isLinear() {
        // A mismatch is linear if matching and mismatching regions lie under the same father
        return this.linear;
    }
    
    public void setOrder(int order) {
        this.order = order;
    }
    
    public int getOrder() {
        return this.order;
    }
    
    public int getId() {
        return this.id;
    }
    
    public int getMinDOMdepth() {
        int depthT = getDOMtokenDepth();
        int depthN = getDOMnodeDepth();
        // this is the correct definition: cfr test-basic-hook with step 0 enabled
        //N.B. Observe that 0 <= |depthT - depthN| <= 1 for ordinary mismatch while  |depthT - depthN| > 1 for ambiguity mismatches
        if (Math.abs(depthT-depthN)>1) {
            log.severe("Token depth: "+depthT+" Node depth: "+depthN);
            throw new RuntimeException("Token depth: "+depthT+" Node depth: "+depthN);
        }
        return Math.min(depthT,depthN);
    }
    
    public int getUpperDOMdepth() {
        int depthT = getDOMtokenDepth();
        int depthN = getDOMnodeDepth();
        return (depthT!=depthN ? Math.min(depthT,depthN) : depthT-1);
    }
        
    public int getDOMtokenDepth() {
        return getFirstMismatchingToken().depth();
    }
    
    public int getDOMnodeDepth() {
        return ((Token)PrefixHunter.getFirstLeaf(this.dir, getFirstMismatchingNode())).depth();
    }
    
    public void setSearchSpace(SearchSpace space) {
        this.space = space;
    } 

    public SearchSpace getSearchSpace() {
        return this.space;
    } 
    
    public String toString() {
        Node lmt = (getMatchingExpression().size()>0 ? getLastMatchingNode() : null);
        return "("+lmt+",   "+getFirstMismatchingNode() + "/" + getFirstMismatchingToken()+")"+
        " at position: "+getPathOfFirstMismatchingNode() + "/" + getTokenIndex();
    }
    
}