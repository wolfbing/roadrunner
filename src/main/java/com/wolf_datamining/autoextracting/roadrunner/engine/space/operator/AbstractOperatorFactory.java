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

     Questo  programma  software libero;  lecito redistribuirlo  o
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
 * OperatorFactory.java
 *
 * Created on 20 marzo 2003, 11.48
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space.operator;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.config.Constants;
import com.wolf_datamining.autoextracting.roadrunner.engine.LLChecker;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Operator;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.SearchSpace;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.SpaceOptions;
import com.wolf_datamining.autoextracting.roadrunner.parser.MismatchPoint;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


/**
 *
 * @author  Valter Crescenzi
 */
public class AbstractOperatorFactory implements Constants {
    
    static private Logger log = Logger.getLogger(AbstractOperatorFactory.class.getName());
    
    public static AbstractOperatorFactory getInstance(SearchSpace space) {
        return new AbstractOperatorFactory(space);
    }
    
    /**
     * Abstract Factory perch� fabbrica oggetti diversi ma correlati (gli operatori)
     *
     * This class centralizes the cut of search space by discarding certain operators and
     * certain regular expressions (Plus of Plus, Plus of Hook, Hook as border node of
     * a square etc. etc.)
     *
     * Since some checks can be performed only after applying the addPlus operator, a
     * call-back method postConditionOnAddPlus is used
     *
     */
    private SearchSpace space;
    private SpaceOptions options; //useful for recursive step on subtrees
    
    private LLChecker LR_checker;
    private LLChecker RL_checker;
    
    private AbstractOperatorFactory(SearchSpace space) {
        this.space = space;
        this.options = space.getOptions();
        this.LR_checker = new LLChecker(options.getLLK(), Direction.LEFT2RIGHT);
        this.RL_checker = new LLChecker(options.getRRK(), Direction.RIGHT2LEFT);
    }
    
    
    private LLChecker getChecker(Direction dir) {
        return (dir == Direction.LEFT2RIGHT ? LR_checker : RL_checker);
    }
    
    public SortedSet createOperators(List mismatches) {
        TreeSet operators = new TreeSet();
        ListIterator it = mismatches.listIterator();
        while (it.hasNext()) {
            MismatchPoint m = (MismatchPoint)it.next();
            log.finest("Space "+space.getId()+" - Creating operators for mismatch "+m.getId());
            log.finest("("+(it.previousIndex()+1)+" out of "+mismatches.size()+")");
            operators.addAll(createOperator(m));
        }
        return operators;
    }
    
    private Set createOperator(MismatchPoint m) {
        Set operators = new HashSet();
        if (options.isPlusEnabled()) operators.addAll(createAddPlusOperators(m));
        if (options.isHookEnabled()) operators.addAll(createAddHookOperators(m));
        if (options.isSubtreeEnabled()) operators.addAll(createAddSubtreeOperators(m));
        return operators;
    }
    
    private Set createAddPlusOperators(MismatchPoint m) {
        Set operators = new HashSet();
        if (checkAdjacencyForAddPlus(m)) {
            if (m.isLinear()) //otherwise Plus extension would spread on two different bases
                operators.addAll(createAddPlus_w(m));
            else logReason("addPlus_w operators aren't created on non-linear mismatches");
            operators.addAll(createAddPlus_s(m));
        }
        else logReason("addPlus adjacencies not satisfied");
        return operators;
    }
    
    private Set createAddPlus_w(MismatchPoint m) {
        Set operators  = new HashSet();
        Iterator it = addPlus_w.getFactory(m).createOperators().iterator();
        
        while (it.hasNext()) {
            addPlus_w addplus_w = (addPlus_w)it.next();
            if (!addplus_w.isExpired()) {
                addOperator(operators, addplus_w);
            }
            else logReason(addplus_w,"already expired");
        }
        return operators;
    }
    
