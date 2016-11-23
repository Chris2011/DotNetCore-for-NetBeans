package net.chrizzly.csharp4netbeans.beans;

import java.io.File;

/**
 *
 * @author ChrisLE
 */
public class Sln {

    private final String _visualStudioSlnFile = "Microsoft Visual Studio Solution File";
    private final String _formatVersion = "Format Version";
    private final String _numericVersion = "12.00"; // TODO numeric-version = nv_2002 | nv_2003 | nv_2005 | nv_2008 | nv_2010 | nv_2012  
                                                    // nv_2002 = "7.00"  
                                                    // nv_2003 = "8.00"  
                                                    // nv_2005 = "9.00"  
                                                    // nv_2008 = "10.00"  
                                                    // nv_2010 = "11.00"  
                                                    // nv_2012 = "12.00" 
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