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
 * addSubtree.java
 *
 * Created on 14 aprile 2003, 16.09
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space.operator;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.bidi.*;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Operator;
import com.wolf_datamining.autoextracting.roadrunner.parser.*;
import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;


class addSubtree extends SimpleOperator implements Operator {
    
    static class Factory extends OperatorFactory {
        
        private Factory(MismatchPoint m) {
            super(m);
        }
        
        public List createOperators() {
            return Collections.singletonList(new addSubtree(this.m));
        }
    }
    
    static OperatorFactory getFactory(MismatchPoint m) {
        return new Factory(m);
    }
    
    private ExpressionRegion extension;
    
    private addSubtree(MismatchPoint mismatch) {
        super(mismatch);
        this.extension = null;
    }
    
    protected Expression compute() {
        this.expired = true; // just one result
        this.extension = getExtension();
        if (extension==null) return null; // no way
        try {
            return mismatch.getExpression().addSubtree(extension);
        }
        catch (RuntimeException rte) {
            System.err.println(this);
            System.err.println(rte.getMessage());
            System.exit(-1);
            return null;
        }
    }
    
    protected ExpressionRegion getExtension() {
        List baselist = mismatch.getBaseRegion().asList();
        int pos = mismatch.getPosBeforeFirstMismatchingNode();
        int depth = mismatch.getUpperDOMdepth();
        BidirectionalList bidilist = BidirectionalListFactory.newListView(Direction.LEFT2RIGHT, baselist);
        ListIterator leftIt  = bidilist.listIterator(Direction.RIGHT2LEFT, pos);
        ListIterator rightIt = bidilist.listIterator(Direction.LEFT2RIGHT, pos);
        int left   = getPosBeforeStartTagAtGivenDepth(leftIt, depth);
        if (left==-1) return null;
        int right  = getPosAfterEndTagAtGivenDepth(rightIt, depth);
        if (right==-1) return null;
        this.extension = (ExpressionRegion)mismatch.getBaseRegion().subRegion(left, right);
        if (!delimitSubtree(extension))
            throw new RuntimeException("This region does not delimit a subtree!\n"+extension);
        return extension;
    }
    
    private boolean delimitSubtree(ExpressionRegion extension) {
        // check if extension covers a whole subtree
        Token f = (Token)extension.getFirst();
        Token l = (Token)extension.getLast();
        return (f.code()+l.code()==0 && f.isStartTag() && l.isEndTag() && f.depth()==l.depth());
    }
    
    private int getPosBeforeStartTagAtGivenDepth(ListIterator lit, int depthWanted) {
        while (lit.hasNext()) {
            Node node = (Node)lit.next();
            if (!(node instanceof Token)) continue;
            Token token = (Token) node;
            int depthReached = token.depth();
            if (depthReached==depthWanted && token.isStartTag())
                return Direction.LEFT2RIGHT.posBeforeIndex(lit.previousIndex());
        }
        return -1;
    }

    private int getPosAfterEndTagAtGivenDepth(ListIterator lit, int depthWanted) {
        while (lit.hasNext()) {
            Node node = (Node)lit.next();
            if (!(node instanceof Token)) continue;
            Token token = (Token) node;
            int depthReached = token.depth();
            if (depthReached==depthWanted && token.isEndTag())
                return Direction.LEFT2RIGHT.posAfterIndex(lit.previousIndex());
        }
        return -1;
    }
    
    public int h() { return 1000000000-mismatch.getUpperDOMdepth(); }
    
    public String toString(Indenter ind) {
        return ind+"Operator "+getId()+":\t"+getClass().getName()+"\th="+h()+"\n"+
               ind+"Mismatch "+mismatch.getId()+":\t"+mismatch.toString()+"\n"+
               ind+"Extension:\t"+getExtension()+"\n";
    }
    
}
