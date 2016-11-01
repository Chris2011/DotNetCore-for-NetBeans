package org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.jccparser.JavaParser;
import org.chrisle.netbeans.plugins.csharp4netbeans.filetypes.cs.jccparser.ParseException;
import org.netbeans.modules.csl.api.Error;
import org.netbeans.modules.csl.spi.ParserResult;
import org.netbeans.modules.parsing.api.Snapshot;
import org.netbeans.modules.parsing.api.Task;
import org.netbeans.modules.parsing.spi.Parser;
import org.netbeans.modules.parsing.spi.SourceModificationEvent;

/**
 *
 * @author Chrizzly
 */
public class CsParser extends Parser {
    private Snapshot snapshot;
    private JavaParser javaParser;

    @Override
    public void parse (Snapshot snapshot, Task task, SourceModificationEvent event) {
        this.snapshot = snapshot;
        Reader reader = new StringReader(snapshot.getText().toString ());
        javaParser = new JavaParser(reader);
        try {
            javaParser.CompilationUnit ();
        } catch (ParseException ex) {
            Logger.getLogger (CsParser.class.getName()).log (Level.WARNING, null, ex);
        }
    }

    @Override
    public ParserResult getResult (Task task) {
        return new CsParserResult (snapshot, javaParser);
    }

    @Override
    public void cancel () {
    }

    @Override
    public void addChangeListener (ChangeListener changeListener) {
    }

    @Override
    public void removeChangeListener (ChangeListener changeListener) {
    }

    
    public static class CsParserResult extends ParserResult {
        private JavaParser javaParser;
        private boolean valid = true;

        CsParserResult (Snapshot snapshot, JavaParser javaParser) {
            super (snapshot);
            this.javaParser = javaParser;
        }

        public JavaParser getJavaParser () throws org.netbeans.modules.parsing.spi.ParseException {
            if (!valid) throw new org.netbeans.modules.parsing.spi.ParseException ();
            return javaParser;
        }

        @Override
        protected void invalidate () {
            valid = false;
        }

        @Override
        public List<? extends Error> getDiagnostics() {
            return new ArrayList<Error>();
        }
    }   
}