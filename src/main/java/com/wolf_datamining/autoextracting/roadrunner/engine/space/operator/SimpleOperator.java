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
 *  SimpleOperator.java
 *
 * Created on 23 marzo 2003, 15.09
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine.space.operator;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.engine.ExpressionIterator;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.Operator;
import com.wolf_datamining.autoextracting.roadrunner.engine.space.SearchSpace;
import com.wolf_datamining.autoextracting.roadrunner.parser.MismatchPoint;
import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;


abstract class SimpleOperator extends ExpressionIterator implements Operator {
    
    protected Direction dir;
    protected MismatchPoint mismatch;
    protected boolean expired;
    protected SearchSpace space;
    
    /** Creates a new instance of SimpleOperator */
    protected SimpleOperator(MismatchPoint mismatch) {
        this.mismatch = mismatch;
        this.dir = this.mismatch.getDir();
        this.expired = false;
        this.space = mismatch.getSearchSpace();
    }
    
    protected MismatchPoint getMismatch() {
        return this.mismatch;
    }
        
    abstract Expression compute();
    
    abstract ExpressionRegion getExtension(); // Region which will be changed by this operator
    
    protected Expression computeNext() {
        if (this.expired) return null;
        else return compute();
    }
    
    boolean isExpired() {
        return this.expired;
    }
    
    String toString(Indenter ind) {
        return ind+this.mismatch.toString()+": "+this.getClass().getName();
    }
    
    abstract public int h();
    
    public int compareTo(Object o) {
        // low h() is better
        SimpleOperator op = (SimpleOperator)o;
        int result = this.h()-op.h();
        return (result!=0 ? result : this.id-op.id);// be consistent with equals()
    }
    
    public String toString() {
        Indenter ind = new Indenter(0,this.dir.toString()+" ",false);
        return toString(ind);
    }
    
}
