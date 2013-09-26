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
/**
 * Config.java
 *
 *
 * Created: Mon Dec  4 12:51:10 2000
 *
 * @author Valter Crescenzi
 * @version
 */
package com.wolf_datamining.autoextracting.roadrunner.config;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.wolf_datamining.autoextracting.roadrunner.util.Util;


public class Config {
    
    static private Logger log = Logger.getLogger(Config.class.getName());
    
    final static public String TOKENIZATION  = "tokenization";
    final static public String GENERATION    = "generation";
    final static public String LABELLING     = "labelling";
    final static public String OUTPUT        = "output";
    
    final static public String RRHOME     = "rr.home";
    final static public String RRLOG      = "etc/logging.properties";
    final static public String RRDEFAULT  = "etc/defaults.xml";
    final static public String RRCONFIG   = "RoadRunner Default Configuration";
    final static public String LEXICALN   = "Wrapper Configuration for Lexical Analysis";
    
    private Preferences defaults;     // contains all prefs of configuration file (defaults)
    private Preferences tokenization; // contains prefs in "tokenization" section (defaults)
    private Preferences generation;   // contains prefs in "generation" section (defaults)
    private Preferences labelling;    // contains prefs in "labelling" section  (defaults)
    private Preferences output;       // contains prefs in "output" section  (defaults)
    
    private Preferences prefs;        // contains all prefs set by user
    private Preferences lexicalPrefs; // contain only lexical prefs set by user
    
    // Singleton Pattern (in its simplest form)
    private static Config config=null;
    public static Config getInstance() {
        // create a first instance and never again
        if (config==null) {
            try {
                config = new Config();
            }
            catch (IOException ioe) {
                log.severe("Cannot load default properties file: "+ioe);
                System.exit(-1);
            }
        }
        return config;
    }
    
    private Config() throws IOException {
        /* use CLASSPATH to look for default options */
        File defaultsFile = Util.searchInRRHOME(RRDEFAULT);
        this.defaults = new Preferences();
        this.defaults.setName(RRCONFIG);
        
        this.tokenization = new Preferences();
        this.tokenization.setName(TOKENIZATION);
        this.tokenization.load(defaultsFile, TOKENIZATION);
        log.config("Tokenization properties from default configuration\n"+ tokenization);
        
        this.generation = new Preferences();
        this.generation.setName(GENERATION);
        this.generation.load(defaultsFile, GENERATION);
        log.config("Generation properties from default configuration\n"+ generation);
        
        this.labelling = new Preferences();
        this.labelling.setName(LABELLING);
        this.labelling.load(defaultsFile, LABELLING);
        log.config("Labelling properties from default configuration\n"+ labelling);
        
        this.output = new Preferences();
        this.output.setName(OUTPUT);
        this.output.load(defaultsFile, OUTPUT);
        log.config("Output properties from default configuration\n"+ output);
        
        this.defaults.putAll(tokenization);
        this.defaults.putAll(generation);
        this.defaults.putAll(labelling);
        this.defaults.putAll(output);
        this.prefs = new Preferences();
        this.prefs.putAll(this.defaults);
        
        this.lexicalPrefs = new Preferences();
        this.lexicalPrefs.setName(LEXICALN);
        this.lexicalPrefs.putAll(this.prefs);
        this.lexicalPrefs.retainAll(this.tokenization);
        log.config("Lexical Preferences:\n"+lexicalPrefs);
    }
    
    public static File getFileInOutputDir(String filename) throws FileNotFoundException {
        File outputdir = Util.searchInRRHOME(getPrefs().getString(Constants.OUTPUTDIR));
        return new File(outputdir,filename);
    }
    
    public static Preferences getPrefs() {
        return getInstance().prefs;
    }
    
    public Preferences getLexicalPrefs() {
        Preferences result = new Preferences();
        result.setName(LEXICALN);
        result.putAll(getPrefs());
        result.retainAll(this.tokenization);
        return result;
    }
    
    public static void load(File cfg) throws IOException {
    	System.out.println(cfg.getAbsolutePath());
        log.config("Loading configuration from file: "+cfg);
        Config.getPrefs().load(cfg);
        log.config("New configuration after loading from file "+cfg+":\n"+Config.getPrefs().toString());
    }
    
    public static void load(Preferences prefs) {
        log.config("Loading configuration from preferences: "+prefs.getName());
        Config.getPrefs().putAll(prefs);
        log.config("New configuration after loading from preferences "+prefs.getName()+":\n"+Config.getPrefs().toString());
    }
    
    public String toString() {
        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);
        out.println("<!-- "+new Date(System.currentTimeMillis())+" -->");
        String[] keys = Constants.keys.asArray();
        for (int i=0; i<keys.length; i++) {
            if (Config.getPrefs().contains(keys[i])) continue;
            out.println(keys[i]+"="+Config.getPrefs().getString(keys[i]));
        }
        return result.toString();
    }
}
