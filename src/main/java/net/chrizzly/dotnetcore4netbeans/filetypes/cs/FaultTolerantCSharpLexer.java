package net.chrizzly.dotnetcore4netbeans.filetypes.cs;

import net.chrizzly.dotnetcore4netbeans.filetypes.cs.CSharpLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

/**
 * Represents an enhanced lexer which can consume unterminated strings,
 * unterminated comments, and unknown characters.
 *
 * @author Christian Wulf
 *
 * @see
 * https://stackoverflow.com/questions/24724194/antlr-help-on-lexing-errors-for-a-custom-grammar-example
 *
 */
public class FaultTolerantCSharpLexer extends CSharpLexer {
    static class UnterminatedTokenException extends RecognitionException {
        private static final long serialVersionUID = 1L;

        public UnterminatedTokenException(Lexer lexer, CharStream input) {
            super(lexer, input);
        }
    }

    public FaultTolerantCSharpLexer(CharStream input) {
        super(input);
    }

    @Override
    public Token emit() {
        switch (getType()) {
            case UNTERMINATED_STRING: {
                setType(STRING);
                CommonToken token = (CommonToken) super.emit();
                RecognitionException ex = new UnterminatedTokenException(this, (CharStream) getInputStream());
                getErrorListenerDispatch().syntaxError(this, UNTERMINATED_STRING, getLine(), getCharPositionInLine(),
                        "Unterminated string: " + token.toString(this), ex);
                return token;
            }
            case UNTERMINATED_DELIMITED_COMMENT: {
                setType(DELIMITED_COMMENT);
                CommonToken token = (CommonToken) super.emit();
                RecognitionException ex = new UnterminatedTokenException(this, (CharStream) getInputStream());
                getErrorListenerDispatch().syntaxError(this, UNTERMINATED_DELIMITED_COMMENT, getLine(),
                        getCharPositionInLine(), "Unterminated comment: " + token.toString(this), ex);
                return token;
            }
            default:
                return super.emit();
        }
    }
}