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
 * Visitor.java
 * java.util.List based implementation of Composite pattern
 * Created on 
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;


public class SimpleNode extends NodeAdapter implements Node, Cloneable {
    
    private List children;
    
    protected SimpleNode(int id) {
        super(id);
        this.children = makeChildren();
    }
    
    protected SimpleNode(int id, List children) {
        super(id);
        this.children = children;
    }
    
    final private void checkIndex(int i) {
        if (i<0 || i>=children.size())
            throw new NoSuchNodeException("The child number "+i+" doesn\'t exist!");
    }
    
    public Node jjtGetChild(int i) {
        checkIndex(i);
        return (Node)children.get(i);
    }
    
    public int jjtGetNumChildren() {
        return children.size();
    }
    
    public List jjtGetChildren() {
        return this.children;
    }

    public Node clone(boolean deep) {
        SimpleNode clone=null;
        try {
            clone = (SimpleNode)this.clone();
            if (deep) {
                clone.children = makeChildren();
                Iterator it = this.children.iterator();
                while (it.hasNext()) {
                    Node child = (Node)it.next();
                    clone.children.add(child.clone(deep));
                }
            }
            else clone.children = makeChildren(this.children);
        }
        catch (CloneNotSupportedException cnse) {
            System.out.print(cnse.getMessage());
            cnse.printStackTrace();
            System.exit(-1);
        }
        return clone;
    }
    
    private List makeChildren()          {  return new ArrayList();     }
    private List makeChildren(List kids) {  return new ArrayList(kids); }
    
}
