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
 * TextExtractor.java
 *
 * Created on 28 gennaio 2004, 14.48
 */

package com.wolf_datamining.autoextracting.roadrunner.labeller;

import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.config.Constants;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;


public class LabelExtractor implements Constants {
    
    private boolean trimming;
    private int minLabelLength;
    private int maxLabelLength;
    
    /** Creates a new instance of TextExtractor */
    LabelExtractor() {
        this.trimming       = Config.getPrefs().getBoolean(TRIM_LABELS);
        this.maxLabelLength = Config.getPrefs().getInt(MAX_LABEL_LEN);
        this.minLabelLength = Config.getPrefs().getInt(MIN_LABEL_LEN);
    }
    
    private boolean isLabelTrimmingEnabled() {
        return this.trimming;
    }
    
    public String extractLabel(String s) {
        if (s==null || Util.isAllWhiteSpaceChars(s)) return null;
        String label = forceLabelLength(isLabelTrimmingEnabled() ? trimLabel(s) : s);
        return label;
    }
    
    static public String trimLabel(String s) {
        // Given a string extract a label by trimming punctuation chars
        StringBuffer result = new StringBuffer(s.length());
        //find first feasible char
        int start = -1, end = s.length();
        for (int i=0; i<s.length(); i++) {
            if (isLabelStart(s.charAt(i))) {
                start = i;
                break;
            }
        }
        if (start== -1) return ""; //no label to extract
        end = s.length()-1;
        for (int j = s.length()-1; j > start; j--) {
            if (isLabelEnd(s.charAt(j))) {
                end = j+1;
                break;
            }
        }
        return s.substring(start, end).trim();
    }
    
    private String forceLabelLength(String label) {
        // discard labels too short and truncate labels too long
        if (label.length()<minLabelLength) return null;
        if (label.length()>maxLabelLength) return null;
        return label;
    }
    
    static private boolean isLabelStart(char ch) {
        return Character.isUnicodeIdentifierStart(ch);
    }
    
     static private boolean isLabelEnd(char ch) {
        return Character.isUnicodeIdentifierPart(ch);
    }
}
