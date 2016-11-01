package org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.parser.reformatting;

import javax.swing.text.BadLocationException;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.ReformatTask;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author chrl
 */
public class CsReformatTask implements ReformatTask {

    private Context context;

    public CsReformatTask(Context context) {
        this.context = context;
    }

    @Override
    public void reformat() throws BadLocationException {
        StatusDisplayer.getDefault().setStatusText("We will format this now...");
    }

    @Override
    public ExtraLock reformatLock() {
        return null;
    }
    
}