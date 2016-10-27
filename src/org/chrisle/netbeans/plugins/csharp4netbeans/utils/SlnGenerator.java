package org.chrisle.netbeans.plugins.csharp4netbeans.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.chrisle.netbeans.plugins.csharp4netbeans.beans.Sln;
import org.openide.util.Exceptions;

/**
 *
 * @author ChrisLE
 */
public class SlnGenerator {
    private final Sln _sln;
    
    public SlnGenerator(Sln sln) {
        this._sln = sln;
    }
    
    public void createSlnFile() throws IOException {
        File dir = new File(this._sln.getSlnPath());
        File slnFile = new File(dir, this._sln.getSlnName() + ".sln");
        
        this._sln.setSlnFile(slnFile);

        try (FileWriter fileWriter = new FileWriter(slnFile)) {
            fileWriter.write(_sln.getVersionsHeader());
            fileWriter.append(_sln.getMinimumVisualStudioVersion());
            fileWriter.append(_sln.getProjectSection());
            fileWriter.append(_sln.getGlobal());
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}