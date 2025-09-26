package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.csharp.CSharpProjectType;
import org.openide.util.Exceptions;

/**
 * @deprecated Use .NET CLI commands instead: "dotnet new <template> -n <name>"
 * and "dotnet sln add <project>"
 * @author ChrisLE
 */
@Deprecated
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

    public void addProjectSettingsToSln(File slnFile) throws FileNotFoundException, IOException {
        BufferedReader bufReader = new BufferedReader(new FileReader(slnFile));
        StringBuffer strBuffer = new StringBuffer();
        String line;

        while ((line = bufReader.readLine()) != null) {
            if (line.equals("# StartProjectSection")) {
                strBuffer.append(line).append("\n").append(this._projType.getProjectInfo());
            }
        }

        bufReader.close();
    }
}
