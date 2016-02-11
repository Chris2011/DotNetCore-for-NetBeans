package org.chrisle.netbeans.plugins.csharp4netbeans.syntaxhighlighter;

import org.chrisle.netbeans.plugins.csharp4netbeans.syntaxhighlighter.utils.ANTLRCharStream;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.Lexer;
import org.netbeans.spi.lexer.LexerRestartInfo;

/**
 *
 * @author chrl
 */
class CSharpEditorLexer implements Lexer<CSharpTokenId> {
    private LexerRestartInfo<CSharpTokenId> info;
    private CSharpLexer lexer;
    
    public CSharpEditorLexer(LexerRestartInfo<CSharpTokenId> lri) {
        this.info = info;
        ANTLRCharStream charStream = new ANTLRCharStream(info.input(), "CMinusEditor", true);
        lexer = new CSharpLexer(charStream);
    }

    @Override
    public Token<CSharpTokenId> nextToken() {
        org.antlr.runtime.Token token = lexer.nextToken();                

        Token<CSharpTokenId> createdToken = null;

        if (token.getType() != -1){
            CSharpTokenId tokenId  = CSharpLanguageHierarchy.getToken(token.getType());
            createdToken = info.tokenFactory().createToken(tokenId);
        }  else if(info.input().readLength() > 0){
            CSharpTokenId tokenId  = CSharpLanguageHierarchy.getToken(CSharpLexer.WS);
            createdToken = info.tokenFactory().createToken(tokenId);
        }

        return createdToken;
    }

    @Override
    public Object state() {
        return null;
    }

    @Override
    public void release() {
        
    }
    
}
