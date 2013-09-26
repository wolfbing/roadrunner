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
 * LLChecker.java
 *
 * Created on 4 ottobre 2003, 10.23
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine;

import java.util.List;
import java.util.Iterator;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.PrefixHunter;
import com.wolf_datamining.autoextracting.roadrunner.ast.Visitor;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;



public class LLChecker extends PrefixHunter implements Visitor {
    
    static private Logger log = Logger.getLogger(LLChecker.class.getName());
    
    /** Creates a new instance of LLChecker */    
    public LLChecker(int k, Direction dir) {
        super(k,dir);
    }           
    
    public  boolean existsAcommonPrefix(Region r1, Region r2) {
        if (getK()==-1) return false;
        return existsAcommonPrefix(getPrefixes(r1), getPrefixes(r2));
    }
    
    private boolean existsAcommonPrefix(List prefixes1, List prefixes2) {
        Iterator it1 = prefixes1.iterator();
        while (it1.hasNext()) {
            List prefix1 = (List)it1.next();
            Iterator it2 = prefixes2.iterator();
            while (it2.hasNext()) {
                List prefix2 = (List)it2.next();
                if (matches(prefix1, prefix2)) {
                   return true;
                }
            }
        }
        return false;
    }
    
    private boolean matches(List prefix1, List prefix2) {
        Iterator it1 = prefix1.iterator();
        Iterator it2 = prefix2.iterator();
        while (it1.hasNext()) {
            Token token1 = (Token)it1.next();
            if (token1 == null) return true; //null stands for subtree
            if (it2.hasNext()) {
                Token token2 = (Token)it2.next();
                if (token2 == null) return true;
                if (!token1.matches(token2)) return false;
            }
            else return true;
        }
        return true;
    }
    
}
