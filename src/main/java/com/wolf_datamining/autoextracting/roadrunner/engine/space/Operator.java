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

     Questo  programma �  software libero; �  lecito redistribuirlo  o
     modificarlo secondo i termini della Licenza Pubblica Generica GNU
     come � pubblicata dalla Free Software Foundation; o la versione 2
     della licenza o (a propria scelta) una versione successiva.

     Questo programma  � distribuito nella speranza che sia  utile, ma
     SENZA  ALCUNA GARANZIA;  senza neppure la  garanzia implicita  di
     NEGOZIABILIT�  o di  APPLICABILIT� PER  UN PARTICOLARE  SCOPO. Si
     veda la Licenza Pubblica Generica GNU per avere maggiori dettagli.

     Questo  programma deve  essere  distribuito assieme  ad una copia
     della Licenza Pubblica Generica GNU; in caso contrario, se ne pu�
     ottenere  una scrivendo  alla:

     Free  Software Foundation, Inc.,
     59 Temple Place, Suite 330,
     Boston, MA 02111-1307 USA

*/
package com.wolf_datamining.autoextracting.roadrunner.engine.space;

/**
 * Operator.java
 *
 *
 * Created: Thu Dec 21 09:57:49 2000
 *
 * @author Valter Crescenzi
 * @version
 */

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;

public interface Operator extends Comparable {

  /** Questa interfaccia deve supportare operatori che restituiscono soluzioni multiple;
   *  in particolare, mentre con gli addHook � possibile prevedere il numero di soluzioni
   *  che possono essere prodotte contando il numero di occorrenze del delimitatore. Nel
   *  Caso dell'addPlus � impossibile. Nel primo caso si possono costruire oggetti diversi
   *  per ciascuna soluzione e rientrare nel modello classico, nel secondo caso no.
   **/
    
  public int getId();
  
  public Expression next();
  
  public boolean hasNext();
  
  public int h();
  
  public int compareTo(Object o);

  /** Sull'ordine degli operatori: permettere agli operatori con square multiplo di precedere
   * quelli a square semplice comporta che vengono velocemente prodotte exp reg in cui i
   * pattern ripetuti sono collassati a due a due, oppure a tre a tre e cos via.
   * Poi un inevitabile mismatch risolve
   * tutto inserendo un plus di plus. Questo pu introdurre "mismatch ricorsivi" che allo
   * stato attuale addPlus_w non sa come gestire. Varie possibilit:
   * - eliminare i mismatch di bordo alla radice: cio direttamente in Parser
   * - rifiutare gli addPlus che introducono i doppi plus
   * - [(a regime) ordinare gli square di modo che i corti vengano provati prima
   */
  
} // Operator
