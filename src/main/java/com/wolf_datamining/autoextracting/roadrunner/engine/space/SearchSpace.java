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
 * SearchSpace.java
 *
 * Created on 17 febbraio 2003, 18.06
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.engine.Backtracker;
import com.wolf_datamining.autoextracting.roadrunner.engine.ExpressionIterator;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.operator.AbstractOperatorFactory;
import com.wolf_datamining.autoextracting.roadrunner.parser.AmbiguityMismatchFactory;
import com.wolf_datamining.autoextracting.roadrunner.parser.MismatchPoint;
import com.wolf_datamining.autoextracting.roadrunner.parser.Parser;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;


/**
 *
 * @author  Valter Crescenzi
 */
public class SearchSpace extends Backtracker {
    
    static private Logger log = Logger.getLogger(SearchSpace.class.getName());
    
    /** Lo stato dell'allineamento si riduce ad una semplice exp. reg., ma servono anche mismatches ed operatori
     *  In questo spazio degli stati:      Operatore: stato -> { stato }
     */
    
/*  Lavorando  con le regioni piuttosto che le espressioni si hanno due vantaggi sostanziali:
 *   1) rende la classe piu' generale perche per passare da una exp ad una regione basta fare exp.asRegion();
 *   2) si evitano alcune materializzazioni che costano parecchio perche' implicano il cloning dei nodi delle exp
 */
        
    private Parser parser;
    
    private Direction dir;

    private ExpressionRegion region;
    private TokenList tokenlist;
    
    private AbstractOperatorFactory operatorFactory;
    private AmbiguityMismatchFactory mismatchFactory;
    
    private SpaceOptions options;
    
    private int numberOfStates;
    
    /** Creates a new instance of SearchSpace */
    public SearchSpace(Expression exp, TokenList tokenlist, int minDOMdepth) {
        this(exp.asRegion(),tokenlist,Direction.LEFT2RIGHT, minDOMdepth);
    }
    SearchSpace(Expression exp, TokenList tokenlist, Direction dir, int minDOMdepth) {
        this(exp.asRegion(),tokenlist,dir,minDOMdepth);
    }
    
    private SearchSpace(ExpressionRegion r, TokenList tl, Direction dir, int minDOMdepth) {
        super(new Expression(r));
        init(r,tl,dir,minDOMdepth);
        log.finest("Creating Search Space "+id+" "+dir+" to match with Tokenlist: "+tl);
    }
    
    private void init(ExpressionRegion r, TokenList tl, Direction dir, int minDOMdepth) {
        this.dir = dir;
        this.region = r;
        this.tokenlist = tl;
        this.options = new SpaceOptions(minDOMdepth);
        this.operatorFactory = AbstractOperatorFactory.getInstance(this);
        this.mismatchFactory = new AmbiguityMismatchFactory();
        this.numberOfStates = 0;
    }
    
    public SpaceOptions getOptions() {
        return this.options;
    }
    
    public AbstractOperatorFactory getOperatorFactory() {
        return this.operatorFactory;
    }
    
    TokenList getTokenlist() {
        return this.tokenlist;
    }
    
    protected ExpressionIterator successor(Expression current) {
        this.numberOfStates++;
        if (this.numberOfStates==this.options.getMaxNumberOfStates())
            return ExpressionIterator.expiredIterator();
        List mismatches = this.parser.getMismatches();
        if (options.getAmbiguity()!=0)
            addAmbiguityMismatches(mismatches);
        setSpaceOfMismatches(mismatches);
        State state = new State(mismatches, this);
        log.finest("Space "+id+" - Reached State "+state.getId());
        log.fine("Space "+id+" - Expression from State "+state.getId()+"\n"+current.dump("\t"));
        return state;
    }
    
    private void addAmbiguityMismatches(List mismatches) {
        int len = mismatches.size();
        for(int i=0; i<len; i++) {
            MismatchPoint m = (MismatchPoint)mismatches.get(i);
            List ambiguityMismatches = mismatchFactory.getAmbiguityMismatches(m);
            mismatches.addAll(ambiguityMismatches);
        }
    }
    
    private void setSpaceOfMismatches(List mismatches) {
        Iterator it = mismatches.iterator();
        while (it.hasNext()) {
            MismatchPoint m = (MismatchPoint)it.next();
            log.fine("Space "+id+" - mismatch "+m.getId()+":\t"+m);
            m.setSearchSpace(this);
        }
    }
    
    protected boolean goalTest(Expression exp) {
        this.parser = new Parser(exp,this.dir);
        return parser.parseAllExpression(this.tokenlist);
    }
    
    private int solutions = 0; //this fields and next two methods only for logging
    protected Expression gotFinal(Expression exp) {
        this.solutions++;
        log.fine("Space "+id+" - Found solution n. "+solutions);
        return exp;
    }
    
    protected void expired() {
        log.fine("Space "+id+" - No other solutions");
    }
    
    ExpressionRegion getMatchingRegion() {
        return this.parser.getMatchingRegion();
    }
    
    int getPosReachedOnTokenlist() {
        return this.parser.getPosReachedOnTokenlist();
    }
    
}
