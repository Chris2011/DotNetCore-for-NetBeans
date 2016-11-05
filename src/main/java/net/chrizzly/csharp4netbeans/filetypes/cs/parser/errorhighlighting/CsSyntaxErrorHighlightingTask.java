package net.chrizzly.csharp4netbeans.filetypes.cs.parser.errorhighlighting;

import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import net.chrizzly.csharp4netbeans.filetypes.cs.jccparser.ParseException;
import net.chrizzly.csharp4netbeans.filetypes.cs.jccparser.Token;
import net.chrizzly.csharp4netbeans.filetypes.cs.parser.CsParser.CsParserResult;
import org.netbeans.modules.parsing.spi.Parser.Result;
import org.netbeans.modules.parsing.spi.ParserResultTask;
import org.netbeans.modules.parsing.spi.Scheduler;
import org.netbeans.modules.parsing.spi.SchedulerEvent;
import org.netbeans.spi.editor.hints.ErrorDescription;
import org.netbeans.spi.editor.hints.ErrorDescriptionFactory;
import org.netbeans.spi.editor.hints.HintsController;
import org.netbeans.spi.editor.hints.Severity;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;

/**
 *
 * @author chrl
 */
public class CsSyntaxErrorHighlightingTask extends ParserResultTask {

    @Override
    public void run (Result result, SchedulerEvent event) {
        try {
            CsParserResult sjResult = (CsParserResult) result;
            List<ParseException> syntaxErrors = sjResult.getJavaParser ().syntaxErrors;
            Document document = result.getSnapshot ().getSource ().getDocument (false);
            List<ErrorDescription> errors = new ArrayList<ErrorDescription> ();
            for (ParseException syntaxError : syntaxErrors) {
                Token token = syntaxError.currentToken;
                int start = NbDocument.findLineOffset ((StyledDocument) document, token.beginLine - 1) + token.beginColumn - 1;
                int end = NbDocument.findLineOffset ((StyledDocument) document, token.endLine - 1) + token.endColumn;
                ErrorDescription errorDescription = ErrorDescriptionFactory.createErrorDescription(
                    Severity.ERROR,
                    syntaxError.getMessage (),
                    document,
                    document.createPosition(start),
                    document.createPosition(end)
                );
                errors.add (errorDescription);
            }
            HintsController.setErrors (document, "C#", errors);
        } catch (BadLocationException ex1) {
            Exceptions.printStackTrace (ex1);
        } catch (org.netbeans.modules.parsing.spi.ParseException ex1) {
            Exceptions.printStackTrace (ex1);
        }
    }

    @Override
    public int getPriority () {
        return 100;
    }

    @Override
    public Class getSchedulerClass () {
        return Scheduler.EDITOR_SENSITIVE_TASK_SCHEDULER;
    }

    @Override
    public void cancel () {
    }
}