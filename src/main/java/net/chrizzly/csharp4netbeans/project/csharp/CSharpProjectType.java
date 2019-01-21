package net.chrizzly.csharp4netbeans.project.csharp;

import java.util.UUID;

/**
 *
 * @author ChrisLE
 */
public class CSharpProjectType {
    private String _projectParams;

    private final String _vcsharpguid = "FAE04EC0-301F-11D3-BF4B-00C04F79EFBC";

    private String _slnPath;
    private String _projName;
    private String _projPath;

    public void setProjName(String projName) {
        this._projName = projName;
    }
    
    public String getProjName() {
        return _projName;
    }

    public String getSlnPath() {
        return _slnPath;
    }

    public void setSlnPath(String slnPath) {
        this._slnPath = slnPath;
    }

    public String getProjGuid() {
        return UUID.randomUUID().toString();
    }

    public String getProjectInfo() {
        return String.format("Project(\"{%s}\") = %s, %s, %s\nEndProject\n", this._vcsharpguid, this._projName, this._projPath, this.getProjGuid());
    }

    private String getGlobalSlnConfigSection() {
        return "GlobalSection(SolutionConfigurationPlatforms) = preSolution \n Debug|Any CPU = Debug|Any CPU \n Release|Any CPU = Release Any CPU \n EndGlobalSection \n";
    }
    
    private String getGlobalSlnPropsSection() {
        return "GlobalSection(SolutionProperties) = preSolution \n HideSolutionNode = FALSE \n EndGlobalSection \n";
    }
}