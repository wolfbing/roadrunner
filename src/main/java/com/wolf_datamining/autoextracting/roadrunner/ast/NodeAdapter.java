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
 * NodeAdapter.java
 *
 * Created on 10 marzo 2003, 12.32
 * An adapter for leaf nodes (Plugger would be a better name?)
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.List;
import java.util.ListIterator;
import java.util.Collections;

import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;


public abstract class NodeAdapter implements Node, Cloneable {
  
  protected int id;
 
  protected NodeAdapter(int i) {
    this.id =  i;
  }
  
  protected int getTypeId() {
      return this.id;
  }
  
  public String getType() {
    return ASTConstants.jjtNodeName[this.id];
  }  
 
  public List jjtGetChildren() {
    return Collections.EMPTY_LIST;
  }
  
  public int jjtGetNumChildren() {
    return 0;
  }
  
  public Node jjtGetChild(int i) {
    throw new NoSuchNodeException("The child number "+i+" doesn\'t exist!");
  }
  
  /** Accept the visitor.  */
  public boolean jjtAccept(Visitor visitor) {
    throw new RuntimeException("This method should never be called!");
  }
  
  /** Accept the visitor. **/
  public boolean childrenAccept(Visitor visitor) {
    boolean result = true; //for result AND logic
    List children = jjtGetChildren();
    if (children != null) {
      ListIterator i = children.listIterator();
      while (i.hasNext()) {
        Node n = (Node)i.next();
        if (!n.jjtAccept(visitor)) result = false;
      }
    }
    return result;
  }
  
  public Node jjtGetNode(Node.Path path) {
    Node result = this;
    int indices[] = path.indices();
    for(int i=0; i<indices.length; i++) {
      result = result.jjtGetChild(indices[i]);
    }
    return result;
  }
  
  
  public Node clone(boolean deep) {
    NodeAdapter clone=null;
    try {
      clone = (NodeAdapter)clone();
    }
    catch (CloneNotSupportedException cnse) {
      System.out.print(cnse.getMessage());
      cnse.printStackTrace();
      System.exit(-1);
    }
    return clone;
  }
   
  /* Override this method if you want to customize how the node dumps out its children. */
  public String dump() {
    return dump(new Indenter(true));
  }
  public String dump(Indenter ind) {
    StringBuffer out = new StringBuffer();
    dump(out,ind);
    return out.toString();
  }
  
  private void dump(StringBuffer out, Indenter ind) {
    out.append(ind.toString()+this+"\n");
    ListIterator it = this.jjtGetChildren().listIterator();
    ind.inc();
    while (it.hasNext()) {
      NodeAdapter n = (NodeAdapter)it.next();
      n.dump(out, ind);
    }
    ind.dec();
  }
  
  public boolean equals(Object o) {
      NodeAdapter that = (NodeAdapter)o;
      return this.getTypeId() == that.getTypeId() && this.jjtGetChildren().equals(that.jjtGetChildren());
  }
  public int hashCode() {
      return this.getTypeId()+jjtGetChildren().hashCode();
  }
  
  public String toString() { return ASTConstants.jjtNodeName[id]; }
}
