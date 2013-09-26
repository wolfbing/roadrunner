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
 * Region.java
 *
 * Created on 25 gennaio 2003, 12.01
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.List;

import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;


public class ExpressionRegion extends Region {
    
    private Expression exp;
    private Node.Path base; // The ASTAnd node which is the base of the region
    
    /** Creates a new instance of Region for AST */
    public ExpressionRegion(Expression exp, Node.Path base, int left, int right) {
        super(left,right);
        this.exp  = exp;
        this.base = base;
        checkRegion(exp,base,left,right);
    }
    
    /** Creates a new instance of Region for all children of an ASTAnd node */
    public ExpressionRegion(Expression exp, Node.Path base) {
        this(exp, base, 0, exp.getNode(base).jjtGetNumChildren());
    }
    
    public Expression getExpression() { return this.exp; }
    
    public boolean isFull() { return this.size() == getBase().jjtGetNumChildren(); }
    public ASTAnd getBase() { return getBase(getExpression());  }
    
    public ASTAnd getBase(Expression exp) {
        return (ASTAnd)exp.getNode(this.base);
    }
    
    public ExpressionRegion getBaseRegion() {
        return new ExpressionRegion(getExpression(),getBasePath());
    }
    
    public Node.Path getBasePath() {
        return this.base;
    }
    
    public Node.Path getAbsolutePath(Node.Path relative) {
        if (relative.depth()==0) return getBasePath();
        int relativeIndices[] = relative.indices();
        relativeIndices[0]+= this.getLeftBorderPos(); // pos -> index
        return getBasePath().absolute(new PathArray(relativeIndices));
    }
    
    public Region subRegion(int l, int r) {
        return new ExpressionRegion(getExpression(), getBasePath(), l, r);
    }
    
    public List asList() { return this.asList(this.exp); }
    
    public List asList(Expression exp) {
        // Cosi' si possono fare modifiche tramite regioni
        return this.getBase(exp).jjtGetChildren().subList(getLeftBorderPos(),getRightBorderPos());
    }
    
    public boolean isBorderRegion() {
        return getLeftBorderPos()==0 || getRightBorderPos()==getBase().jjtGetNumChildren();
    }
    
    private static void checkRegion(Expression exp, Node.Path basePath, int left, int right) {
        Node base = exp.getNode(basePath);
        if (!(base instanceof ASTAnd))
            throw new IllegalArgumentException("Base of region must be an ASTAnd node instead of "+base);
        int children = base.jjtGetNumChildren();
        checkBorders(children, left,right);
    }
      
    public boolean equals(Object o) {
        ExpressionRegion r = (ExpressionRegion)o;

        return this.getLeftBorderPos()==r.getLeftBorderPos() && this.getRightBorderPos()==r.getRightBorderPos() &&
               this.getBasePath().equals(r.getBasePath()) && this.exp.equals(r.exp);
    }
    
    public int hashCode() {
        return this.exp.hashCode() + this.getBasePath().hashCode() + this.getLeftBorderPos() + this.getRightBorderPos();
    }
    
}
