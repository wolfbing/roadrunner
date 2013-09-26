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
 * CharacteristicSample.java
 * @author  Valter Crescenzi
 * Created on 14 ottobre 2003, 14.29
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.sampler;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.bidi.*;
import com.wolf_datamining.autoextracting.roadrunner.parser.Parser;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;


public class CharacteristicSample extends TokenList {
    
    static private Logger log = Logger.getLogger(CharacteristicSample.class.getName());
    
    private Region region;
    private Direction dir;
    
    private BidirectionalList sampled;
    
    /** Creates a new instance of CharacteristicSample */
    CharacteristicSample(Region region, Direction dir) {
        this.region = region;
        this.dir = dir;
        this.tokens = new ArrayList(Math.max(region.size(), 16));
        this.sampled = BidirectionalListFactory.newListView(dir, this.tokens);
    }
    
    private CharacteristicSample(Region region, TokenList alreadySampled, Direction dir) {
        super(alreadySampled.getTokens());
        this.region = region;
        this.dir = dir;
        this.sampled = null;
    }
    
    public Region getRegion() {
        return this.region;
    }
    
    void addToken(Token token) {
        this.sampled.add(token);
    }
    
    public CharacteristicSample getProjectedSampleRemainingAfterPos(Direction dir, int pos) {
        //N.B. pos is a position on the sample, not on the region sampled
        Region tokenlistRegion = this.asRegion();
        TokenList tokenlistBeforePos = new TokenList(dir.subRegionBefore(tokenlistRegion, pos).asList());
        TokenList tokenlistAfterPos = new TokenList(dir.subRegionAfter(tokenlistRegion, pos).asList());
        Parser parser = new Parser(this.region, dir);
        if (!parser.parseAllTokenlist(tokenlistBeforePos)) {
            log.fine("portion of characteristic sample doesn't match with the generating exp!\n"+
                     "Region:  "+this.region+"\nPortion: "+tokenlistBeforePos);
            return null;
        }
        ExpressionRegion remainingRegion = parser.getRemainingRegion();
        return new CharacteristicSample(remainingRegion, tokenlistAfterPos, dir);
    }
    
    
}