    private Set createAddPlus_s(MismatchPoint m) {
        Set operators  = new HashSet();
        Iterator it = addPlus_s.getFactory(m).createOperators().iterator();
        
        // discard already expired operators
        while (it.hasNext()) {
            addPlus_s addplus_s = (addPlus_s)it.next();
            if (!addplus_s.isExpired()) {
                addOperator(operators, addplus_s);
            }
            else logReason(addplus_s,"already expired");
        }
        return operators;
    }
    
    private Set createAddHookOperators(MismatchPoint m) {
        Set operators = new HashSet();
        if (m.isLinear()) {//non-linear mismatches can only lead to border hooks
            if (checkAdjacencyForAddHook(m)) {
                operators.addAll(createAddHook_w(m));
                operators.addAll(createAddHook_s(m));
            }
            else logReason("addHook adjacencies not satisfied");
        }
        else logReason("addHook mismatch not linear");
        return operators;
    }
    
    private Set createAddHook_w(MismatchPoint m) {
        Set operators  = new HashSet();
        Iterator it = addHook_w.getFactory(m).createOperators().iterator();
        Direction dir = m.getDir();
        while (it.hasNext()) {
            //to avoid adjacencies amongst Hooks:  after the square of an addHook_w operator cannot appear an ASTHook
            addHook_w op = (addHook_w)it.next();
            
            if (check(op.isExpired(),"already expired",op)) continue;
            if (check(op.getExtension().isBorderRegion(),"border Hook",op)) continue;
            Node firstAfter = (Node)dir.getFirst(op.getRegionAfterSquare());
            if (check(firstAfter instanceof ASTHook,"Hook after square",op)) continue;
            Node lastInside = (Node)dir.getLast(op.getSquare());
            if (check(lastInside instanceof ASTHook,"Hook as last node of square",op)) continue;
            if (check(!isLLk(m, op),"not LL(k)",op)) continue;
            addOperator(operators, op);
        }
        return operators;
    }
    
    private Set createAddHook_s(MismatchPoint m) {
        Set operators  = new HashSet();
        Iterator it = addHook_s.getFactory(m).createOperators().iterator();
        
        // whenever an addPlus operator applies, the corresponding addHook operator can only lead to ambiguous expressions
        while (it.hasNext()) {
            addHook_s op = (addHook_s)it.next();
            
            if (check(op.isExpired(),"already expired",op)) continue;
            if (check(op.getExtension().isBorderRegion(),"border Hook",op)) continue;
            if (check(!isLLk(m, op),"not LL(k)",op)) continue;
            addOperator(operators, op);
        }
        return operators;
    }
    
    private Set createAddSubtreeOperators(MismatchPoint m) {
        Set operators = new HashSet();
        
        if (checkAdjacencyForAddSubtree(m))
            if (m.getUpperDOMdepth() > options.getMinDOMdepth())
                if (m.isLinear()) {
                    List op_subtrees = addSubtree.getFactory(m).createOperators();
                    operators.addAll(op_subtrees);
                    addOperator(operators, (Operator)op_subtrees.get(0));
                }
                else logReason("addSubtree not created on on non-linear mismatches");
            else logReason("addSubtree not enabled at DOM depth "+m.getUpperDOMdepth()+"<="+options.getMinDOMdepth());
        else logReason("addSubtree adjacencies not satisfied");
        return operators;
    }
    
    private boolean check(boolean cond, String reason, Operator op) {
        if (cond) return true;
        else logReason(op, reason);
        return false;
    }
    
    private void logReason(String reason) {
        log.finest("Space "+space.getId()+" - "+reason);
    }
    
    private void logReason(Operator op,String reason) {
        log.finest("Space "+space.getId()+" - "+op.getClass().getName()+ " "+op.getId()+" "+reason);
    }
    
    private void addOperator(Set operators, Operator op) {
        log.finest("Space "+space.getId()+" - adding operator:\n"+op);
        operators.add(op);
    }
    
    private boolean checkAdjacencyForAddHook(MismatchPoint mismatch) {
        
        return ( !(mismatch.getLastMatchingNode() instanceof ASTHook) &&
                 !(mismatch.getFirstMismatchingNode() instanceof ASTHook) && //this should be useless
                   checkAdjacencyForAddPlus(mismatch) );
    }
    
