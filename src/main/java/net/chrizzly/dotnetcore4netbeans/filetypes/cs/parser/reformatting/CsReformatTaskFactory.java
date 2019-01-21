package net.chrizzly.dotnetcore4netbeans.filetypes.cs.parser.reformatting;

import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ReformatTask;

/**
 *
 * @author chrl
 */
@MimeRegistration(mimeType="text/x-cs",service=ReformatTask.Factory.class)
public class CsReformatTaskFactory implements ReformatTask.Factory {

    @Override
    public ReformatTask createTask(Context context) {
        return new CsReformatTask(context);
    }   
}