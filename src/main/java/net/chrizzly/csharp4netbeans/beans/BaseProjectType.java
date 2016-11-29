package net.chrizzly.csharp4netbeans.beans;

import java.util.UUID;

/**
 *
 * @author Chrizzly
 */
public class BaseProjectType {
    private String _slnPath;
    private String _projectName;
    private String _projectFileLocation;
    private String _projectTypeGuid;
    private String _uniqueProjectGuid;

    public String getSlnPath() {
        return _slnPath;
    }
    
    public void setSlnPath(String slnPath) {
        this._slnPath = slnPath;
    }
    
    public String getProjName() {
        return _projectName;
    }

    public void setProjName(String projName) {
        this._projectName = projName;
    }

    public String getUniqueProjectGuid() {
        return this._uniqueProjectGuid;
    }

    public void setUniqueProjectGuid() {
        this._uniqueProjectGuid = UUID.randomUUID().toString();
    }
   
    public String getProjGuid() {
        return UUID.randomUUID().toString();
    }

    public void setProjTypeGuid(String projTypeGuid) {
        this._projectTypeGuid = projTypeGuid;
    }

    public String getProjectInfo() {
        return String.format("Project(\"{%s}\") = %s, %s, %s\nEndProject\n", this._projectTypeGuid, this._projectName, this._projectFileLocation, this.getProjGuid());
    }

    private String getGlobalSlnConfigSection() {
        return "GlobalSection(SolutionConfigurationPlatforms) = preSolution \n Debug|Any CPU = Debug|Any CPU \n Release|Any CPU = Release Any CPU \n EndGlobalSection \n";
    }

    private String getGlobalSlnPropsSection() {
        return "GlobalSection(SolutionProperties) = preSolution \n HideSolutionNode = FALSE \n EndGlobalSection \n";
    }
}
