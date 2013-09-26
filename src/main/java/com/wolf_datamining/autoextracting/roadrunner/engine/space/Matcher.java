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
 * Matcher.java
 * This class encapsulates the recursive nature of the matching technique
 * Created on 25 marzo 2003, 11.00
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.ast.Node;
import com.wolf_datamining.autoextracting.roadrunner.ast.PrefixHunter;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.engine.Backtracker;
import com.wolf_datamining.autoextracting.roadrunner.engine.ExpressionIterator;
import com.wolf_datamining.autoextracting.roadrunner.engine.sampler.CharacteristicSample;
import com.wolf_datamining.autoextracting.roadrunner.engine.sampler.CharacteristicSet;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;





/**
 *
 * @author  Valter Crescenzi
 */
public class Matcher extends Backtracker {
    
    static private Logger log = Logger.getLogger(Matcher.class.getName());
    
    private Direction  dir;
    
    private ExpressionRegion region;
    private ListIterator samplesIt;
    private ExpressionRegion remainderRegion;
    
    protected CharacteristicSet chi;
    private Set samplesOfRemainder;
    private Set regionsOfRemainder;
    
    private SearchSpace space;
    
    /** Creates a new instance of Matcher */
    public Matcher(ExpressionRegion r, Expression exp, Direction dir, SearchSpace space) {
        this(new CharacteristicSet(r,domDepth4Sampler(dir, exp), dir), exp, dir, space);
    }
    Matcher(CharacteristicSet sampled, Expression exp, Direction dir, SearchSpace space) {
        super(new SearchSpace(exp,sampled.first(), dir, space.getOptions().getMinDOMdepth()));
        this.dir = dir;
        this.space = space;
        this.chi = sampled;
        this.region = (ExpressionRegion)sampled.getRegion();
        this.samplesIt = sampled.listIterator();
        this.samplesIt.next();
    }
    
    private static int domDepth4Sampler(Direction dir, Expression exp) {
        return ((Token)PrefixHunter.getFirstLeaf(dir, (Node)dir.getFirst(exp.asRegion()))).depth()-1;
    }

    protected void start() {
        this.regionsOfRemainder = new LinkedHashSet();
        this.samplesOfRemainder = new LinkedHashSet();
    }
    
    protected void back() {
        this.samplesIt.previous();
    }
    
    protected boolean goalTest(Expression exp) {
        SearchSpace current = (SearchSpace)getCurrent();
        CharacteristicSample aSampleOfRemainder = projectRemainingRegion(current);
        if (aSampleOfRemainder==null) fail();
        this.remainderRegion = (ExpressionRegion)aSampleOfRemainder.getRegion();
        
        regionsOfRemainder.add(aSampleOfRemainder.getRegion());
        if (regionsOfRemainder.size()>1) {
            log.finer("Internal matching is ambiguous ...");
            regionsOfRemainder.remove(aSampleOfRemainder.getRegion());
            fail();
        }
        this.samplesOfRemainder.add(aSampleOfRemainder);
        return !this.samplesIt.hasNext(); // all samples done
    }
    
    protected ExpressionIterator successor(Expression generalized) {
        log.finer("Proceeding with next sample "+this.samplesIt.nextIndex());
        TokenList nextSample = (TokenList)this.samplesIt.next();
        int minDOMdepth = this.space.getOptions().getMinDOMdepth();
        return new SearchSpace(generalized, nextSample, this.dir, minDOMdepth);
    }
    
    protected Expression gotFinal(Expression exp) {
        SearchSpace current = (SearchSpace)getCurrent();
        ExpressionRegion matchingRegion = current.getMatchingRegion();
        if (!this.space.getOperatorFactory().checkBorderNodes(matchingRegion)) fail();
        if (!remainderRegion.getBasePath().equals(region.getBasePath())) fail();
        return new Expression(matchingRegion);
    }
    
    public ExpressionRegion getRemainingRegion() {
        return this.remainderRegion;
    }
    
    public CharacteristicSet getCharacteristicSetOfRemainingRegion() {
        return new CharacteristicSet(this.remainderRegion, this.samplesOfRemainder);
    }
    
    private CharacteristicSample projectRemainingRegion(SearchSpace space) {
        CharacteristicSample sample = (CharacteristicSample)space.getTokenlist();
        return sample.getProjectedSampleRemainingAfterPos(dir, space.getPosReachedOnTokenlist());
    }
    
}
