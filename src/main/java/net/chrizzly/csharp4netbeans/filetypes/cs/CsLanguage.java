package net.chrizzly.csharp4netbeans.filetypes.cs;

import net.chrizzly.csharp4netbeans.filetypes.cs.lexer.CsTokenId;
import net.chrizzly.csharp4netbeans.filetypes.cs.parser.CsParser;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;
import org.netbeans.modules.parsing.spi.Parser;

/**
 *
 * @author Chrizzly
 */
@LanguageRegistration(mimeType = "text/x-cs")
public class CsLanguage extends DefaultLanguageConfig {
    @Override
    public Language getLexerLanguage() {
        return CsTokenId.getLanguage();
    }

    @Override
    public String getDisplayName() {
        return "Cs";
    }

    @Override
    public Parser getParser() {
        return new CsParser();
    }
}