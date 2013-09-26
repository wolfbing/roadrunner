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
/*
 * Matrix.java
 *
 * Created on January 29, 2004, 9:57 PM
 * @author  Luigi Arlotta, Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.labeller;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;


public class Matrix {
    
    private List candidateVariants;   //these variants can be labelled
    private List candidateInvariants; //these invariants can be used as labels
    
    /** Creates a new instance of Matrix */
    public Matrix(Expression exp) {
        this.candidateVariants   = findCandidateVariants(exp);
        this.candidateInvariants = findCandidateInvariants(exp);
    }
    
    private List findCandidateVariants(Expression exp) {
        List candidates = new LinkedList();
        Iterator it = exp.getVariants().iterator();
        while (it.hasNext()) {
            ASTVariant variant = (ASTVariant)it.next();
            if (variant.getToken().isPCDATA()) {
                candidates.add(variant);
            }
        }
        return candidates;
    }
    
    private List findCandidateInvariants(Expression exp) {
        List candidates = new LinkedList();
        Iterator it = exp.getInvariants().iterator();
        while (it.hasNext()) {
            ASTToken invariant = (ASTToken)it.next();
            // check the candidate label 
            if (LabelExtractor.trimLabel(invariant.getText()).length()!=0) {
                candidates.add(invariant);
            }
        }
        return candidates;
    }
    
    public List getCandidateVariants()   { return this.candidateVariants;   }
    public List getCandidateInvariants() { return this.candidateInvariants; }
    
    public ASTVariant getVariant(int i) {
        return (ASTVariant)getCandidateVariants().get(i);
    }
    
    public ASTToken getInvariant(int j) {
        return (ASTToken)getCandidateInvariants().get(j);
    }
    
    public int getNumberOfVariants()   { return this.candidateVariants.size();   }
    public int getNumberOfInvariants() { return this.candidateInvariants.size(); }
    
}
