package net.chrizzly.csharp4netbeans.solution;

import java.io.File;

/**
 *
 * @author ChrisLE
 */
public class Sln {

    private final String _visualStudioSlnFile = "Microsoft Visual Studio Solution File";
    private final String _formatVersion = "Format Version";
    private final String _numericVersion = "12.00";
    private final String _commentVersion = "# " + System.getProperty("netbeans.productversion"); // TODO: Dynamic Netbeans version.
    private final String _minimumVSVersion = "MinimumVisualStudioVersion = 10.0.40219.1\n\n";

    private String _slnName;
    private String _slnPath;
    private File _slnFile;

    public String getGlobal() {
        return String.format("Global\n%s   %s\n%sEndGlobal\n", this.getGlobalSlnConfigSection(), this.getGlobalProjectSettingsSection(), this.getGlobalSlnPropsSection());
    }

    public String getVersionsHeader() {
        return String.format("\n%s, %s %s\n%s\n\n", this._visualStudioSlnFile, this._formatVersion, this._numericVersion, this._commentVersion);
    }

    public String getSlnName() {
        return _slnName;
    }

    public void setSlnName(String slnName) {
        this._slnName = slnName;
    }

    public String getSlnPath() {
        return _slnPath;
    }

    public void setSlnPath(String slnPath) {
        this._slnPath = slnPath;
    }
    
    /**
     * Adds Projects to the SLN file like in this format
     * Project("{8BB2217D-0F2D-49D1-97BC-3654ED321F3B}") = "WebApplication1", "src\WebApplication1\WebApplication1.xproj", "{CE0F9028-0B70-40ED-9DDF-E912208E84EA}"
     * @param projectGuid 
     */
    public void addProject(String projectGuid) {
        // TODO: Add Project Guid and Path to the SLN file.
    }
    
    /**
     * Removed a project in case of using the project guid from the sln file.
     * @param projectGuid 
     */
    public void removeProject(String projectGuid) {
        // TODO: Remove Project Guid and Path from SLN file.
    }
    
    public String getProjectSection() {
        return "# Created with Netbeans\n# StartProjectSection\n# EndProjectSection\n\n";
    }

    private String getGlobalSlnConfigSection() {
        return "   GlobalSection(SolutionConfigurationPlatforms) = preSolution\n      Debug|Any CPU = Debug|Any CPU\n      Release|Any CPU = Release Any CPU\n   EndGlobalSection\n\n";
    }
    
    private String getGlobalProjectSettingsSection() {
        return "# Created with Netbeans\n   # StartProjectGlobalSection\n   # EndProjectGlobalSection\n";
    }
    
    private String getGlobalSlnPropsSection() {
        return "   GlobalSection(SolutionProperties) = preSolution\n      HideSolutionNode = FALSE\n   EndGlobalSection\n";
    }

    public String getMinimumVisualStudioVersion() {
        return this._minimumVSVersion;
    }

    public void setSlnFile(File slnFile) {
        this._slnFile = slnFile;
    }
    
    public File getSlnFile() {
        return _slnFile;
    }
}