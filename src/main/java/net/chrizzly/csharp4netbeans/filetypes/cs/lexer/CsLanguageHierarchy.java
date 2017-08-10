package net.chrizzly.csharp4netbeans.filetypes.cs.lexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.lexer.LanguageHierarchy;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Chrl
 */
public class CsLanguageHierarchy extends LanguageHierarchy<CsTokenId> {
    private static final List<CsTokenId> tokens = new ArrayList<CsTokenId>();
    private static final Map<Integer, CsTokenId> idToToken = new HashMap<Integer, CsTokenId>();

    static {
        TokenType[] tokenTypes = TokenType.values();

        for (TokenType tokenType : tokenTypes) {
            tokens.add(new CsTokenId(tokenType.name(), tokenType.category, tokenType.id));
        }

        tokens.forEach(token -> {
            idToToken.put(token.ordinal(), token);
        });
    }

    static synchronized CsTokenId getToken(int id) {
        return idToToken.get(id);
    }

    @Override
    protected synchronized Collection<CsTokenId> createTokenIds() {
        return tokens;
    }

    @Override
    protected synchronized Lexer<CsTokenId> createLexer(LexerRestartInfo<CsTokenId> info) {
        return new CsLexer(info);
    }

    @Override
    protected String mimeType() {
        return "text/x-cs";
    }
}