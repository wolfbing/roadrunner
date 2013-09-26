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
 * MDLEvaluator.java
 *
 * Created on 17 aprile 2003, 12.16
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.engine;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.*;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Direction;
import com.wolf_datamining.autoextracting.roadrunner.bidi.Region;
import com.wolf_datamining.autoextracting.roadrunner.parser.Parser;
import com.wolf_datamining.autoextracting.roadrunner.parser.ParserListener;
import com.wolf_datamining.autoextracting.roadrunner.parser.ParserListenerAdapter;
import com.wolf_datamining.autoextracting.roadrunner.parser.Token;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenList;
import com.wolf_datamining.autoextracting.roadrunner.parser.TokenListRegion;
import com.wolf_datamining.autoextracting.roadrunner.parser.token.TokenFactory;



public class MDLEvaluator extends ParserListenerAdapter implements ParserListener {
    
    /*  Listen events trigged by Parser to avoid duplication of  parse code */
    
    /**
     * Note sulla codifica adottata
     *       - si suppone che (il sottoinsieme utilizzato del-) l'alfabeto sia noto al ricevitore
     *       - il costo di codificare un Token  lg S se S  il numero di token utilizzati
     *       - gli altri nodi non foglia (Plus, Hook, Variant, Subtree) sono visti come token aggiunti
     *       - al costo di un Token di tipo testo va aggiunto il costo di codificare la stringa
     *       - la profondit� dei tag  un'informazione ridondante e non codificata
     *       - gli And sono ridondanti e non codificati esplicitamente
     *       - si suppone che i caratteri di testo siano codificati come codice ASCII ad 8 bit
     *       - il costo di codificare un numero N>0 in uno stream  pari a 2[lg(N)+1], 1 se N=0
     *
     * Sullo schema:
     *  si codificano i nodi seguiti (laddove necessario) dal numero di figli, e cos via ricorsivamente
     * Sull'istanza:
     *  si codifica ciascuna istanza specificando quante volte istanziare tutti i punti di scelta
     *  (0 od 1 per gli Hook, 1 o pi� per i Plus, una stringa per i Variant, tutto per i Subtree).
     *  Attualmente per i subtree si codica il numero di token del subtree e tutti i token, presi singolarmente
     */
    
    final private static int CHAR_COST  =  3; /* how many bits for a char (English text compress ratio is about 0.3 */
    final private static int TAGS_BITS =   8; /* how many auxiliary bits for a tag  */
    
    static private double lg(int n)    { return Math.log(n)/Math.log(2); }
    static private int ceil(double d)   { return (int)Math.ceil(d);        }
    
    private Parser parser;
    private Expression expression;
    private TokenList tokenlist;
    private TokenList[] instances;
    
    private double cratio;
    private int   dl; // of Schema + Instances
    private int   schemaDL;
    private int   totInstancesDL;
    private int   totSamplesDL;
    private int[] instancesDL;
    
    private int currentInstanceDL;
    private Stack squaresDLs;
    
    private boolean computed;
    
    /** Creates a new instance of MDLEvaluator */
    public MDLEvaluator(Expression expression, TokenList[] tokenlists) {
        this.parser = new Parser(expression);
        this.parser.setParserListener(this);
        this.expression = expression;
        this.instances = tokenlists;
        this.instancesDL = new int[tokenlists.length];
        this.squaresDLs = new Stack();
        this.computed = false;
    }
    
    private void checkAlreadyComputed() {
        if (computed) return;
        else computed = true;
        this.schemaDL = this.getExpressionDL(this.expression);
        this.totInstancesDL = this.getInstancesDL(this.instances);
        this.totSamplesDL = this.getTokenListsDL(this.instances);
        this.dl = this.schemaDL + this.totInstancesDL;
        this.cratio = ((double)this.getDL()) / this.totSamplesDL;
    }
    
    public int getDL() {
        checkAlreadyComputed();
        return this.dl;
    }
    
    public int getDLofInstance(int n) {
        checkAlreadyComputed();
        return this.instancesDL[n];
    }
    
    public double getCompressRatio() {
        checkAlreadyComputed();
        return this.cratio;
    }
    
