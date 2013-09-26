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
 * addPlus.java
 *
 * Created on 23 marzo 2003, 15.09
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space.operator;

import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Collapser;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Matcher;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Operator;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.SearchSpace;
import com.wolf_datamining.autoextracting.roadrunner.parser.MismatchPoint;



abstract class addPlus extends SquareOperator implements Operator {
    
    static private Logger log = Logger.getLogger(addPlus.class.getName());
    
    private final static int MAX_MATCHING_BORDER_TOKENS = 16;
    
/* Problemi da risolvere:
 * 1. dove e come fare backtracking sul collassamento
 *       (per il momento limitiamoci alla vecchia semantica) <--
 *       E' UN VERO PROBLEMA: la semantica aggressiva produce troppi subtree e  fa sbagliare estensione del Plus per eccesso
 * 2. come trovare i riferimenti dell'estensione per poi fare le modifiche
 *       (predisporre Matcher per supportare il calcolo dell'estensione) FATTO
 * 3. come gestire le soluzioni multiple
 *       (pattern iterator, qui, dentro SearchSpace e dentro Matcher) FATTO
 * 4. come costruire il campione caratteristico senza spendere troppo
 *       (mantere la cache dei discendenti ASTAnd che esistono dentro Expression e Region) CHISSENEFREGA
 * 5. come calcolare il compress-ratio FATTO (tramite Registry)
 */
    
    /**
     * Come punto di backtracking prendere tutti quelli in cui la generalizzazione
     * dello square comporta un incremento del compress-ratio.
     * Quindi lo stato dell'allineamento deve ricordarsi dello square usato per
     * la generalizzazione (e volendo anche l'estensione anche se questa si pu�
     * ricostruire). Si fa l'ipotesi che non possano esiste occorrenze di square
     * non collassate adiacenti al corrispondente square.
     */
    
    private ExpressionRegion base;
    private Expression csquare;
    private Matcher csmatcher;
    private Collapser beforeCollapser;
    private Collapser afterCollapser;
    private int first, last;
    
    
    /** Creates a new instance of addPlus */
    addPlus(MismatchPoint mismatch, Searcher searcher, Region square, int k) {
        super(mismatch,searcher,square,k);
        //
        // Square Location by Delimiter Search performed during operator construction
        //
        this.base = mismatch.getBaseRegion();
        this.csquare = null;
        this.csmatcher = new Matcher(mismatch.getMatchingExpression(), new Expression(square), dir.uTurn(), space);
        this.beforeCollapser = null;
        this.afterCollapser = null;
        this.first = -1;
        this.last = -1;
        this.lenOfMatchingBorder = countMatchingBorder(dir.uTurn(), getRegionBeforeSquare(), square, MAX_MATCHING_BORDER_TOKENS);
    }
    
    protected Expression compute() {
        while (afterCollapser!=null || beforeCollapser!=null || this.csmatcher.hasNext()) {
            //
            // Square Matching after mismatch point
            //
            if (afterCollapser!=null) {
                this.last = collapse(afterCollapser);
                if (!afterCollapser.hasNext()) afterCollapser = null;
                
                Expression result = wrapperGeneralization(csquare,first,last);
                if (result!=null) return result;
                
                continue;
            }
            //
            // Square Matching before mismatch point
            //
            if (beforeCollapser!=null) {
                this.first = collapse(beforeCollapser);
                if (!beforeCollapser.hasNext()) beforeCollapser = null;
                
                afterCollapser = getAfterCollapser(this.csquare, this.space);
                
                continue;
            }
            //
            // Candidate Square Evaluation
            //
            this.csquare = csmatcher.next();
            if (!this.csquare.isSingleton()) {
                log.finest(" operator "+getId()+": collapsing before: "+dir.uTurn());
                beforeCollapser = new Collapser(csquare, csmatcher.getRemainingRegion(), dir.uTurn(), space);
            }
        }
        this.expired = true;
        return null;
    }
    
    private int collapse(Collapser collapser) {
        int pos = collapser.getPosCollapsedTo();
        if (collapser.hasNext()) {
            this.csquare = collapser.next();
            pos = collapser.getPosCollapsedTo();
            log.finest(" operator "+getId()+"\n collapsed "+collapser.getDir()+"to pos "+pos);
            log.finest(" Candidate Square \n"+csquare.dump("\t"));
        }
        return pos;
    }
    
    private Expression wrapperGeneralization(Expression square, int first, int last) {
        //
        // Wrapper Generalization
        //
        if (!this.mismatch.getSearchSpace().getOperatorFactory().postConditionOnAddPlus(this))
            return null; // check post-evaluation conditions
        
        return mismatch.getExpression().addPlus(getExtension(),square);
    }
    
    abstract Collapser getAfterCollapser(Expression square, SearchSpace space);
    
    protected Region getRegionAfterSquare() {
        return getSearcher().getRegionAfterOccurrence(k-1);
    }
    
    protected Expression getCandidateSquare() {
        return this.csquare;
    }
    
    protected ExpressionRegion getExtension() {
        return (ExpressionRegion)dir.subRegion(this.base,this.first,this.last);
    }
    
    public int h() { return -10+super.h(); }
    
}
