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

     Questo  programma software libero;   lecito redistribuirlo  o
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
 * PrefixLabeller.java
 *
 * Created on 2 dicembre 2003, 12.32
* @author  Luigi Arlotta, Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.labeller;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.Instance;
import com.wolf_datamining.autoextracting.roadrunner.Wrapper;
import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;


class PrefixLabeller {
    
    private Wrapper wrapper;
    private Set instances;
    
    private IdentityHashMap var2label;
    
    /** Creates a new instance of PrefixLabeller */
    PrefixLabeller(Wrapper wrapper, Set instances) {
        this.wrapper = wrapper;
        this.instances = instances;
        this.var2label = new IdentityHashMap();
        this.init();
    }
    
    private void init() {
        Expression exp = this.wrapper.getExpression();
        List variants = exp.getVariants();
        Iterator it = variants.iterator();
        while (it.hasNext()) {
            ASTVariant node = (ASTVariant)it.next();
            List values = findValuesOfVariant(node);
            String label = getLongestCommonPrefix(values);
            if (Util.isAllWhiteSpaceChars(label)) label = getLongestCommonSuffix(values);
            if (!Util.isAllWhiteSpaceChars(label)) var2label.put(node, label);
        }
    }
    
    private List findValuesOfVariant(ASTVariant variant) {
        List result = new LinkedList();
        Iterator it = this.instances.iterator();
        while (it.hasNext()) {
            Instance instance = (Instance)it.next();
            IdentityHashMap token2node = instance.getTokens2Nodes();
            result.addAll(Util.getKeys(token2node,  variant));
        }
        return result;
    }
    
    private interface BinaryOperator {
        String applyTo(String s1, String s2);
    }
    
    private String getLongestCommonPrefix(List tokens) {
        BinaryOperator lcp = new BinaryOperator() {
            public String applyTo(String s1, String s2) {
                return getLongestCommonPrefix(s1,s2);
            }
        };
        return associativeOperator(tokens, lcp);
    }
    private String getLongestCommonSuffix(List tokens) {
        BinaryOperator lcs = new BinaryOperator() {
            public String applyTo(String s1, String s2) {
                return getLongestCommonSuffix(s1,s2);
            }
        };
        return associativeOperator(tokens, lcs);
    }
    
    private String associativeOperator(List tokens, BinaryOperator op) {
        String result = "";
        Token token = null;
        Iterator it = tokens.iterator();
        while (it.hasNext() && result.equals("")) { //find first non-null token
            token = (Token)it.next();
            if (token.getVariantValue()==null) continue; //skip null-token
            result = token.getVariantValue().trim();
        }
        while (it.hasNext() && !result.equals("")) {
            token = (Token)it.next();
            if (token.getVariantValue()==null) continue; //skip null-token
            String string = token.getVariantValue().trim();
            result = op.applyTo(result,string);
        }
        return result;
    }
    
    
    private static String getLongestCommonPrefix(String string1, String string2) {
        int i;
        for (i=0; i<string1.length() && i<string2.length(); i++) {
            if (string1.charAt(i) != string2.charAt(i) ) break;
        }
        return string1.substring(0,i);
    }
    private static String getLongestCommonSuffix(String string1, String string2) {
        int i,j;
        for (i=string1.length()-1,j=string2.length()-1; i>0 && j>0; i--,j--) {
            if (string1.charAt(i) != string2.charAt(j)) 
                return string2.substring(j+1);                
        }
        return string1.substring(i);
    }
    
    public String getLabel(ASTVariant node) {
        return (String)this.var2label.get(node);
    }
}
