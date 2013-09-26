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

     Questo  programma  software libero;  lecito redistribuirlo  o
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
 * ASTConstants.java
 *
 * Created on 10 marzo 2003, 18.34
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.ast;

public interface ASTConstants {
    
    int JJTVOID    = 0;
    int JJTAND     = 1; final public String AND     = "and";
    int JJTPLUS    = 2; final public String PLUS    = "plus";
    int JJTHOOK    = 3; final public String HOOK    = "hook";
    int JJTTOKEN   = 4; final public String TOKEN   = "token";
    int JJTVARIANT = 5; final public String VARIANT = "variant";
    int JJTSUBTREE = 6; final public String SUBTREE = "subtree";
    
    
    public String[] jjtNodeName = {
        "void",
        AND,
        PLUS,
        HOOK,
        TOKEN,
        VARIANT,
        SUBTREE,
    };
    
    
    final public String EXPRESSION  = "expression";
    final public String PCDATA      = "pcdata";
    final public String TAG         = "tag";
    final public String ELEMENT     = "element";
    final public String ATTRIBUTES  = "attrs";
    final public String DEPTH       = "depth";
    final public String LABEL       = "label";
    
    final public String ATTRIBUTES_SEPARATOR     = ",";
    final public String ATTRIBUTE_NAME_VALUE_SEP = "=";
}
