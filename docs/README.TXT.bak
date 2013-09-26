INSTALLATION
- Unzip the distribution and consider the main directory "RoadRunner"
- Add to your CLASSPATH these libraries:
      lib/roadrunner.jar
      lib/nekohtml.jar
      lib/xercesImpl.jar
      lib/xmlParserAPIs.jar

In order to run RoadRunner you can either cd to the main RoadRunner directory
or set the system property "rr.home" to point to the main directory.

(Note: the system property "rr.home" can be set by adding the option
  -Drr.home=path/to/directory/RoadRunner
to the JVM invocation)

USAGE
The main class is roadrunner.Shell. For help on its usage
type "java roadrunner.Shell" without args:

Usage:  java roadrunner.Shell [-trace[level]] [-O<config.xml>]
                                [-N<name>]
                         ( -Finputfiles.txt | file0 [file1 ...] )
-O<config.xml>        a xml configuration file
-trace[level]         enable tracing at log level <level>
-F<files.txt>         files.txt lists the input filenames
-N<name>              set a <name> for output (default "out"):
                      wrapper file(s): "<name>Wrapper<N>.xml"
                         data file(s): "<name>_DataSet<N>.xml"

RR disposes inferred wrappers and extracted dataset under
subdirectories of the "output" directory. Each subdirectory
is named after the argument following the option -N.
The option -O specifies an XML configuration file whose
sintax is briefly described in docs/PROPERTIES.TXT

EXAMPLES
Three examples (bash sintax):
1) Soccer players

java roadrunner.Shell -Nplayers \
  -Oexamples/fifaworldcup.yahoo.com/players.xml \
  examples/fifaworldcup.yahoo.com/Cannavaro_xy_.xhtml \
  examples/fifaworldcup.yahoo.com/Nesta_xy_.xhtml \
  examples/fifaworldcup.yahoo.com/Zidane_xy_.xhtml


2) jobs

java roadrunner.Shell -Nhotjobs \
-Oexamples/www.hotjobs.com/hotjobs.xml \
  examples/www.hotjobs.com/ByLocation01_xy_.xhtml \
  examples/www.hotjobs.com/ByLocation02_xy_.xhtml \
  examples/www.hotjobs.com/ByLocation03_xy_.xhtml

3) Overstock Jewelry

java roadrunner.Shell -Noverstock \
  -Oexamples/www.overstock.com/jewelry.xml \
   examples/www.overstock.com/jewelry01_xy_.xhtml \
   examples/www.overstock.com/jewelry02_xy_.xhtml \
   examples/www.overstock.com/jewelry03_xy_.xhtml


After executing one of these examples, you find a directory named "output",
whose subdirectories are named after the -N option of the command line:
"players" for the first example and "overstock" for the last.
Therefore, the last example about jewelry produces the following files:

output/overstock/overstock00.xml               (the wrapper inferred)
output/overstock/overstock0_DataSet.xml        (the dataset extracted from input samples)
output/overstock/overstockWrappersIndex.xml    (and index of all wrappers produced)
output/overstock/results.html                  (html file to display all results)


Open the files "results.html" with a css, xslt-capable browser (Mozilla 1.3- and
recent versions of IE seem to work) to watch directly the data extracted by the
automatically generated wrapper.


USING ROADRUNNER ON NEW SAMPLES
RoadRunner works on well-formed documents. The input HTML pages are
pre-processed using the nekoHTML parser (http://www.apache.org/~andyc/neko/doc/html)
to produce DOM representations of input documents.

Note that in order to use the RR visual labelling feature, the input html document
must be adorned with html comments reporting the coordinates of the bounding box
of every text string in the visual rendering of the page. Just to give an
example of what RR Labeller expects, the following fragment of HTML code:

...<TR><TD><!--BB:10,10,60,20--> a text X</TD><TD><!--BB:70,10,120,30--> a text Y</TD></TR>...

states that the coordinates of the bounding box of the string "a text X" in the
visual rendering of the document are (minX=10, minY=10,  maxX=60, maxY=20);
similarly for "a text Y". Currently we use the suffix "_xy_.xhtml" to mark
HTML files which have been processed in this way.


REFERENCES

The general ideas underlying the RoadRunner Project can be found in the papers:

Valter Crescenzi, Giansalvatore Mecca, Paolo Merialdo: RoadRunner: Towards
Automatic Data Extraction from Large Web Sites. Proc. of 27th International
Conference on Very Large Databases (VLDB 2001): 109-118
http://www.vldb.org/conf/2001/P109.pdf

Valter Crescenzi, Giansalvatore Mecca, Paolo Merialdo Automatic Annotation of
Data Extracted from Large Web Sites. SIGMOD Conference 2003, WebDB Workshop.
http://doi.acm.org/10.1145/643477.643480

Valter Crescenzi, Giansalvatore Mecca, On Automatic Information Extraction from
Large Web Sites. Dip. Informatica ed Automazione Technical Report dia-76-2003.
http://www.dia.uniroma3.it/db/roadRunner/publications/dia-76-2003.ps.gz


THIRD-PARTY SOFTWARE INCLUDED
* Xerces Java Parser    (check file license: license/Apache_Software_LICENSE.TXT)
* NekoHTML HTML scanner (check file license: license/NekoHTML_LICENSE.TXT       )

This product includes software developed by:
- Andy Clark (http://www.apache.org/~andyc/neko/doc/html)
- The Apache Software Foundation (http://www.apache.org/).

Namely, these softwares are used to produce DOM representations of input html pages.

