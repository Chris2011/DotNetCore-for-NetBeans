package net.chrizzly.csharp4netbeans.filetypes.cs.parser.bracematching;

import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.bracesmatching.BracesMatcher;
import org.netbeans.spi.editor.bracesmatching.BracesMatcherFactory;
import org.netbeans.spi.editor.bracesmatching.MatcherContext;
import org.netbeans.spi.editor.bracesmatching.support.BracesMatcherSupport;

/**
 *
 * @author chrl
 */
@MimeRegistration(mimeType="text/x-cs",service=BracesMatcherFactory.class)
public class CsBracesMatcherFactory implements BracesMatcherFactory {
    @Override
    public BracesMatcher createMatcher(MatcherContext context) {
        return BracesMatcherSupport.defaultMatcher(context, -1, -1);
    }
}