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
/*
 * Shell.java
 *
 * Created on "a long time ago"
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.*;


import org.xml.sax.SAXException;

import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.engine.Engine;
import com.wolf_datamining.autoextracting.roadrunner.engine.MDLEvaluator;
import com.wolf_datamining.autoextracting.roadrunner.labeller.Labeller;
import com.wolf_datamining.autoextracting.roadrunner.marshall.BrowserMarshall;
import com.wolf_datamining.autoextracting.roadrunner.util.Util;

public class Shell {
    
    static private Logger log = Logger.getLogger(Shell.class.getName());
    
    static final public String version = "0.02.11";
    
    static final public String helpMessage     =
    
    "\nUsage:  java roadrunner.Shell [-trace[level]] [-O<config.xml>]  \n"+
    "                                [-N<name>]  \n"+
    "                         ( -Finputfiles.txt | file0 [file1 ...] ) \n"+
    "-O<config.xml>        a xml configuration file                    \n"+
    "-trace[level]         enable tracing at log level <level>         \n"+
    "-F<files.txt>         files.txt lists the input filenames         \n"+
    "-N<name>              set a <name> for output (default \"out\"):  \n"+
    "                      wrapper file(s): \"<name>Wrapper<N>.xml\"   \n"+
    "                         data file(s): \"<name>_DataSet<N>.xml\"  \n";
    
    static final public String WRAPPER_NAME_DEFAULT  = "outWrapper";  //default name of wrappers
    
    /****  Command Line Options      */
    static private String wrapperName = "out"; //name of the wrapper (to derive names of produced files)
    static private void setWrapperName(String s)     { Shell.wrapperName = s;   }
    static public String getWrapperName()            { return Shell.wrapperName;}
    /*  Command Line Options         */
    
    /* user specified files */
    static private File wrapperFile;        // input wrapper file
    static private void setWrapperFile(final File wrapperFile) {
        Shell.wrapperFile = wrapperFile;
    }
    static public File  getWrapperFile() {
        return Shell.wrapperFile;
    }
    static private List urls = new LinkedList();
    static private URL[] samplesURL;        // input sample URLs
    static private void setSamplesURL() {
        Shell.samplesURL = (URL[]) urls.toArray(new URL[urls.size()]);
    }
    static public URL[] getSamplesURL() {
        return Shell.samplesURL;
    }
    static private Wrapper getWrapper() throws IOException, SAXException {
        if (getWrapperFile()==null) return null;
        log.info("Loading wrapper from file: "+getWrapperFile());
        Wrapper loaded = Wrapper.load(getWrapperFile());
        Config.load(loaded.getPrefs()); // set system config from wrapper prefs
        log.fine("Preferences of wrapper: ");
        log.fine(loaded.getPrefs().toString());
        log.fine("Expression of wrapper: ");
        log.fine(loaded.getExpression().dump(""));
        return loaded;
    }
    
    private static String  encoding = "utf8";
    
    public Shell setFileEncoding(String code)
    {
    	this.encoding = code;
    	return this;
    }
    
    static private Sample[] getSamples() throws IOException, SAXException {
        URL[] samplesURL = getSamplesURL();
        Sample[] samples = new Sample[samplesURL.length];
        log.info("There are "+samplesURL.length+" input file(s)");
        for(int h=0; h<samplesURL.length; h++) {
            log.info(h+".  "+samplesURL[h].toString());
            try {
                samples[h] = new Sample(samplesURL[h], Config.getPrefs(),encoding);
            }
            catch (IOException e) {
                log.severe("Cannot reach input sample: "+samplesURL[h]);
                throw e;
            }
            catch (SAXException e) {
                log.severe("Input sample not well-formed: "+samplesURL[h]);
                throw e;
            }
            log.finer("Tokens of sample: "+samples[h].getTokenlist());
        }
        return samples;
    }
    
    static private void readListOfSampleURLs(final String samplelist) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(samplelist));
        String url = in.readLine();
        while (url != null) {
            Shell.urls.add(getAsURL(url));
            url = in.readLine();
        }
    }
    
    static private void addSampleURL(String url) throws MalformedURLException {
        Shell.urls.add(getAsURL(url));
    }
    
    static private URL getAsURL(final String u) throws MalformedURLException {
        URL url;
        try { url = new URL(u); }
        catch (MalformedURLException mue) {
            url = new File(u).getAbsoluteFile().toURL();
        }
        return url;
    }
    
    static private void enableLogging(Level level) throws IOException {
        /* use RoadRunner Home Directory to look for logging properties  */
        File logProperties = Util.searchInRRHOME(Config.RRLOG);
        LogManager.getLogManager().readConfiguration(new FileInputStream(logProperties));
        Logger rrlogger = Logger.getLogger("roadrunner");
        log.info("Check "+Config.RRLOG+" to know the name of the log file used");
        FileHandler  rrhandler = new FileHandler();
        rrhandler.setLevel(level);
        rrlogger.addHandler(rrhandler);
        rrlogger.setLevel(level);
    }
    
    static boolean parseCommandLine(String argv[]) throws Exception {
        /* return false if parsing failed */
        boolean ok = true;
        int i=0;
        while (i<argv.length && argv[i].startsWith("-")) {
            //options
            if (argv[i].startsWith("-trace")) {
                Shell.enableLogging(Level.parse(argv[i].substring(6)));
                
                i++;
                continue;
            }
            
            if (argv[i].startsWith("-O")) {
                Config.load(new File(getOptionValue(argv[i])));
                i++;
                continue;
            }
            
            /* create array of file names */
            if (argv[i].startsWith("-F")) {
                Shell.readListOfSampleURLs(getOptionValue(argv[i]));
                i++;
                continue;
            }
            
            /* read a name for generated wrappers */
            if (argv[i].startsWith("-N")) {
                String ov = getOptionValue(argv[i]);
                Shell.setWrapperName( (ov.length()==0 ? WRAPPER_NAME_DEFAULT: ov) );
                i++;
                continue;
            }
            
            /* load an existing wrapper to generalize */
            if (argv[i].startsWith("-I")) {
                Shell.setWrapperFile(new File(getOptionValue(argv[i])));
                i++;
                continue;
            }
            
            if (argv[i].startsWith("-")) {
                System.err.println("Error --- Unrecognized option: "+argv[i]);
                i++;
                ok = false;
                continue;
            }
        }
        while (i<argv.length) {
            //Options -F might have already set sources
            Shell.addSampleURL(argv[i++]);
        }
        Shell.setSamplesURL();
        return ok;
    }
    
    static private String getOptionValue(String option) {
        if (option.length()<=2) return "";
        return (option.charAt(2)!=':' ? option.substring(2) : option.substring(3)) ;
    }
    
    static private void printResultOnConsole(WrappersRepository repository) throws IOException {
        DecimalFormat scFormatter = new DecimalFormat("   ######0");
        DecimalFormat saFormatter = new DecimalFormat("       ######0");
        DecimalFormat crFormatter = new DecimalFormat("          .##%");
        Set wrappers = repository.getWrappers();
        Iterator it = wrappers.iterator();
        System.out.println("=================== "+getWrapperName()+" =====================");
        System.out.println(getWrapperName()+"          schema DL | samples DL | CompressRatio");
        while (it.hasNext()) {
            Wrapper wrapper = (Wrapper)it.next();
            MDLEvaluator mdl = repository.getMDLEvaluator(wrapper);
            System.out.print(wrapper.getName()+wrapper.getId()+")   ");
            System.out.print(scFormatter.format(mdl.getSchemaDL()));
            System.out.print(saFormatter.format(mdl.getDL()));
            System.out.print(crFormatter.format(mdl.getCompressRatio()));
            System.out.println();
        }
    }
    
    public static void disguiseShell(String[] argv) throws Exception
    {
    	if (argv.length==0 || !parseCommandLine(argv)) {
            System.out.println(helpMessage);
            System.out.println("version: "+version);
            System.exit(-1);
            return;
        }
        
        /* Load input wrapper (if any) */
        Wrapper wrapper = null;
        try {
            wrapper = getWrapper();
        }
        catch(SAXException wexcp) {
            log.severe("Cannot decode input wrapper: "+wexcp.getMessage());
            return;
        }
        
        /* Load input samples */
        Sample[] samples = null;
        try {
            samples = getSamples();
        }
        catch (SAXException sexcp) {
            log.severe("Cannot load all input samples: "+sexcp.getMessage());
            return;
        }
        
        /* Lexical properties might have been set by input wrapper */
        log.config("== roadRunner version: "+version+" ===");
        log.config("\n"+Config.getPrefs().toString());
        log.config("======================================");
        
        WrappersRepository repository = Engine.infer(wrapper,samples);
        if (!repository.isEmpty()) {
            log.info("Inference successful.");
            repository.setWrappersNamesIds(getWrapperName()); // set wrappers unique ids
            
            // label attributes
            Labeller labeller = new Labeller(repository);
            
            // save wrappers; wrap data; and make result browser-friendly
            BrowserMarshall marshall = new BrowserMarshall(repository, Shell.getWrapperName());
            marshall.marshall();
            
            // print sketch of results on console
            printResultOnConsole(repository);
            return;
        }
        else log.info("Sorry: none wrapper inferred.");
    }
    
    
    static public void main(String[] argv) throws Exception {
        if (argv.length==0 || !parseCommandLine(argv)) {
            System.out.println(helpMessage);
            System.out.println("version: "+version);
            System.exit(-1);
            return;
        }
        
        /* Load input wrapper (if any) */
        Wrapper wrapper = null;
        try {
            wrapper = getWrapper();
        }
        catch(SAXException wexcp) {
            log.severe("Cannot decode input wrapper: "+wexcp.getMessage());
            return;
        }
        
        /* Load input samples */
        Sample[] samples = null;
        try {
            samples = getSamples();
        }
        catch (SAXException sexcp) {
            log.severe("Cannot load all input samples: "+sexcp.getMessage());
            return;
        }
        
        /* Lexical properties might have been set by input wrapper */
        log.config("== roadRunner version: "+version+" ===");
        log.config("\n"+Config.getPrefs().toString());
        log.config("======================================");
        
        WrappersRepository repository = Engine.infer(wrapper,samples);
        if (!repository.isEmpty()) {
            log.info("Inference successful.");
            repository.setWrappersNamesIds(getWrapperName()); // set wrappers unique ids
            
            // label attributes
            Labeller labeller = new Labeller(repository);
            
            // save wrappers; wrap data; and make result browser-friendly
            BrowserMarshall marshall = new BrowserMarshall(repository, Shell.getWrapperName());
            marshall.marshall();
            
            // print sketch of results on console
            printResultOnConsole(repository);
            return;
        }
        else log.info("Sorry: none wrapper inferred.");
        System.out.println("-----------End--------------");
    }
    
    
}