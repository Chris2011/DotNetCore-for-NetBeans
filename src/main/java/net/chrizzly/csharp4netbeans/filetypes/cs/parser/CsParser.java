package net.chrizzly.csharp4netbeans.filetypes.cs.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import net.chrizzly.csharp4netbeans.filetypes.cs.CSharpParser;
import net.chrizzly.csharp4netbeans.filetypes.cs.FaultTolerantCSharpLexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.ParseException;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;
import org.openide.util.Exceptions;

/**
 *
 * @author Chrizzly
 */
public class CsParser extends Parser {

    private Snapshot snapshot;
    private CSharpParser cSharpParser;

    private ParseTree tree;
    private CsError error;

//    @Override
//    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) {
//        this.snapshot = snapshot;
//        ANTLRStringStream input = new ANTLRStringStream(snapshot.getText().toString());
////        Lexer lexer = new CSharpLexer(input);
////        CommonTokenStream tokens = new CommonTokenStream(lexer);
////        cSharpParser = new CSharpParser(tokens);
////        try {
////            cSharpParser.prog();
////        } catch (Exception ex) {
////            ex.printStackTrace();
////        }
//    }
    @Override
    public void parse(Snapshot snapshot, Task task, SourceModificationEvent event) {
        try {
            this.snapshot = snapshot;
//        this.error = null;
            this.tree = null;

            Reader reader = new StringReader(snapshot.getText().toString());

            ANTLRInputStream stream = new ANTLRInputStream(reader);
//            final ANTLRErrorListener errorReporter = new CsErrorReporter();
            Lexer lexer = new FaultTolerantCSharpLexer(stream);
//            lexer.addErrorListener(errorReporter);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CSharpParser parser = new CSharpParser(tokens);
//            parser.addErrorListener(errorReporter);
            parser.getInterpreter().setPredictionMode(PredictionMode.SLL);
//            tree = parser.template();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public Result getResult(Task task) {
        return new CsEditorParserResult(snapshot, cSharpParser);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void addChangeListener(ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener(ChangeListener changeListener) {
    }

    public static class CsEditorParserResult extends ParserResult {
        private final CSharpParser cSharpParser;
        private boolean valid = true;
        private CsError error;

        CsEditorParserResult(Snapshot snapshot, CSharpParser cSharpParser) {
            super(snapshot);
            this.cSharpParser = cSharpParser;
        }

        public CSharpParser getCSharpParser()
                throws ParseException {
            if (!valid) {
                throw new ParseException();
            }
            return cSharpParser;
        }

        public CsError getError() {
            return this.error;
        }

        @Override
        protected void invalidate() {
            valid = false;
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            return new ArrayList<>();
        }
    }
}
