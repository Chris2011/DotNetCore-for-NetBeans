package org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.lexer;

import org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.jcclexer.JavaCharStream;
import org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.jcclexer.JavaParserTokenManager;
import org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.jcclexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

public class CsLexer implements Lexer<CsTokenId> {
    private LexerRestartInfo<CsTokenId> info;
    private JavaParserTokenManager javaParserTokenManager;

    CsLexer(LexerRestartInfo<CsTokenId> info) {
        this.info = info;
        JavaCharStream stream = new JavaCharStream(info.input());
        javaParserTokenManager = new JavaParserTokenManager(stream);
    }

    @Override
    public org.netbeans.api.lexer.Token<CsTokenId> nextToken() {
        Token token = javaParserTokenManager.getNextToken();
        if (info.input().readLength() < 1) {
            return null;
        }
        return info.tokenFactory().createToken(CsLanguageHierarchy.getToken(token.kind));
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
    }
}