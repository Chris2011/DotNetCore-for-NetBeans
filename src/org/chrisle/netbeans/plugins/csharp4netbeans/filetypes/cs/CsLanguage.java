package org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs;

import org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.lexer.CsTokenId;
import org.netbeans.api.lexer.Language;
import org.netbeans.modules.csl.spi.DefaultLanguageConfig;
import org.netbeans.modules.csl.spi.LanguageRegistration;

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
}