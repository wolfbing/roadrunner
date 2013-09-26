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
 * Cartographer.java
 * Given an instance, this class finds all boxes of variants and invariants occurrences
 * Created on 7 dicembre 2003, 15.21
 * @author  Valter Crescenzi, Luigi Arlotta
 */

package com.wolf_datamining.autoextracting.roadrunner.labeller;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.Instance;
import com.wolf_datamining.autoextracting.roadrunner.Sample;
import com.wolf_datamining.autoextracting.roadrunner.ast.Node;
import com.wolf_datamining.autoextracting.roadrunner.parser.*;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;


class Cartographer {
    
    private final static int TOO_CLOSE_THRESHOLD = 5;
    
    private IdentityHashMap variant2boxes;
    private IdentityHashMap invariant2boxes;
    private Instance instance;
    private Matrix matrix;

    /** Creates a new instance of Cartographer */
    Cartographer(Matrix m, Instance instance) {
        this.matrix = m;
        this.instance = instance;
        init();
    }
    
    private void init() {
        try {
            Sample sample  = instance.getSample();
            IdentityHashMap token2box = sample.getTokens2BoundingBoxes();
            Binder binder = new Binder(instance.getWrapper().getExpression());
            binder.setBindings(sample.getTokenlist());
            IdentityHashMap token2node = binder.getTokenBindings();
            this.variant2boxes = getNodes2Boxes(matrix.getCandidateVariants(), token2node, token2box);
            this.invariant2boxes = getNodes2Boxes(matrix.getCandidateInvariants(), token2node, token2box);
        }
        catch(BindingException be) {
            System.err.println("Bad Instance object!");
            System.err.println("The sample and the exp. of instance does not match: ");
            System.err.println(be.getMismatches());
            be.printStackTrace();
            System.exit(-1);
        }
    }
    
    public IdentityHashMap getVariant2Boxes() {
        return this.variant2boxes;
    }
    public IdentityHashMap getInvariant2Boxes() {
        return this.invariant2boxes;
    }
    
    private IdentityHashMap getNodes2Boxes(List nodes, IdentityHashMap token2node, IdentityHashMap token2box) {
        IdentityHashMap result = new IdentityHashMap(nodes.size());
        Iterator nIt = nodes.iterator();
        while (nIt.hasNext()) {
            Node node = (Node)nIt.next();
            SortedSet boxes = getBoxesOfNodeOccurrences(node, token2node, token2box);
            result.put(node,boxes);
        }
        return result;
    }
    
    private SortedSet getBoxesOfNodeOccurrences(Node node, IdentityHashMap token2node, IdentityHashMap token2box) {
        List datavalues = Util.getKeys(token2node,node);
        Iterator dvIt = datavalues.iterator();
        SortedSet boxes = new TreeSet();
        while (dvIt.hasNext()) {
            Token datavalue = (Token)dvIt.next();

            Box box = (Box)token2box.get(datavalue);
            if (box!=null) boxes.add(box); //not all text chunks are rendered (e. g. titles and head stuff)
        }
        return boxes;
    }
    
    public SortedSet getBoxesOfVariant(int var) {
        return (SortedSet)getVariant2Boxes().get(this.matrix.getVariant(var));
    }
    
    public SortedSet getBoxesOfInvariant(int inv) {
        Token invariant = this.matrix.getInvariant(inv);
        return (SortedSet)getInvariant2Boxes().get(invariant);
    }
    
    public Box getUpperLeftBoxOfVariant(int var) {
        SortedSet boxes = getBoxesOfVariant(var);
        if (boxes.isEmpty()) return null;
        return (Box)boxes.first();
    }
    
    public Box getUpperLeftBoxOfInvariant(int inv) {
        SortedSet boxes = getBoxesOfInvariant(inv);
        if (boxes.isEmpty()) return null;
        return (Box)boxes.first();
    }
    
    public boolean isThereSomethingInBetween(int inv, int var) {
        Box varBox = getUpperLeftBoxOfVariant(var);
        Box invBox = getUpperLeftBoxOfInvariant(inv);
        // discard limit cases where two boxes are adjacent somehow
        if (varBox.distance(invBox)<=TOO_CLOSE_THRESHOLD) return false;
        int nInv = this.matrix.getNumberOfInvariants();
        for(int i=0; i<nInv; i++) {
            if (i==inv) continue;
            if (isThereBoxInBetween(invBox, varBox, getBoxesOfInvariant(i)))   
                return true;
        }
        int nVar = this.matrix.getNumberOfVariants();
        for(int v=0; v<nVar; v++) {
            if (v==var) continue;
            if (isThereBoxInBetween(invBox, varBox, getBoxesOfVariant(v)))  
                return true;
        }
        return false;
    }
    
    private boolean isThereBoxInBetween(Box box1, Box box2, SortedSet boxes) {
        Iterator it = boxes.iterator();
        while (it.hasNext()) {
            Box box = (Box)it.next();
            if (box.isInBetween(box1,box2))
                return true;
        }
        return false;
    }
}
