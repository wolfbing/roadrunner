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
 * VisitorPlugger.java
 *
 * Created on 16 ottobre 2003, 9.34
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;


import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.bidi.*;


public class VisitorPlugger implements Visitor {
    
    /** Creates a new instance of VisitorPlugger */
    
    protected Direction dir;
    
    protected VisitorPlugger() {
        this(Direction.LEFT2RIGHT);
    }
    protected VisitorPlugger(Direction dir) {
        this.dir = dir;
    }
    
    public boolean visit(Expression exp) {
        return visit(exp.getRoot());
    }
    
    public boolean visit(Region region) {
        return visitList(region.asList());
    }
    
    public boolean visit(NodeAdapter node) {
        throw new RuntimeException("This method shouldn't be called on "+node.getClass());
    }
    
    public boolean visit(ASTToken node)   {  return visitNode(node);       }
    public boolean visit(ASTVariant node) {  return visitNode(node);       }
    public boolean visit(ASTSubtree node) {  return visitNode(node);       }
    public boolean visit(ASTHook node)    {  return visitNode(node);       }
    public boolean visit(ASTPlus node)    {  return visitNode(node);       }
    public boolean visit(ASTAnd node)     {  return visitNode(node);       }
    
    protected boolean visitNode(Node node){   return visitChildren(node);  }
    
    protected boolean visitChildren(Node node) {
        return visitList(node.jjtGetChildren());
    }
    
    protected boolean visitList(List list) {
        // N.B. this is the only method which is invoked on all nodes; visitNode() and visitChildren are not invoked on node of base region
        boolean result = true;
        ListIterator it = BidirectionalListFactory.newListIterator(list, this.dir);
        startVisitingList(it);
        while (it.hasNext()) {
            Node node = (Node)it.next();
            result = node.jjtAccept(this) && result; // AND logic for result
        }
        endVisitingList(it);
        return result;
    }
    protected void startVisitingList(ListIterator it) {};
    protected void   endVisitingList(ListIterator it) {};
    
}
