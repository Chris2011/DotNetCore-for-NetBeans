package org.chrisle.netbeans.plugins.csharp4netbeans.syntaxhighlighter;

import org.chrisle.netbeans.plugins.csharp4netbeans.syntaxhighlighter.utils.ANTLRTokenReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.api.lexer.Language;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author chrl
 */
class CSharpLanguageHierarchy extends LanguageHierarchy<CSharpTokenId> {
    private static List<CSharpTokenId> tokens;
    private static Map<Integer, CSharpTokenId> idToToken;
    
    private static final Language<CSharpTokenId> language = new CSharpLanguageHierarchy().language();

    public CSharpLanguageHierarchy() {
    }
    
    public static Language<CSharpTokenId> getLanguage() {
        return language;
    }
    
    /**
     * Initializes the list of tokens with IDs generated from the ANTLR
     * token file.
     */
    private static void init() {
        ANTLRTokenReader reader = new ANTLRTokenReader();
        tokens = reader.readTokenFile();
        idToToken = new HashMap<Integer, CSharpTokenId>();
        for (CSharpTokenId token : tokens) {
            idToToken.put(token.ordinal(), token);
        }
    }
    
    /**
     * Returns an actual CMinusTokenId from an id. This essentially allows
     * the syntax highlighter to decide the color of specific words.
     * @param id
     * @return
     */
    static synchronized CSharpTokenId getToken(int id) {
        if (idToToken == null) {
            init();
        }

        return idToToken.get(id);
    }
    
    /**
     * Initializes the tokens in use.
     *
     * @return
     */
    @Override
    protected synchronized Collection<CSharpTokenId> createTokenIds() {
        if (tokens == null) {
            init();
        }

        return tokens;
    }

    /**
     * Creates a lexer object for use in syntax highlighting.
     *
     * @param info
     * @return
     */
    @Override
    protected synchronized Lexer<CSharpTokenId> createLexer(LexerRestartInfo<CSharpTokenId> lri) {
        return new CSharpEditorLexer(lri);
    }

    /**
     * Returns the mime type of this programming language ("text/x-cm). This
     * allows NetBeans to load the appropriate editors and file loaders when
     * a file with the cm file extension is loaded.
     *
     * @return
     */
    @Override
    protected String mimeType() {
        return "text/x-cs";
    }
}