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
 * Util.java
 *
 * Created on 
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.util;

import java.io.*;
import java.util.*;

public class Util {
    
    
    public static File searchInRRHOME(String filename) throws FileNotFoundException {
        String paths = System.getProperty("rr.home",".");
        StringTokenizer tokenizer = new StringTokenizer(paths,File.pathSeparator);
        File file = null;
        while (tokenizer.hasMoreTokens()) {
            String path = tokenizer.nextToken();
            file = new File(path,filename);
            if (file.canRead()) return file.getAbsoluteFile();
        }
        throw new FileNotFoundException("Required file "+ filename +"cannot be read from "+ paths);
    }
    
    static public void copyFile(File from , File to) throws IOException {
        Reader reader = new BufferedReader(new FileReader(from));
        Writer writer = new BufferedWriter(new FileWriter(to));
        int ch;
        while ((ch=reader.read())!=-1) {
            writer.write(ch);
        }
        reader.close(); writer.close();
    }
    
    static public void reverseMapping(Map value2keys, Map map) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            Object value = entry.getValue();
            Object key = entry.getKey();
            List keys = (List)value2keys.get(value);
            if (keys==null) keys = new LinkedList();
            keys.add(key);
            value2keys.put(value, keys);
        }
    }
    
    static public List getKeys(IdentityHashMap map, Object value) {
        //Cannot use:
        //Map clone = (Map)map.clone();
        //clone.values().retainAll(Collections.singleton(value));
        //return clone.keySet();
        // because identity on view is based on equals method
        List result = new LinkedList();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            if (entry.getValue()==value)
                result.add(entry.getKey());
        }
        return result;
    }
    
    public static boolean isAllWhiteSpaceChars(String string) {
        final char nbsp = (char)160;
        int len = string.length();
        for (int h=0; h<len; h++) {
            char ch = string.charAt(h);
            if (!(Character.isWhitespace(ch) || ch==nbsp)) return false;
        }
        return true;
    }
    //GRAZIE A NEKOHTML QUESTA DOVREBBE ESSERE DIVENTATA UNICA
    static public String escapeChars4XML(String s) {
        int len = s.length();
        StringBuffer result = null;
        for(int i=0; i<len; i++) {
            char ch = s.charAt(i);
            if (result==null) {
                switch(ch) {
                    case '&':
                    case '=':
                    case '<':
                    case '>':
                    case '\u00a0':
                        result = new StringBuffer(s.substring(0,i));
                }
            }
            if (result!=null) {
                switch(ch) {
                    case '&':
                        result.append("&amp;");
                        break;
                    case '=':
                        result.append("%3d");
                        break;
                    case '<':
                        result.append("&lt;");
                        break;
                    case '>':
                        result.append("&gt;");
                        break;
                    case '\u00a0':
                        result.append(" ");
                        break;
                    default:
                        result.append(ch);
                }
            }
        }
        return (result==null ? s : result.toString());
    }
    
}



