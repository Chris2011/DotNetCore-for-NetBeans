package org.chrisle.netbeans.plugins.csharp4netbeans.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.openide.util.Exceptions;

/**
 *
 * @author ChrisLE
 */
public class SlnGenerator {
    private final String _headInformation;

    public SlnGenerator() {
        this._headInformation = "\n"+
                                "Microsoft Visual Studio Solution File, Format Version 12.00\n" +
                                "# Netbeans\n";
    }
    
    public void createSlnFile(String projectDirName, String projectFileName) throws IOException {
        File dir = new File(projectDirName);
        File slnFile = new File(dir, projectFileName);

        try (FileWriter fileWriter = new FileWriter(slnFile)) {
            fileWriter.write(this._headInformation);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}