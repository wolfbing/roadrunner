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
 * PrefixHunter.java
 *
 * Created on 4 ottobre 2003, 10.23
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.bidi.*;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;


public class PrefixHunter extends VisitorPlugger implements Visitor {
    
    static private Logger log = Logger.getLogger(PrefixHunter.class.getName());
    
    static public Node getFirstLeaf(Direction dir, Node node) {
        while (node.jjtGetNumChildren()>0) {
            List kids = node.jjtGetChildren();
            node = (Node)BidirectionalListFactory.newListView(dir, kids).getFirst();
        }
        return node;
    }
    
    /** Creates a new instance of PrefixHunter */
    private int k;
    private List currentPrefixes;
    private List finalPrefixes;
    
    public PrefixHunter(int k, Direction dir) {
        super(dir);
        this.k = k;
        this.currentPrefixes = new LinkedList();
    }
    
    public int getK() {
        return this.k;
    }
    
    public List getPrefixes(Region region) {
        this.finalPrefixes = new LinkedList();
        this.currentPrefixes.add(new ArrayList());
        visit(region);
        this.finalPrefixes.addAll(this.currentPrefixes); // these are shorter than k tokens
        this.currentPrefixes.clear();
        return this.finalPrefixes;
    }
    
    /* methods return true if prefixes are not completed (length < k) */
    public boolean visit(ASTAnd node) {
        boolean finished = false;
        ListIterator it = BidirectionalListFactory.newListIterator(node.jjtGetChildren(), this.dir);
        while (it.hasNext() && !finished) {
            Node child = (Node)it.next();
            finished = child.jjtAccept(this);
        }
        return finished;
    }
    public boolean visit(ASTPlus node) {
        boolean finished = visitNode(node);
        if (!finished) {
            List wasCurrentPrefixes = getCurrentPrefixesClone();
            visitNode(node);
            this.currentPrefixes.addAll(wasCurrentPrefixes); // prefixes for case in which plus node doesn't' match once more
            return false; //not finished: there are these ^ prefixes to complete
        }
        return true;
    }
    public boolean visit(ASTHook node) {
        List wasCurrentPrefixes = getCurrentPrefixesClone();
        visitNode(node);
        this.currentPrefixes.addAll(wasCurrentPrefixes); // prefixes for case in which hook node doesn't match
        return false; //not finished: there are these ^ prefixes to complete
    }
    public boolean visit(ASTSubtree node) {
        return addTokenToPrefixes(null);
    }
    public boolean visit(ASTVariant node) {
        return addTokenToPrefixes(node.getToken());
    }
    public boolean visit(ASTToken node) {
        return addTokenToPrefixes(node);
    }
    
    private List getCurrentPrefixesClone() {
        List result = new LinkedList();
        Iterator it = this.currentPrefixes.iterator();
        while (it.hasNext()) {
            List prefix = (List)it.next();
            result.add(new ArrayList(prefix));
        }
        return result;
    }
    
    private boolean addTokenToPrefixes(Token token) {
        boolean finished = true;
        Iterator it = this.currentPrefixes.iterator();
        while (it.hasNext()) {
            List prefix = (List)it.next();;
            prefix.add(token);
            if (prefix.size()==k) {
                this.finalPrefixes.add(prefix);
                it.remove();
            }
            else finished = false;
        }
        return finished;
    }
}
