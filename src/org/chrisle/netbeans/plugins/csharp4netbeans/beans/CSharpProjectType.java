package org.chrisle.netbeans.plugins.csharp4netbeans.beans;

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

    public void setProjName(String projName) {
        this._projName = projName;
    }
    
    public String getProjName() {
        return _projName;
    }
    private String _projPath;

    public String getGlobal() {
        return String.format("Global\n%s\n%s\n%s\nEndGlobal\n", this.getGlobalSlnConfigSection(), null, this.getGlobalSlnPropsSection());
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

    private String getProjectId() {
        return String.format("Project(\"{%s}\") = %s, %s, %s", this._vcsharpguid, this._projName, this._projPath, this.getProjGuid());
    }

    public String getProjectInfo() {
        return String.format("%s \n EndProject\n", getProjectId());
    }

    private String getGlobalSlnConfigSection() {
        return "GlobalSection(SolutionConfigurationPlatforms) = preSolution \n Debug|Any CPU = Debug|Any CPU \n Release|Any CPU = Release Any CPU \n EndGlobalSection \n";
    }
    
    private String getGlobalSlnPropsSection() {
        return "GlobalSection(SolutionProperties) = preSolution \n HideSolutionNode = FALSE \n EndGlobalSection \n";
    }
}