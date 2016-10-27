package org.chrisle.netbeans.plugins.csharp4netbeans.syntaxhighlighter;

import org.netbeans.api.lexer.InputAttributes;
import org.netbeans.api.lexer.Language;
import org.netbeans.api.lexer.LanguagePath;
import org.netbeans.api.lexer.Token;
import org.netbeans.spi.lexer.LanguageEmbedding;
import org.netbeans.spi.lexer.LanguageProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author chrl
 */
@ServiceProvider(service=LanguageProvider.class)
public class CSharpLanguageProvider extends LanguageProvider {

    @Override
    public Language<?> findLanguage(String mimeType) {
        if ("text/x-cs".equals(mimeType)) {
            return new CSharpLanguageHierarchy().language();
        }

        return null;
    }

    @Override
    public LanguageEmbedding<?> findLanguageEmbedding(Token<?> token, LanguagePath lp, InputAttributes ia) {
        return null;
    }
}