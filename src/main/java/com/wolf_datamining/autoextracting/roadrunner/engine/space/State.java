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
/**
 * State.java
 *
 *
 * Created: Tue Dec 28 12:57:51 1999
 *
 * @author Valter Crescenzi
 * @version
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.engine.ExpressionIterator;


class State extends ExpressionIterator  {
    
    static private Logger log = Logger.getLogger(State.class.getName());
    
    /* This class contains all information to recover the state of the matching process */

    private SortedSet operators;
    private SearchSpace space;
    
    State(List mismatches, SearchSpace space) {
        this.space = space;
        log.finest("Space "+space.getId()+" - Creating operators for State "+getId());
        this.operators = space.getOperatorFactory().createOperators(mismatches);
        log.fine("Space "+space.getId()+" - State "+getId()+": there are "+operators.size()+" operator(s) to apply\n"+operators);
    }

    protected Expression computeNext() {
        while (!this.operators.isEmpty()) {
            Operator op = (Operator)(this.operators.first());
            log.finest("Space "+space.getId()+" - Evaluating operator "+op.getId()+" from State "+getId()+":\n"+op);
            if (op.hasNext()) {
                log.fine("Space "+space.getId()+" - Applying operator "+op.getId()+" from State "+getId()+":\n"+op);
                this.operators.add(op); // N.B. its order could change
                return op.next();
            }
            else {
                log.finest("Space "+space.getId()+" - Operator "+op.getId()+" from State "+getId()+" has expired");
                this.operators.remove(op);
            }
        }
        return null;
    }
    
} // State
