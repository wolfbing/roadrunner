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
 * SpaceOptions.java
 *
 * Created on December 30, 2003, 2:37 PM
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space;

import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.config.Constants;

public class SpaceOptions implements Constants {

    //enabled operators 
    private boolean hookEnabled;
    private boolean plusEnabled;
    private boolean subtreeEnabled;
    private boolean freePlus;
    
    //enable collapse backtracking
    private boolean collapsingBckt; 

    private int llk; //force expression to be LL(k)
    private int rrk; //force expression to be "RR(k)" (i. e. LL(k) with direction <- )

    //number of DOM depth at which introducing ambiguity mismatches
    private int ambiguity;   

    //minimum DOM depth to apply addSubtree operator
    private int minDOMdepth;
    
    //maximum number of states in a search space
    private int maxStates;
        
    /** Creates a new instance of SpaceOptions */
    public SpaceOptions(int minDOMdepth4subtrees) {
        this.hookEnabled    = Config.getPrefs().getBoolean(HOOK);
        this.plusEnabled    = Config.getPrefs().getBoolean(PLUS);
        this.subtreeEnabled = Config.getPrefs().getBoolean(SUBTREE);
        this.freePlus       = Config.getPrefs().getBoolean(FREE_PLUS);
        this.collapsingBckt = Config.getPrefs().getBoolean(CBACKTRACKING);
        this.ambiguity      = Config.getPrefs().getInt(AMBIGUITY);
        this.llk            = Config.getPrefs().getInt(LLK); 
        this.rrk            = Config.getPrefs().getInt(RRK);
        this.maxStates      = Config.getPrefs().getInt(MAX_STATES);
        this.minDOMdepth    = minDOMdepth4subtrees;
    }
        
    public int getAmbiguity() {
        return this.ambiguity;
    }

    public boolean isHookEnabled() {
        return this.hookEnabled;
    }

    public boolean isPlusEnabled() {
        return this.plusEnabled;
    }

    public boolean isSubtreeEnabled() {
        return this.subtreeEnabled;
    }

    public boolean isPlusFree() {
        return this.freePlus;
    }

    public int getMinDOMdepth() {
        return this.minDOMdepth;
    }

    public boolean isCollapsingBacktrackingEnabled() {
        return this.collapsingBckt;
    }

    public int getLLK() {
        return this.llk;
    }

    public int getRRK() {
        return this.rrk;
    }
    
    public int getMaxNumberOfStates() {
        return this.maxStates;
    }
}
