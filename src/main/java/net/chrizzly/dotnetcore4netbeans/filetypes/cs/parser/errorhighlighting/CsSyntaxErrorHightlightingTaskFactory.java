package net.chrizzly.dotnetcore4netbeans.filetypes.cs.parser.errorhighlighting;

import java.util.Collection;
import java.util.Collections;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.spi.TaskFactory;

/**
 *
 * @author chrl
 */
public class CsSyntaxErrorHightlightingTaskFactory extends TaskFactory {
    @Override
    public Collection create (Snapshot snapshot) {
        return Collections.singleton (new CsSyntaxErrorHighlightingTask());
    }
}