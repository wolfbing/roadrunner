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
 * VisualLabeller.java
 *
 * Created on 2 dicembre 2003, 12.49
 * @author  Valter Crescenzi, Luigi Arlotta
 */

package com.wolf_datamining.autoextracting.roadrunner.labeller;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.Instance;
import com.wolf_datamining.autoextracting.roadrunner.Wrapper;
import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.config.Constants;



class VisualLabeller {
    
    private static Logger log = Logger.getLogger(VisualLabeller.class.getName());
    
    private Matrix matrix;
    private Set instances;
    
    private IdentityHashMap var2inv;
    
    private int maxDistance;
    private double maxAlpha;
    private double maxScore;
    
    /** Creates a new instance of VisualLabeller */
    VisualLabeller(Wrapper wrapper, Set instances) {
        this.matrix = new Matrix(wrapper.getExpression());
        this.instances = instances;
        this.maxDistance = Config.getPrefs().getInt(Constants.MAX_DISTANCE);
        this.maxAlpha = Config.getPrefs().getDouble(Constants.MAX_ALPHA);
        this.maxScore = Config.getPrefs().getDouble(Constants.MAX_SCORE); 
        this.init();
    }
        
    private void init() {
        Iterator it = this.instances.iterator();
        List allInstancesAssociations = new ArrayList(this.instances.size());
        while (it.hasNext()) {
            Instance instance = (Instance)it.next();
            log.fine("Computing Score Matrix for instance of "+instance.getSample());
            IdentityHashMap associations = computeAssociations(instance);
            log.fine("Associations for instance of "+instance.getSample()+":");
            log.fine(associations.toString());
            allInstancesAssociations.add(associations);
        }
        // merge results from several instances
        Combiner combiner = new Combiner(this.matrix.getCandidateVariants(), allInstancesAssociations);
        this.var2inv = combiner.combine();
    }
    
    private IdentityHashMap computeAssociations(Instance instance) {
        ScoreMatrix matrix = getMatrixScores(instance);
        log.finer("Score Matrix is:\n "+matrix.toString());
        return findAssociations(matrix);
    }
    
    private ScoreMatrix getMatrixScores(Instance instance) {
        ScoreMatrix m = new ScoreMatrix(this.matrix);
        Cartographer grapher = new Cartographer(this.matrix,instance);
        int nVar = this.matrix.getNumberOfVariants();
        int nInv = this.matrix.getNumberOfInvariants();
        for(int var=0; var<nVar; var++) {
            Box varBox = grapher.getUpperLeftBoxOfVariant(var);
            if (varBox==null) continue;
            for(int inv=0; inv<nInv; inv++) {
                Box invBox = grapher.getUpperLeftBoxOfInvariant(inv);
                if (invBox==null) continue;
                if ( isAllowedDistance(varBox,invBox) &&  isAboveOrLeft(invBox, varBox)  && !isOblique(invBox, varBox)) {
                    Double score = new java.lang.Double(score(invBox, varBox));
                    if (this.checkScoreThreshold(score.doubleValue()))
                        m.setScore(score, inv, var);
                }
            }
        }
        checkInBetweenBoxes(grapher,m);
        return m;
    }
    
    private boolean isAllowedDistance(Box invBox, Box varBox) {
        return (varBox.distance(invBox) < this.maxDistance);
    }
    
    private boolean isAboveOrLeft(Box invBox, Box varBox) {
        return !(varBox.isAbove(invBox) || varBox.isOnLeft(invBox));
    }
    
    private boolean isOblique(Box invBox, Box varBox) {
        return Box.alignment(invBox, varBox) > this.maxAlpha ;
    }
    
    private boolean checkScoreThreshold(double score) {
        return score <= this.maxScore ;
    }
        
    private void checkInBetweenBoxes(Cartographer grapher, ScoreMatrix m) {
        int nVar = this.matrix.getNumberOfVariants();
        int nInv = this.matrix.getNumberOfInvariants();
        for(int var=0; var<nVar; var++) {
            for(int inv=0; inv<nInv; inv++) {
                if (m.getScore(inv, var)!=null) {
                    if (grapher.isThereSomethingInBetween(inv, var)) {
                        log.finer("Something in between"+
                        "\tinv:"+this.matrix.getInvariant(inv)+
                        "\tvar:"+this.matrix.getVariant(var));
                        m.setScore(null, inv, var);
                    }
                }
            }
        }
    }
    
    private double score(Box invBox, Box varBox) {
        //  0 <= (Box.distance(invBox, varBox) / maxDistance) <= 1
        //  0 <= Math.sin( 2 * Box.alignment(invBox, varBox)) <= 1
        return  (Box.distance(invBox, varBox) / maxDistance) * (Math.sin( 2 * Box.alignment(invBox, varBox)));
    }
    
    private IdentityHashMap findAssociations(ScoreMatrix matrix) {
        IdentityHashMap associations = new IdentityHashMap();
        do {
            findBestAssociation(matrix, associations);
        } while (!matrix.isAllNull());
        return associations;
    }
    
    private void findBestAssociation(ScoreMatrix m, IdentityHashMap assoc) {
        m.findMinimum();
        if (m.getMinimum()!=null) {
            int minVarIndex = m.getMinVariantIndex();
            int minInvIndex = m.getMinInvariantIndex();
            ASTVariant variant = this.matrix.getVariant(minVarIndex);
            ASTToken invariant  = this.matrix.getInvariant(minInvIndex);
            log.finer("Variant "+variant+" associated to \""+invariant+"\"");
            assoc.put(variant, invariant);
            m.deleteVariantScores(minVarIndex); m.deleteInvariantScores(minInvIndex);
        }
    }
    
    public String getLabel(ASTVariant node) {
        ASTToken invariant = (ASTToken)this.var2inv.get(node);
        return (invariant!=null ? invariant.getText() : null);
    }
    
}