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
 * Walker.java
 *
 * Created on 22 gennaio 2003, 11.31
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.ListIterator;

import com.wolf_datamining.autoextracting.roadrunner.bidi.*;


public class Walker extends VisitorPlugger implements Visitor {
    
  private IteratorsStack iteratorsStack;
  protected Region region;
  
  /** Creates a new instance of Walker */
  protected Walker(Expression exp) {
      this(exp.asRegion());
  }
  protected Walker(Region region) {
    this(region, Direction.LEFT2RIGHT);
  }
  protected Walker(Region region, Direction dir) {
    super(dir);
    this.region = region;
    this.iteratorsStack = new IteratorsStack();
  }
  
  // N.B. to update correctly the iterators used by path getters, visitors deriving from this class
  // MUST invoke either directly or by means of methods
  // visitNode(node), visitChildren(node), visitList(list) inside VisitorPlugger  one of the following two methods 
  protected void startVisitingList(ListIterator lit) {
    this.iteratorsStack.register(lit);
  }
  protected void   endVisitingList(ListIterator lit) {
    this.iteratorsStack.unregister(lit);      
  }
  
 /**
   *  Return the absolute path of the node currently visited
   */  
  protected Node.Path getCurrentAbsolutePath() {
    return ((ExpressionRegion)this.region).getAbsolutePath(new PathArray(this.iteratorsStack.makeIndices()));
  }

 /**
   *  Return the path relative to the region visited
   */  
  protected Node.Path getCurrentRelativePath() {
    return new PathArray(this.iteratorsStack.makeIndices());
  }
}
