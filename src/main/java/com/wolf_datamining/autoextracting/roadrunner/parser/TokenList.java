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

     Questo  programma software libero; lecito redistribuirlo  o
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
 * TokenList.java
 *
 * Created on 23 gennaio 2003, 16.08
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser;


import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.engine.MDLEvaluator;


public class TokenList {
    
    /** Creates a new instance of TokenList */    
    protected List tokens;
    private int dl;
    
    public TokenList(List tokens) {
        this.tokens = Collections.unmodifiableList(tokens);
        this.dl = -1;
    }
    protected TokenList() {
        this.dl = -1;
    }
    
    public List getTokens() {
        return this.tokens;
    }
    
    public Token getToken(int index) {
        return (Token)this.tokens.get(index);
    }
    
    public TokenListRegion asRegion() {
        return new TokenListRegion(this);
    }
    
    public int size() {
        return this.tokens.size();
    }
    
    public int getDL() {
        this.dl = (this.dl == -1 ? MDLEvaluator.getTokenlistDL(this) : this.dl);
        return this.dl;
    }
    
    public String toString() {
        return "\t\n(TL: "+asRegion().toString()+")";
    }
    
    // N.B. other classes (like Binder) expect that the equals() method of TokenList is not redefined
}
