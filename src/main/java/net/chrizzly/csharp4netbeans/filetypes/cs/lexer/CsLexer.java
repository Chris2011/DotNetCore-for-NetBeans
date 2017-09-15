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
    private final LexerRestartInfo<CsTokenId> info;
    private final CSharpLexer csharpLexer;
    private final AntlrCharStream stream;

    public CsLexer(LexerRestartInfo<CsTokenId> info) {
        this.info = info;
        this.stream = new AntlrCharStream(info.input(), "CsEditor");
        this.csharpLexer = new CSharpLexer(stream);
    }

    @Override
    public org.netbeans.api.lexer.Token<CsTokenId> nextToken() {
        Token token = csharpLexer.nextToken();

        if (token.getType() != CSharpLexer.EOF) {
            CsTokenId tokenId = CsLanguageHierarchy.getToken(token.getType());

            return info.tokenFactory().createToken(tokenId);
        }

        return null;
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {}
}