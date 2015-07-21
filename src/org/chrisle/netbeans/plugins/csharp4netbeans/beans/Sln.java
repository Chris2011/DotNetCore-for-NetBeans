/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.chrisle.netbeans.plugins.csharp4netbeans.beans;

import java.util.UUID;

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
    private final String _commentVersion = "# Netbeans 8.0.2"; // TODO: Dynamic Netbeans version.

    private String _projectParams;

//    private final String _vcppguid = "8BC9CEB8-8B4A-11D0-8D11-00A0C91BC942";
    private final String _vcsharpguid = "FAE04EC0-301F-11D3-BF4B-00C04F79EFBC";
//    private final String _webguid = "E24C65DC-7377-472b-9ABA-BC803B73C61A";
//    private final String _siguid = "2150E333-8FDC-42A3-9474-1A3956D46DE8";
    private String _projName;
    private String _projPath;

    public String getGlobal() {
        return String.format("Global\n{0}\n{1}\n{2}\nEndGlobal\n", this.getGlobalSlnConfigSection(), null, this.getGlobalSlnPropsSection());
    }

    public String getVersionsHeader() {
        return String.format("\n{0}, {1} {2} \n{3}\n", this._visualStudioSlnFile, this._formatVersion, this._numericVersion, this._commentVersion);
    }

    public String getProjName() {
        return _projName;
    }

    public void setProjName(String _projName) {
        this._projName = _projName;
    }

    public String getProjPath() {
        return _projPath;
    }

    public void setProjPath(String _projPath) {
        this._projPath = _projPath;
    }

    public String getProjGuid() {
        return UUID.randomUUID().toString();
    }

    private String getProjectId() {
        return String.format("Project(\"{{0}}\") = {1}, {2}, {3}", this._vcsharpguid, this._projName, this._projPath, this.getProjGuid());
    }

    public String getProject() {
        return String.format("{0} \n EndProject\n", getProjectId());
    }

    private String getGlobalSlnConfigSection() {
        return "GlobalSection(SolutionConfigurationPlatforms) = preSolution \n Debug|Any CPU = Debug|Any CPU \n Release|Any CPU = Release Any CPU \n EndGlobalSection \n";
    }
    
    private String getGlobalSlnPropsSection() {
        return "GlobalSection(SolutionProperties) = preSolution \n HideSolutionNode = FALSE \n EndGlobalSection \n";
    }
}