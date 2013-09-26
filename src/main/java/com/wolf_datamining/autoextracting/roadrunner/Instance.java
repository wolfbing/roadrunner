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
 * Instance.java
 *
 * Created on 20 gennaio 2003, 12.50
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTAnd;
import com.wolf_datamining.autoextracting.roadrunner.ast.ASTVariant;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;


public class Instance  {
  
  private ASTAnd root; // root of instance tree
  //N.B. *token of instance* to node of expression mapping; inclusive of null tokens and exclusive of invariant token
  private IdentityHashMap token2node; 
  private Wrapper wrappedby;
  private Sample sample;
  
  /** Creates a new instance of class Instance */
  public Instance(Wrapper wrapper, Sample sample, ASTAnd root, IdentityHashMap token2node) {
    this.wrappedby = wrapper;
    this.sample = sample;
    this.root = root;
    this.token2node = token2node;
  }
  
  public ASTAnd getRoot() {
    return this.root;
  }
  
  public Wrapper getWrapper() {
    return this.wrappedby;
  }
  
  public Sample getSample() {
    return this.sample;
  }

  public IdentityHashMap getTokens2Nodes() {
      return this.token2node;
  }
  
  public String getLabel(Token token) {
    ASTVariant node = (ASTVariant)this.token2node.get(token);
    return node.getLabel();
  }
  
  public String dump(String prefix) {
    return this.getRoot().dump(new Indenter(prefix));
  }
  
  public String toString() {
    return dump("");
  }
  
}
