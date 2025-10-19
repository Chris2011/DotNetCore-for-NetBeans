package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project;

import java.util.UUID;

/**
 * Abstract base class for .NET project types
 *
 * @author ChrisLE
 */
public abstract class AbstractDotNetProjectType implements DotNetProjectType {
    private String _projectParams;
    private String _slnPath;
    private String _projName;
    private String _projPath;

    @Override
    public void setProjName(String projName) {
        this._projName = projName;
    }

    @Override
    public String getProjName() {
        return _projName;
    }

    @Override
    public String getSlnPath() {
        return _slnPath;
    }

    @Override
    public void setSlnPath(String slnPath) {
        this._slnPath = slnPath;
    }

    @Override
    public String getProjGuid() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getProjectInfo() {
        return String.format("Project(\"{%s}\") = %s, %s, %s\nEndProject\n",
                this.getProjectTypeGuid(),
                this._projName,
                this._projPath,
                this.getProjGuid());
    }

    protected String getGlobalSlnConfigSection() {
        return "GlobalSection(SolutionConfigurationPlatforms) = preSolution \n Debug|Any CPU = Debug|Any CPU \n Release|Any CPU = Release Any CPU \n EndGlobalSection \n";
    }

    protected String getGlobalSlnPropsSection() {
        return "GlobalSection(SolutionProperties) = preSolution \n HideSolutionNode = FALSE \n EndGlobalSection \n";
    }

    public String getProjPath() {
        return _projPath;
    }

    public void setProjPath(String projPath) {
        this._projPath = projPath;
    }

    public String getProjectParams() {
        return _projectParams;
    }

    public void setProjectParams(String projectParams) {
        this._projectParams = projectParams;
    }
}
