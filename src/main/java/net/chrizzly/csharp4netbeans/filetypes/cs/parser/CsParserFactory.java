package net.chrizzly.csharp4netbeans.filetypes.cs.parser;

import java.util.Collection;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.ParserFactory;

/**
 *
 * @author Chrizzly
 */
public class CsParserFactory extends ParserFactory {

    @Override
    public Parser createParser(Collection<Snapshot> clctn) {
        return new CsParser();
    }
}