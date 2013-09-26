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
 * Engine.java
 * 
 * Created on 
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.engine;

import java.util.*;

import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.Sample;
import com.wolf_datamining.autoextracting.roadrunner.Wrapper;
import com.wolf_datamining.autoextracting.roadrunner.WrappersRepository;
import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.config.*;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.SearchSpace;
import com.wolf_datamining.autoextracting.roadrunner.parser.Parser;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;


public class Engine implements Constants {
    
    static private Logger log = Logger.getLogger(Engine.class.getName());
    
    static final private Preferences step0prefs = new Preferences(); //Preferences for step 0 (first of all, find subtrees)
    static  {
        //Set preferences for step 0
        step0prefs.setName("Step 0 Preferences");
        step0prefs.put(HOOK,   "false");
        step0prefs.put(PLUS,   "false");
        step0prefs.put(SUBTREE, "true");
        step0prefs.put(AMBIGUITY, "0");
        step0prefs.put(SOLUTIONS, "1");
    }
    
    private Expression expression0;
    private TokenList[] tokenlists;
    
    private Sample[] samples;
    
    private int minDOMdepth4subtrees; //useful for recursive step on subtrees
    
    public Engine(Expression expression, TokenList[] tokenlists, int minDOMdepth4subtrees) {
        if (expression==null) {
            log.fine("Creating a canonical wrapper from the first sample");
            this.expression0 = new Expression(tokenlists[0]);
        }
        else this.expression0 = expression;
        this.tokenlists = tokenlists;
        this.samples = null;
        this.minDOMdepth4subtrees = minDOMdepth4subtrees;
    }
    
    public static WrappersRepository infer(Wrapper wrapper, Sample[] samples) {
        TokenList[] tokenlists = asTokenLists(samples);
        Expression expression = (wrapper != null ? wrapper.getExpression() : getInitialExpression(tokenlists));
        log.fine("Initial Expression is\n"+ (expression!=null ? expression.dump("\t"): null));
        Engine engine = new Engine(expression, tokenlists, 0);
        Set solutions = engine.match();
        log.fine("Looking for variants...");
        VariantsHunter variantsHunter = new VariantsHunter(solutions,  tokenlists);
        solutions = variantsHunter.insertVariants();
        log.fine("Detected "+variantsHunter.getAmbiguousExpressions().size()+ " ambiguous wrappers");
        Registry registry = new Registry(solutions, tokenlists);
        return new WrappersRepository(registry, samples);
    }
    
    static private TokenList[] asTokenLists(Sample[] samples) {
        TokenList[] tokenlists = new TokenList[samples.length];
        for(int i=0; i<samples.length; i++) {
            tokenlists[i] = samples[i].getTokenlist();
        }
        return tokenlists;
    }
    
    private static Expression getInitialExpression(TokenList[] tokenlists) {
        if (!Config.getPrefs().getBoolean(STEP0)) return null;
        log.info("Performing step 0");
        Preferences prefs = new Preferences();
        prefs.putAll(Config.getPrefs());  // save old prefs
        Config.load(step0prefs);          // set prefs for step 0
        log.config("Preferences before performing step 0:\n "+Config.getPrefs());
        Engine engine = new Engine(null, tokenlists, 0);
        Set solutions0 = engine.match();  // all-subtrees solution
        Config.load(prefs);               // back to ordinary prefs
        log.config("Preferences after performing step 0:\n "+Config.getPrefs());
        if (solutions0.isEmpty()) return null;
        Expression step0expression = (Expression)solutions0.iterator().next();
        return step0expression;
    }
    
    public Set match()  {
        
        final List matchingTokenlists = Arrays.asList(this.tokenlists);
        log.finer("Matching Expression0:\n" + expression0.dump("\t")+
        "\nNumber of tokenlists to match: " + tokenlists.length);
        SearchSpace space0 = new SearchSpace(expression0, tokenlists[0], minDOMdepth4subtrees);
        space0.setBacktracking(isExtBacktrackingEnabled());
        
        Backtracker runner = new Backtracker(space0) {
            private int sol=0, index = 1;
            
            public ExpressionIterator successor(Expression solution) {
                log.finer("Proceeding with next tokenlist of index "+index);
                SearchSpace space = new SearchSpace(solution, tokenlists[index], minDOMdepth4subtrees);
                space.setBacktracking(isExtBacktrackingEnabled());
                index++;
                return space;
            }
            
            public void back() {
                index--;
                log.finer("Backtracking to tokenlist "+(index-1));
            }
            
            public boolean goalTest(Expression exp) {
                if (! parseAll(exp, matchingTokenlists.subList(0, index)) ) fail();
                return index==matchingTokenlists.size();
            }
            
            public Expression gotFinal(Expression solution) {
                sol++; log.finer("Got solution n."+ sol +" matching with all "+index+" tokenlists");
                return solution;
            }
        };
        
        Set expressions = new HashSet();
        int max_number_of_solutions = maxNumberOfSolutions();
        int sol = 0;
        while (sol++<max_number_of_solutions && runner.hasNext()) {
            expressions.add(runner.next());
            log.finer(expressions.size()+" different solutions are known");
        }
        
        log.finer("Found "+expressions.size()+" solutions");
        log.finer("About to process their subtrees");
        Lumberjack jack = new Lumberjack(expressions, tokenlists);
        return jack.processSubtrees();
    }
    
    private boolean parseAll(Expression exp, List samples) {
        Parser parserlr = new Parser(exp, Direction.LEFT2RIGHT);
        Parser parserrl = new Parser(exp, Direction.RIGHT2LEFT);
        ListIterator it = samples.listIterator();
        while (it.hasNext()) {
            TokenList tokenlist = (TokenList)it.next();
            if (!parserlr.parse(tokenlist)) {
                log.finer("-> Expression does not align with tokenlist "+it.previousIndex());
                log.finer(parserlr.getMismatches().toString());
                return false;
            }
            if (!parserrl.parse(tokenlist)) {
                log.finer("<- Expression does not align with tokenlist "+it.previousIndex());
                log.finer(parserrl.getMismatches().toString());
                return false;
            }
        }
        return true;
    }
    
    private int maxNumberOfSolutions() {
        int max = Config.getPrefs().getInt(SOLUTIONS);
        if (max==0) max = Integer.MAX_VALUE;
        return max;
    }
    
    private boolean isExtBacktrackingEnabled() {
        return Config.getPrefs().getBoolean(Constants.EBACKTRACKING);
    }
    
}