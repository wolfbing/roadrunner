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

     Questo  programma  software libero; lecito redistribuirlo  o
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
 * MarshallConstants.java
 *
 * Created on
 * @author  Valter Crescenzi
 */
package com.wolf_datamining.autoextracting.roadrunner.marshall;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTConstants;

public interface MarshallConstants extends ASTConstants {
    
    // constants to produce XML output
    final public String DATASET  = "dataset";
    final public String SOURCE   = "source";
    final public String NAME     = "name";
    final public String INDEX    = "index";
    final public String WRAPPER  = "wrapper";
    final public String WRAPPEDBY = "wrappedby";
    final public String INSTANCE  = "instance";
    final public String SCHEMADL  = "schema_dl";
    final public String SAMPLEDL  = "sample_dl";
    final public String SAMPLESDL  = "samples_dl";
    final public String INSTANCEDL = "instance_dl";
    final public String INSTANCESDL = "instances_dl";
    final public String COMPRESSRATIO = "compress_ratio";
    final public String NUMBEROFSAMPLES = "number_of_samples";
    
}
