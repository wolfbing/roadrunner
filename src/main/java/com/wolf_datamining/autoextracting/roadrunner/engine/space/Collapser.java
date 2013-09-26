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
 * Collapser.java
 *
 * Created on 3 agosto 2003, 18.08
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space;

import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTSubtree;
import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.ast.Node;
import com.wolf_datamining.autoextracting.roadrunner.ast.VisitorPlugger;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.engine.Backtracker;
import com.wolf_datamining.autoextracting.roadrunner.engine.ExpressionIterator;
import com.wolf_datamining.autoextracting.roadrunner.engine.sampler.CharacteristicSet;






/**
 * Collapses as much as possible on the specified region
 * Whenever another square can be collapsed only by
 * introducing a first subtree, it sets a backtracking point and
 * the current result is returned
 */
public class Collapser extends Backtracker {
    
    static private Logger log = Logger.getLogger(Collapser.class.getName());
    
    private Direction dir;
    private ExpressionRegion region;
    private int pos;
    
    private Matcher succ;
    private SearchSpace space;
    
    /** Creates a new instance of Collapser */
    public Collapser(Expression square0, ExpressionRegion region, Direction dir, SearchSpace space) {
        super(getInitialMatcher(region, square0, dir, space));
        this.dir = dir;
        this.pos   = dir.getFirstPos(region);
        this.space = space;
        this.region = region;
    }
    
    static private Matcher getInitialMatcher(final ExpressionRegion region, final Expression square0, Direction dir, SearchSpace space) {
        if (!space.getOptions().isCollapsingBacktrackingEnabled()) {
            return new Matcher(region, square0, dir, space);
        }
        else {
            //this covers the case of an immediate point of backtracking during collapsing
            // this special purpose matcher returns region and its characteristic sample collapsing a 0-sized region
            Matcher matcher0 = new Matcher(region, square0, dir, space) {                
                protected void back() {}
                protected void start() {}
                protected void expired() {}
                public ExpressionRegion getRemainingRegion() {
                    return region;
                }
                public CharacteristicSet getCharacteristicSetOfRemainingRegion() {
                    return chi;
                }
                protected Expression gotFinal(Expression exp) {
                    return square0;
                }
                protected boolean goalTest(Expression square) {
                    return true;
                }
            };
            matcher0.setBacktracking(false);
            return matcher0;
        }
    }
    
    public int getPosCollapsedTo() { return this.pos; }
    
    public Direction getDir() { return this.dir; }
    
    protected ExpressionIterator successor(Expression square) {
        return this.succ;
    }
    
    protected boolean goalTest(Expression square) {
        if (square.isSingleton()) fail();
        Matcher matcher = (Matcher)getCurrent();
        Region base = region.getBaseRegion();
        int leftToCollapse = matcher.getRemainingRegion().size();
        ExpressionRegion remaining = (ExpressionRegion)dir.subRegionFinal(base,leftToCollapse);
        this.pos  = dir.getFirstPos(remaining);
        this.succ = new Matcher(matcher.getCharacteristicSetOfRemainingRegion(),square,dir,space);
        //collapse to the largest extent or to a backtracking point
        return !this.succ.hasNext() || isBacktrackingPoint(remaining,square);
    }
    
    private boolean isBacktrackingPoint(ExpressionRegion remaining, Expression currentSquare)  {
        if (!space.getOptions().isCollapsingBacktrackingEnabled()) return false;
        Matcher nextMatcher = new Matcher(remaining,currentSquare,dir,space);
        Expression nextSquare = nextMatcher.next();
        if (currentSquare.equals(nextSquare)) return false;
        return containsSubtrees(nextSquare);
    }
    
    private boolean containsSubtrees(final Expression exp) {
        return new VisitorPlugger() {
            private boolean result = false;
            public boolean visit(ASTSubtree subtree) {
                this.result = true;
                return true;
            }
            public boolean visitNode(Node node) {
                return (this.result ? true : visitChildren(node));
            }
        }.visit(exp.getRoot());
    }
    
}
