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
/**
 * Constants.java
 *
 *
 * Created: 
 *
 * @author Valter Crescenzi
 * @version
 */
package com.wolf_datamining.autoextracting.roadrunner.config;

import java.lang.reflect.*;

/**
 * All constants used in defaults.xml and by Config.java
 */

public interface Constants {

  //Options for the lexical pre-processor
  static final public String FREE_TEXT     = "freetext";         //switch for free-text detection
  static final public String FREETEXT_TAGS = "freetextTags";     //non-leaf tags marking presentation
  static final public String VARIANT_TAGS  = "variantTags";      //leaf tags marking presentation
  static final public String KEEP_TAGS     = "keepTags";         //keep track of tags in free-text chuncks
  
  static final public String IGNORE_TREES  = "skipTrees";        //subtrees of DOM to ignore
  static final public String IGNORE_TAGS   = "skipTags";         //tags to ignore
  static final public String IGNORE_ATTS   = "skipAttributes";   //attributes to ignore
  static final public String ATTS_VALUES   = "attributeValues";  //attribute whose value is considered as schema
  //Options for the inference engine
  static final public String MAX_STATES    = "maxStates";        //max number of states in a seach space
  static final public String FREE_PLUS     = "freePlus";         //pluses free to be close to hooks
  static final public String HOOK          = "useHooks";         //use hooks
  static final public String PLUS          = "usePluses";        //use pluses
  static final public String SUBTREE       = "useSubtrees";      //use subtree
  static final public String MAX_NUM_OCCUR = "occurrences";      //max number of occurrences
  static final public String EBACKTRACKING = "backtracking";     //external backtracking
  static final public String CBACKTRACKING = "collapsingbt";     //backtracking on collapsing
  static final public String AMBIGUITY     = "ambiguity";        //consider ambiguity mismatches
  static final public String LLK           = "LLk";              //expressions must be LL(k) in bot
  static final public String RRK           = "RRk";              //expressions must be RR(k) in both directions
  static final public String STEP0         = "step0";            //use all-subtrees solution as starting expression  
  static final public String SOLUTIONS     = "solutions";        //max number of solution (0 is all)
  
  //Options for labelling attributes
  static final public String PREFIX_ENABLED= "prefixLabelling";  //labelling by common prefix/suffix enabled
  static final public String VISUAL_ENABLED= "visualLabelling";  //labelling by visual proximity enabled
  static final public String TRIM_LABELS   = "trimLabels";       //trim punctuation char from labels
  static final public String MIN_LABEL_LEN = "minLabelLen";      //min length of labels
  static final public String MAX_LABEL_LEN = "maxLabelLen";      //max length of labels
  static final public String MAX_DISTANCE  = "maxDistance";      //max distance from a label and its values
  static final public String MAX_SCORE     = "maxScore";         //max score label/value
  static final public String MAX_ALPHA     = "maxAlpha";         //max angle label/value
  
  //Options for producing & displaying results
  static final public String INDEXSTYLE     = "index_style";      //style sheet to view index of wrappers xml output
  static final public String DATASETSTYLE   = "dataset_style";    //style sheet to view dataset xml output
  static final public String BROWSER        = "browser";          //browser command to display output
  static final public String OUTPUTDIR      = "outputdir";        //directory to store results
  
  public interface KeysGetter extends Constants {
    public String[] asArray();
  }
  
  static final public KeysGetter keys = new KeysGetter() {
    public String[] asArray() {      
      Field[] fields = getClass().getFields();
      String[] result = new String[fields.length-1];
      Class StringClass = "".getClass(); // Class of java.lang.String
      int j=0,i=0;
      while (i<fields.length) {
        if (fields[i].getType()==StringClass)  {
          result[j++] = fields[i].getName();
        }
        i++;
      }
      return result;   
    }
  };
  
}
