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
 * Combiner.java
 *
 * Created on 7 dicembre 2003, 16.01
 * @author  Valter Crescenzi, Luigi Arlotta
 */

package com.wolf_datamining.autoextracting.roadrunner.labeller;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTVariant;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


/**
 *
 * @author  Luigi Arlotta, Valter Crescenzi
 */
class Combiner {
    
    private static Logger log = Logger.getLogger(Combiner.class.getName());
    
    private List variants;
    private List assocs;
    private IdentityHashMap var2inv;
    
    /** Creates a new instance of Combiner */
    Combiner(List variants,List assocs) {
        this.variants = variants;
        this.assocs = assocs;
        this.var2inv = new IdentityHashMap();
        init();
    }
    
    private void init() {
        Iterator varIt = this.variants.iterator();
        while (varIt.hasNext()) {
            ASTVariant variant = (ASTVariant)varIt.next();
            Token invariant = null;
            String label = null;
            Iterator assocIt = this.assocs.iterator();
            while (assocIt.hasNext()) {
                IdentityHashMap var2inv = (IdentityHashMap)assocIt.next();
                invariant = (Token)var2inv.get(variant);
                //discard white-space invariants
                if (invariant!=null) {
                    if (label==null) label = invariant.getText();
                    if (!label.equals(invariant.getText())) {
                        log.fine("Discarding annotation of "+variant+" since it is associated "+
                                 "both to \""+label+"\" and \""+invariant.getText()+"\"");
                        label = null;
                        break;
                    }
                }
            }
            if (label!=null) {
                log.fine("Combined annotation of "+variant+" is \""+invariant+"\"");
                this.var2inv.put(variant, invariant);
            }
        }
        log.fine("Final annotations: "+this.var2inv);
    }
        
    public IdentityHashMap combine() {
        return this.var2inv;
    }
    
    
}
