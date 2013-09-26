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

     Questo  programma   software libero;  lecito redistribuirlo  o
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
 * AmbiguityMismatchFactory.java
 *
 * Created on December 30, 2003, 6:20 PM
 * @author  Valter Crescenzi
 */

package com.wolf_datamining.autoextracting.roadrunner.parser;

import java.util.*;

import com.wolf_datamining.autoextracting.roadrunner.ast.ExpressionRegion;
import com.wolf_datamining.autoextracting.roadrunner.ast.Node;
import com.wolf_datamining.autoextracting.roadrunner.bidi.*;
import com.wolf_datamining.autoextracting.roadrunner.config.Config;
import com.wolf_datamining.autoextracting.roadrunner.config.Constants;


public class AmbiguityMismatchFactory {
    
    
    private int max_ambiguity_mismatches;
    
    /** Creates a new instance of AmbiguityMismatchFactory */
    public AmbiguityMismatchFactory() {
        max_ambiguity_mismatches = Config.getPrefs().getInt(Constants.AMBIGUITY);
    }
       
    public List getAmbiguityMismatches(MismatchPoint m) {
        // SI POTREBBE EVITARE TUTTO QUESTO PER LA MAGGIOR PARTE DEGLI AMBIGUITY MISMATCH        
        Direction dir = m.getDir();
        // Observe that non-linear mismatches are generated together with one linear mismatch
        // For the purpose of dealing with ambiguity, it suffices to handle the linear mismatch
        if (max_ambiguity_mismatches==0 || !m.isLinear())  return Collections.EMPTY_LIST;
        /* Ambiguity mismatches occur before the  mismatch in correspondance of dom depth changes */
        List result = new LinkedList();
        int domDepth = m.getMinDOMdepth()-1; // wanted DOM depth for first ambiguity mismatch
        int order = m.getOrder();
        /* Look for tokens on the matching tokenlist at the given dom depth from the original mismatch backwards*/
        Parser parser = new Parser(m.getExpression(), dir);
        TokenListRegion matchingTLR = m.getMatchingTokenlist();
        List matchingTokens = matchingTLR.asList();
        ListIterator tokensIt = BidirectionalListFactory.newListIterator(matchingTokens, dir.uTurn());
        int tokensBack = 0, numberOfMismatches = 0;
        while (numberOfMismatches<max_ambiguity_mismatches && tokensIt.hasNext()) {
            Token token = (Token)tokensIt.next(); tokensBack++;
            if (token.depth()==domDepth) {
                //add ambiguity mismatch for that DOM depth on tokenlist
                int currentTokIdx = m.getTokenIndex() - dir.unit()*tokensBack;
                Token currentToken = m.getTokenlist().getToken(currentTokIdx);
                TokenListRegion tlr = (TokenListRegion)dir.subRegionBefore(matchingTLR, dir.posAfterIndex(currentTokIdx));
                parser.parseAllTokenlist(new TokenList(tlr.asList())); // parse to find last matching node
                List currentPaths = parser.getPathsReachedOnExpression();
                Iterator pathIt = currentPaths.iterator();
                Node.Path currentPath = null;
                while (pathIt.hasNext()) {
                    //Choose the one matching with current token
                    Node.Path path = (Node.Path)pathIt.next();
                    Node currentNode = m.getExpression().getNode(path);
                    if (currentNode instanceof Token && currentToken.matches((Token)currentNode)) {
                        currentPath = path;
                        break;
                    }
                }
                if (currentPath==null) throw new RuntimeException("Something wrong!");
                MismatchPoint mismatch = getMismatchPoint(m, currentPath,  currentTokIdx);
                mismatch.setOrder(++order);
                result.add(mismatch);
                numberOfMismatches++;
                domDepth--;
            }
        }
        return result;
    }
    
    private MismatchPoint getMismatchPoint(MismatchPoint m, Node.Path path, int tokenIndex) {
        // Potrebbe divenire un secondo costruttore ...
        Direction dir = m.getDir();
        ExpressionRegion baseRegion = new ExpressionRegion(m.getExpression(), path.subpath(-1));
        int expPos = dir.posBeforeIndex(path.lastIndex());
        ExpressionRegion matchingRegion  = (ExpressionRegion)dir.subRegionBefore(baseRegion, expPos);
        ExpressionRegion mismatchingRegion = (ExpressionRegion)dir.subRegionAfter(baseRegion, expPos);
        return new MismatchPoint(matchingRegion,mismatchingRegion, path, m.getTokenlist(), tokenIndex, dir);
    }
}
