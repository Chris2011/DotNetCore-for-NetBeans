package net.chrizzly.csharp4netbeans.filetypes.cs.parser.reformatting;

import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.chrizzly.csharp4netbeans.utils.MiscUtil;
import org.netbeans.modules.editor.indent.spi.Context;
import org.netbeans.modules.editor.indent.spi.ExtraLock;
import org.netbeans.modules.editor.indent.spi.ReformatTask;
import org.openide.awt.StatusDisplayer;

/**
 *
 * @author chrl
 */
public class CsReformatTask implements ReformatTask {

    private final Context context;

    public CsReformatTask(Context context) {
        this.context = context;
    }

    @Override
    public void reformat() throws BadLocationException {
        StatusDisplayer.getDefault().setStatusText("We will format this now...");
        Document document = context.document();
        String fileContent = document.getText(document.getStartPosition().getOffset(),
                document.getEndPosition().getOffset());
        String fileFormattedFinal = formatFile(fileContent, 4);
        int length = document.getLength();
        document.remove(0, length);
        document.insertString(0, fileFormattedFinal, null);
    }

    @Override
    public ExtraLock reformatLock() {
        return null;
    }

    private static String formatFile(String fileContent, int spacesToIncrement) {
        short currentSpacesToUse = 0;

        StringBuilder sbResult = new StringBuilder();

        List<String> listLines = MiscUtil.getLinesFromFileContent(fileContent);
        for (String line : listLines) {
            int openTagsAmount = MiscUtil.getOpenSymbolsAmount(line);

            String amountSeparatorChars;
            if (openTagsAmount > 0) {
                amountSeparatorChars = MiscUtil.getAmountSeparatorChars(currentSpacesToUse);
                currentSpacesToUse += spacesToIncrement * openTagsAmount;
            } else {
                currentSpacesToUse += spacesToIncrement * openTagsAmount;
                amountSeparatorChars = MiscUtil.getAmountSeparatorChars(currentSpacesToUse);
            }

            sbResult.append(amountSeparatorChars).append(line.trim()).append(MiscUtil.LINE_SEPARATOR);
        }

        return sbResult.toString().substring(0, sbResult.length() - 1); //substring to remove the last LINE_SEPARATOR
    }
}