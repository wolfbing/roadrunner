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
 * Wrapper.java
 *
 *
 * Created: 
 *
 * @author Valter Crescenzi
 * @version
 */

package com.wolf_datamining.autoextracting.roadrunner;

import java.io.*;
import java.util.logging.*;
import java.net.URL;

import org.xml.sax.SAXException;
import org.w3c.dom.Document;

import com.wolf_datamining.autoextracting.roadrunner.ast.ASTDecoder;
import com.wolf_datamining.autoextracting.roadrunner.ast.ASTEncoder;
import com.wolf_datamining.autoextracting.roadrunner.ast.Expression;
import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.config.Preferences;
import com.wolf_datamining.autoextracting.roadrunner.marshall.InstanceSerializer;
import com.wolf_datamining.autoextracting.roadrunner.parser.BindingException;
import com.wolf_datamining.autoextracting.roadrunner.parser.Extractor;
import com.wolf_datamining.autoextracting.roadrunner.util.DOMLoader;
import com.wolf_datamining.autoextracting.roadrunner.util.Indenter;


public class Wrapper {
    
    static private Logger log = Logger.getLogger(Wrapper.class.getName());
    
    final static private String WRAPPER     = "wrapper";
    final static private String NAME        = "name";
    
    static private void write(Wrapper wrapper, Writer writer) throws IOException {
//    	PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getResultsFile()),"utf-8"));
        PrintWriter out = new PrintWriter(writer);
        Indenter ind = new Indenter(false);
        out.println("<?xml version='1.0' encoding=\"UTF-8\"?>");
        out.println("<"+WRAPPER+" "+NAME+"=\""+wrapper.getName()+"\">");
        wrapper.getPrefs().encode(ind,out); // encode lexical configuration
        new ASTEncoder(ind,out).encode(wrapper.getExpression());
        ind.dec();
        out.println("</"+WRAPPER+">");
        out.close();
    }
    
    static private Wrapper read(Reader in) throws IOException, SAXException {
        Document wrapperDoc = DOMLoader.parseXML(in);
        // decode lexical preferences of wrapper. 
        Preferences prefs = new Preferences();
        prefs.decode(wrapperDoc);
        // decode expression of wrapper
        Expression exp = new ASTDecoder(wrapperDoc).decode();
        Wrapper wrapper = new Wrapper(exp,prefs);
        wrapper.setName(wrapperDoc.getDocumentElement().getAttribute(NAME));
        return wrapper;
    }
    
    public static Wrapper load(File file) throws IOException, SAXException {
        return read(new FileReader(file));
    }
    
    private Expression expression;
    private Preferences prefs;
    private String name;
    private int id;
    
    public Wrapper(Expression expression, Preferences options) {
        this.expression = expression;
        this.prefs = options;
        this.name = null;
        this.id = 0;
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    public Preferences getPrefs() {
        return this.prefs;
    }
    
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public int getId() {
        return this.id;
    }
    void setId(int id) {
        this.id = id;
    }
    
    public Instance wrap(Sample sample) throws BindingException {
        return new Extractor(this).extract(sample);
    }
        
    public void save() throws IOException {
        saveAs(new File(this.getName()+".xml"));
    }
    public void saveAs(File file) throws IOException {
    	OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
        write(this, writer);
    }
    
    public String toString() {
        return this.getExpression().dump(getName()+">");
    }
    
    public boolean equals(Object o) {
        Wrapper w = (Wrapper)o;
        return w.getExpression().equals(getExpression()) && w.getPrefs().equals(getPrefs());
    }
    
    public int hashCode() {
        return getExpression().hashCode()+getPrefs().hashCode();
    }
    
    public static void extract(String argv[]) throws IOException
    {
    	int n = 0;
        boolean test = false;
        if (argv.length < 2) {
            System.err.println("Usage:\tjava roadrunner.wrapper.Wrapper [-t] <wrapper.xml> "+
            "<url0> <url1> ... \n"+
            "\t     -t: test only, do not write extracted data");
            System.exit(-1);
        }
        if (argv[n].toLowerCase().startsWith("-t")) {
            test = true;
            n++;
        }
        Wrapper w = null;
        
        try {
            w = Wrapper.load(new File(argv[n++]));
        }
        catch (SAXException saxe) {
            System.err.println("Cannot decode wrapper from xml "+saxe);
            System.exit(-1);
        }
//        PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));
        String path =  new File(argv[n++]).getAbsolutePath()+"\\";
        String encoding = argv[n++];
        for(int i=n; i<argv.length; i++) {
            System.out.println("Wrapping: "+argv[i]); System.out.flush();
            try {
                Sample sample = new Sample(new URL(argv[i]), w.getPrefs(),encoding);
                Instance data = w.wrap(sample);
                if (!test) {
                	File f = new File(argv[i]);
                	String name = f.getName();
                	if(name.toLowerCase().endsWith(".xhtml"))
                		name = name.substring(0,name.length()-6);
                	else
                	{
                		System.out.println("file have not been converted to xhtml");
                		System.exit(-1);
                	}

	            	PrintWriter fileOut = new PrintWriter(path+"res_"+name+".xml","utf8");
	                fileOut.println("<?xml version=\'1.0\' encoding=\"UTF-8\"?>");
	                fileOut.println("<?xml-stylesheet href=\"..\\.style\\data.xsl\" type=\"text/xsl\"?>");
	                new InstanceSerializer(data,fileOut,new Indenter()).encode();
	                fileOut.close();
                }
            }
            catch (IllegalArgumentException iae) {
                System.err.println("Skipping "+argv[i]);
                System.err.println(iae.getMessage());
            }
            catch (IOException ioe) {
                System.err.println("Skipping "+argv[i]);
                System.err.println("Cannot reach source:");
                System.err.println(ioe.getMessage());
            }
            catch (SAXException saxe) {
                System.err.println("Skipping "+argv[i]);
                System.err.println("Cannot parse xml source:");
                System.err.println(saxe.getMessage());
            }
            catch (BindingException be) {
                System.err.println("Skipping "+argv[i]);
                System.err.println("Wrapping failed:");
                System.err.println(be.getMessage());
            }
            //out.close();
            
        }
    
    	
    }
    
    
    public static void main(String argv[])  throws Exception {
        
    	Wrapper.extract(argv);
    }
    
}
