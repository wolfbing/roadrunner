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

     Questo  programma   software libero; lecito redistribuirlo  o
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
 * WrappersRepository.java
 *
 *
 * Created on 17 dicembre 2002, 11.55
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.engine.MDLEvaluator;
import com.wolf_datamining.autoextracting.roadrunner.engine.Registry;
import com.wolf_datamining.autoextracting.roadrunner.parser.BindingException;


public class WrappersRepository {
    
    private Map wrapper2samples;
    private Map wrapper2instances;
    private Registry registry;
    
    /** Creates a new instance of WrappersRepository */
    //Passare a WrappersRepository(Set wrappers, Sample[] samples)
    // C'e' qualcosa che non va tra questa classe, Engine che la usa e Registry che viene usata
    public WrappersRepository(Registry registry, Sample[] samples) {
        this.wrapper2samples = new LinkedHashMap();
        this.wrapper2instances = new LinkedHashMap();
        this.registry = registry;
        // make Wrappers
        Set wrappers = new LinkedHashSet();
        Iterator it = registry.getExpressionsByCompressRatio().iterator();
        while (it.hasNext()) {
            Expression exp = (Expression)it.next();
            wrappers.add(new Wrapper(exp, Config.getInstance().getLexicalPrefs()));
        }
        putAll(wrappers,Arrays.asList(samples));
    }
    
    public boolean isEmpty() {
        return this.wrapper2samples.isEmpty();
    }
    
    public Set getWrappers() {
        return Collections.unmodifiableSet(this.wrapper2samples.keySet());
    }
    
    public Set getSamples(Wrapper wrapper) {
        return Collections.unmodifiableSet((Set)this.wrapper2samples.get(wrapper));
    }
    
    public Set getInstances(Wrapper wrapper) throws BindingException {
        if (this.wrapper2instances.containsKey(wrapper))
            return (Set)this.wrapper2instances.get(wrapper);
        Set result = new LinkedHashSet();
        Set samples = getSamples(wrapper);
        Iterator it = samples.iterator();
        while (it.hasNext()) {
            Sample sample = (Sample)it.next();
            Instance instance = wrapper.wrap(sample);
            result.add(instance);
        }
        this.wrapper2instances.put(wrapper, result);
        return result;
    }
    
    private void putAll(Set wrappers, Collection samples) {
        Iterator it = wrappers.iterator();
        while (it.hasNext()) this.putAll((Wrapper)it.next(), samples);
    }

    private void putAll(Wrapper wrapper, Collection samples) {
        this.wrapper2samples.put(wrapper, new LinkedHashSet(samples));
    }
    
    public MDLEvaluator getMDLEvaluator(Wrapper wrapper) {
        return this.registry.getMDLEvaluator(wrapper.getExpression());
    }
    
    public void setWrappersNamesIds(String name) {
        Iterator it = getWrappers().iterator();
        int n=0;
        while (it.hasNext()) {
            Wrapper wrapper = (Wrapper)it.next();
            wrapper.setName(name);
            wrapper.setId(n++);
        }
    }
}
