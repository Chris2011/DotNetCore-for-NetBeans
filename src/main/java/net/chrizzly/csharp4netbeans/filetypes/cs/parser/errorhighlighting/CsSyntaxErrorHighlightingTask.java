package net.chrizzly.csharp4netbeans.filetypes.cs.parser.errorhighlighting;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import net.chrizzly.csharp4netbeans.filetypes.cs.parser.CsError;
import net.chrizzly.csharp4netbeans.filetypes.cs.parser.CsParser;
//import net.chrizzly.csharp4netbeans.filetypes.cs.parser.CsParser.CsParserResult;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.text.NbDocument;

/**
 *
 * @author chrl
 */
public class CsSyntaxErrorHighlightingTask extends ParserResultTask {
    public void run(Result result, SchedulerEvent event) {
            CsParser.CsEditorParserResult csResult = (CsParser.CsEditorParserResult) result;
            CsError error = csResult.getError();

            if (error == null) {
                return;
            }

            Document document = result.getSnapshot().getSource().getDocument(false);
            List<ErrorDescription> errors = new ArrayList<ErrorDescription>();

        try {
            int start = NbDocument.findLineOffset ((StyledDocument) document, error.line - 1) + error.column - 1;

            ErrorDescription errorDescription = ErrorDescriptionFactory.createErrorDescription(
                    Severity.ERROR,
                    error.message,
                    document,
                    document.createPosition(start),
                    document.createPosition(start)
            );

            errors.add(errorDescription);
            HintsController.setErrors(document, "cs", errors);

//            for (SyntaxError syntaxError : syntaxErrors) {
//                RecognitionException exception = syntaxError.exception;
//                String message = syntaxError.message;
//
//                int line = exception.line;
//                if (line <= 0) {
//                    continue;
//                }
//            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public Class<? extends Scheduler> getSchedulerClass() {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel() {}
}