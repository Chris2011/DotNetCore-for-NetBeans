package net.chrizzly.csharp4netbeans.filetypes.cs.parser.indentaion;

import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.IndentTask;

/**
 *
 * @author chrl
 */
@MimeRegistration(mimeType="text/x-cs",service=IndentTask.Factory.class)
public class CsIndentTaskFactory implements IndentTask.Factory {

    @Override
    public IndentTask createTask(Context context) {
        return new CsIndentTask(context);
    }
}