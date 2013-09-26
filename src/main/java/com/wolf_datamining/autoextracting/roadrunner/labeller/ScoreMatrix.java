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
 * ScoreMatrix.java
 *
 * Created on 7 dicembre 2003, 15.55
 * @author  Luigi Arlotta, Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.labeller;

import java.text.DecimalFormat;

class ScoreMatrix {
    
    private Double[][] scores; //scores[ i ][ j ] is score of label i as label of variant j
    private int numberOfNulls;
    
    private int minVarIndex;
    private int minInvIndex;

    private Double minimum;
    
    private Matrix matrix;
    
    private int numOfVar;
    private int numOfInv;
    
    /** Creates a new instance of ScoreMatrix */
    ScoreMatrix(Matrix m) {
        this.matrix = m;
        this.numOfVar = m.getNumberOfVariants();
        this.numOfInv = m.getNumberOfInvariants();
        this.scores = new Double[numOfInv][numOfVar];
        this.numberOfNulls = numOfInv*numOfVar; //initially all nulls
    }
    
    public Double getScore(int i, int j) {
        return this.scores[i][j];
    }
    
    public void setScore(Double score, int inv, int var) {
        Double old = this.scores[inv][var];
        if (old!=null && score==null) this.numberOfNulls++;
        if (old==null && score!=null) this.numberOfNulls--;
        this.scores[inv][var] = score;
    }
    
    public void deleteVariantScores(int j) {
        for(int i=0; i<this.matrix.getNumberOfInvariants(); i++) {
            setScore(null,i,j);
        }
    }
    
    public void deleteInvariantScores(int i) {
        for(int j=0; j<this.matrix.getNumberOfVariants(); j++) {
            setScore(null,i,j);
        }
    }
    
    public boolean isAllNull() {
        return (this.numberOfNulls == this.numOfVar*this.numOfInv);
    }
    
    
    public void findMinimum() {
        this.minimum = null;
        this.minVarIndex = -1;
        this.minInvIndex = -1;
        for(int var=0; var<this.numOfVar; var++) {
            for(int inv=0; inv<this.numOfInv; inv++) {
                Double score = this.getScore(inv,var);
                if (score!=null) {
                    if (minimum==null || score.doubleValue()<minimum.doubleValue()) {
                        this.minimum = score;
                        this.minVarIndex = var;
                        this.minInvIndex = inv;
                    }
                }
            }
        }
    }
    
    public int getMinVariantIndex() {
        return this.minVarIndex;
    }
    
    public int getMinInvariantIndex() {
        return this.minInvIndex;
    }
    
    public Double getMinimum() {
        return this.minimum;
    }
    
    public String toString() {
        return matrixHeading()+matrixBody();
    }
    
    private String matrixHeading() {
        StringBuffer result = new StringBuffer("         ");
        for(int v=0; v<this.numOfVar; v++) {
            result.append(pad(this.matrix.getVariant(v).getLabel(),6));
        }
        result.append("\n");
        return result.toString();
    }
    
    private String matrixBody() {
        StringBuffer result = new StringBuffer();
        final DecimalFormat formatter = new DecimalFormat("#0.0");
        for(int inv=0; inv<this.numOfInv; inv++) {
            result.append(pad(this.matrix.getInvariant(inv).getText(), 9));
            for(int var=0; var<scores[inv].length; var++) {
                Double score = scores[inv][var];
                if (score==null) result.append("  -  ");
                else result.append(pad(formatter.format(score.doubleValue()),5));
                result.append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }
    
    private static String pad(String s, int p) {
        StringBuffer result = new StringBuffer(p);
        result.append(s);
        for(int i=s.length(); i<p; i++) {
            result.append(" ");
        }
        return result.substring(0,p);
    }
    
}
