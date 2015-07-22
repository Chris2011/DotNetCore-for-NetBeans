package org.chrisle.netbeans.plugins.csharp4netbeans.utils;

import java.io.File;
import java.io.FileWriter;
import org.chrisle.netbeans.plugins.csharp4netbeans.beans.CSharpProjectType;
import org.openide.util.Exceptions;

/**
 *
 * @author ChrisLE
 */
public class ProjectGenerator {
    private final CSharpProjectType _projType;

    public ProjectGenerator(CSharpProjectType pt) {
        this._projType = pt;
    }

    public void createProjFolder() {
        File slnDir = new File(this._projType.getSlnPath());
        File projDir = new File(slnDir, this._projType.getProjName());
        projDir.mkdirs();

        File projFile = new File(projDir, this._projType.getProjName() + ".csproj");

        try (FileWriter fileWriter = new FileWriter(projFile)) {
            fileWriter.write("# Test");
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void addProjectSettingsToSln(File slnFile) {
        
    }
}