    private boolean checkAdjacencyForAddPlus(MismatchPoint mismatch) {
        return ( options.isPlusFree() || (mismatch.getLastMatchingNode() instanceof Token) &&
               (mismatch.getFirstMismatchingNode() instanceof Token) );
    }
    
    private boolean adjacencyForNewPlus(ExpressionRegion p, ExpressionRegion f, Direction dir) {
        return ( options.isPlusFree() ||
                (p.isEmpty()  || dir.getLast(p) instanceof Token) &&
                (f.isEmpty()  || dir.getFirst(f) instanceof Token) );
    }
    
    private boolean checkAdjacencyForAddSubtree(MismatchPoint mismatch) {
        return true;
    }
    
    public boolean checkBorderNodes(ExpressionRegion region) {
        Node first = (Node)Direction.LEFT2RIGHT.getFirst(region);
        Node last  = (Node)Direction.LEFT2RIGHT.getLast(region);
        return checkBorderNode(last) && checkBorderNode(first);
    }
    public boolean checkBorderNode(Node node) {
        return node instanceof Token || options.isPlusFree() && node instanceof ASTPlus;
    }
    
    private boolean isLLk(MismatchPoint m, SquareOperator sop) {
        return isLLk(sop.getSquare(), sop.getRegionBeforeSquare(), sop.getRegionAfterSquare(), m.getDir());
    }
    
    boolean postConditionOnAddPlus(addPlus op) {
        MismatchPoint m = op.getMismatch();
        ExpressionRegion extension = op.getExtension();
        Direction dir = m.getDir();
        ExpressionRegion foll = getRegionFollowing(extension, dir);
        ExpressionRegion prec = getRegionFollowing(extension, dir.uTurn());
        Expression csquare = op.getCandidateSquare();
        return !(check(fullExtensionUnderPlus(m, extension),"Post: Plus under one Plus", op) ||
        check(!adjacencyForNewPlus(prec, foll, dir),"Post: adjacencies not allowed", op) ||
        check(!isLLk(csquare.asRegion(), prec, foll, dir),"Post: is not LL(k)", op));
    }
    
    private boolean isLLk(Region square, Region preceding, Region following, Direction dir) {
        if (!following.isEmpty() && getChecker(dir).existsAcommonPrefix(square, following))
            return false;
        if (!preceding.isEmpty() && getChecker(dir.uTurn()).existsAcommonPrefix(square, preceding))
            return false;
        return true;
    }
    
    private boolean fullExtensionUnderPlus(MismatchPoint mismatch, ExpressionRegion extension) {
        if (!extension.isFull()) return false;
        if (mismatch.getBasePath().depth()==0) return false;
        Node parent = mismatch.getExpression().getNode(mismatch.getBasePath().subpath(-1));
        return parent instanceof ASTPlus;
    }
    
    // N.B. Moving these two methods to roadrunner.ast.ExpressionRegion would make that class
    // coupled to roadrunner.bidi.Direction
    private ExpressionRegion getRegionFollowing(ExpressionRegion r, Direction dir) {
        int lpos = dir.getLastPos(r);
        ExpressionRegion following = (ExpressionRegion)dir.subRegionAfter(r.getBaseRegion(), lpos);
        if (following.isEmpty() || r.getBasePath().depth()<2) return following;
        else return getRegionFollowing(r.getExpression(), r.getBasePath().subpath(-1), dir);
    }
    
    private ExpressionRegion getRegionFollowing(Expression exp, Node.Path path, Direction dir) {
        ExpressionRegion base = new ExpressionRegion(exp, path.subpath(-1));
        int lastPos = dir.posAfterIndex(path.lastIndex());
        ExpressionRegion following =  (ExpressionRegion)dir.subRegionAfter(base, lastPos);
        if (following.isEmpty() || path.depth()<2) return following;
        else return getRegionFollowing(exp, path.subpath(-2), dir);
    }
    
}