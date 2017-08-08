package net.chrizzly.csharp4netbeans.filetypes.cs.parser.folding;

import org.netbeans.api.editor.mimelookup.MimeRegistration;
import org.netbeans.spi.editor.fold.FoldManager;
import org.netbeans.spi.editor.fold.FoldManagerFactory;

/**
 *
 * @author chrl
 */
@MimeRegistration(mimeType="text/x-cs",service=FoldManagerFactory.class)
public class CsFoldManagerFactory implements FoldManagerFactory {

    @Override
    public FoldManager createFoldManager() {
        return new CsFoldManager();
    }
    
}