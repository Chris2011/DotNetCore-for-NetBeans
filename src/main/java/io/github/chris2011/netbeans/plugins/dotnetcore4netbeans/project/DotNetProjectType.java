package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project;

/**
 * Interface representing a .NET project type (C#, VB, F#, C++)
 *
 * @author ChrisLE
 */
public interface DotNetProjectType {

    /**
     * Get the project type GUID for Visual Studio solution files
     * @return The project type GUID
     */
    String getProjectTypeGuid();

    /**
     * Get the file extension for this project type (e.g., "csproj", "vbproj")
     * @return The file extension without dot
     */
    String getProjectFileExtension();

    /**
     * Get the project name
     * @return The project name
     */
    String getProjName();

    /**
     * Set the project name
     * @param projName The project name
     */
    void setProjName(String projName);

    /**
     * Get the solution path
     * @return The solution path
     */
    String getSlnPath();

    /**
     * Set the solution path
     * @param slnPath The solution path
     */
    void setSlnPath(String slnPath);

    /**
     * Get a unique project GUID
     * @return A new UUID string
     */
    String getProjGuid();

    /**
     * Get the project information formatted for a solution file
     * @return The formatted project information
     */
    String getProjectInfo();

    /**
     * Get the icon resource path for this project type
     * @return The resource path to the icon
     */
    String getIconResourcePath();

    /**
     * Get the display name for this project type
     * @return The display name (e.g., "C# Project", "VB.NET Project")
     */
    String getDisplayName();

    /**
     * Get the node factory path for logical view
     * @return The path for NodeFactorySupport
     */
    String getNodeFactoryPath();
}