    public int getSchemaDL() {
        return this.schemaDL;
    }
    
    public int getInstancesDL() {
        checkAlreadyComputed();
        return this.totInstancesDL;
    }
    
    public int getSamplesDL() {
        checkAlreadyComputed();
        return this.totSamplesDL;
    }
    
    public static int getTokenListsDL(TokenList[] tls) {
        int gdl = 0;
        for(int i=0; i<tls.length; i++)
            gdl += tls[i].getDL();
        return gdl;
    }
    
    public static int getTokenlistDL(TokenList tl) {
        return getTokenListRegionDL(tl.asRegion());
    }
    
    public static int getTokenListRegionDL(TokenListRegion tlr) {
        int dl=0;
        Iterator it = tlr.asList().iterator();
        while (it.hasNext()) {
            Token token = (Token)it.next();
            dl += codeLength4token(token);
        }
        return dl;
    }
    
    public static int getExpressionDL(Expression expression) {
        return new VisitorPlugger() {
            private int sdl=0;
            
            int getDL(Expression exp) {
                visit(exp.getRoot());
                return this.sdl;
            }
            
            public boolean visitNode(Node node)   {
                this.sdl += codeLength4token();
                return visitChildren(node);
            }
            public boolean visit(ASTAnd node) {
                this.sdl += codeLength4number(node.jjtGetNumChildren());
                return visitChildren(node);
            }
            public boolean visit(ASTToken token)  {
                this.sdl += codeLength4token(token);
                return false;
            }
        }.getDL(expression);
    }
    
    private int getInstancesDL(TokenList[] tokenlists) {
        this.totInstancesDL = 0;
        for (int n=0; n<tokenlists.length; n++) {
            this.currentInstanceDL = 0;
            this.tokenlist = tokenlists[n];
            this.parser.parse(this.tokenlist);
            this.instancesDL[n] = this.currentInstanceDL;
            this.totInstancesDL += this.currentInstanceDL;
        }
        return this.totInstancesDL;
    }
    
    public void startAnd(ASTAnd and) {
        this.squaresDLs.push(new Integer(currentInstanceDL));
        this.currentInstanceDL = 0;
    }
    
    public void endAnd(ASTAnd and, boolean matches) {
        int squareDL = this.currentInstanceDL;
        this.currentInstanceDL = ((Integer)squaresDLs.pop()).intValue();
        if (matches) this.currentInstanceDL += squareDL;
    }
    
    public void endHook(ASTHook hook, boolean matches, int n) {
        if (matches) this.currentInstanceDL += codeLength4number(n);
    }
    
    public void endPlus(ASTPlus plus, boolean matches, int n) {
        if (matches) this.currentInstanceDL += codeLength4number(n);
    }
    
    public void endVariant(ASTVariant variant, boolean matches, Token token)  {
        if (matches) this.currentInstanceDL += codeLength4token(token);
    }
    
    public void endSubtree(ASTSubtree node, boolean matches, int startIndex, int endIndex) {
        this.currentInstanceDL += getTokenListRegionDL(extractTokenListRegion(startIndex, endIndex));
        this.currentInstanceDL += codeLength4number(endIndex-startIndex+1);
    }
    
    private TokenListRegion extractTokenListRegion(int startIndex, int endIndex) {
        Direction d = Direction.LEFT2RIGHT;
        Region tlr = this.tokenlist.asRegion();
        return (TokenListRegion)d.subRegion(tlr, d.posBeforeIndex(startIndex), d.posAfterIndex(endIndex));
    }
    
    static private int codeLength4number(int n)      {
        return ( n==0 ? 1 : 2 * ( ceil(lg(n)) + 1 ) );
    }
    static private int codeLength4token()            {
        return TAGS_BITS+ceil(lg(TokenFactory.getInstance().getNumberOfKnownTokens()));
    }
    static private int codeLength4token(Token token) {
        int result = codeLength4token();
        if (token.maybeVariant() && token.getVariantValue()!=null)
            result += codeLength4number(token.getVariantValue().length())+codeLength4string(token.getVariantValue());
        return result;
    }
    static private int codeLength4string(String s)   { return CHAR_COST * s.length(); }
    
}
