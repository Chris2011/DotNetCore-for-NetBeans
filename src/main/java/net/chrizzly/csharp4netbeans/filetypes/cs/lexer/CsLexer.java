package net.chrizzly.csharp4netbeans.filetypes.cs.lexer;

import net.chrizzly.csharp4netbeans.filetypes.cs.CSharpLexer;
import org.antlr.v4.runtime.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author Chrl
 */
public class CsLexer implements Lexer<CsTokenId> {
    private LexerRestartInfo<CsTokenId> info;

    private CSharpLexer csharpLexer;

    public CsLexer(LexerRestartInfo<CsTokenId> info) {
        this.info = info;

        AntlrCharStream charStream = new AntlrCharStream(info.input(), "CSharpEditor");
        csharpLexer = new CSharpLexer(charStream);
    }

    public org.netbeans.api.lexer.Token<CsTokenId> nextToken() {
        Token token = csharpLexer.nextToken();

        if (token.getType() != CSharpLexer.EOF) {
            CsTokenId tokenId = CsLanguageHierarchy.getToken(token.getType());

            return info.tokenFactory().createToken(tokenId);
        }

        return null;
    }

    public Object state() {
        return null;
    }

    public void release() {}
}