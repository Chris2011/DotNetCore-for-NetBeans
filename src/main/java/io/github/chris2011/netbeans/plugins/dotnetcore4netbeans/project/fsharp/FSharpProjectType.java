package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.fsharp;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetProjectType;

/**
 * F# Project Type implementation
 *
 * @author ChrisLE
 */
public class FSharpProjectType extends AbstractDotNetProjectType {
    // F# project type GUID
    private static final String FSHARP_GUID = "F2A71F9B-5D33-465A-A702-920D77279786";
    private static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/fsproj.svg";
    private static final String NODE_FACTORY_PATH = "Projects/org-fsharp-subproject/Nodes";

    @Override
    public String getProjectTypeGuid() {
        return FSHARP_GUID;
    }

    @Override
    public String getProjectFileExtension() {
        return "fsproj";
    }

    @Override
    public String getIconResourcePath() {
        return PROJECT_ICON;
    }

    @Override
    public String getDisplayName() {
        return "F# Project";
    }

    @Override
    public String getNodeFactoryPath() {
        return NODE_FACTORY_PATH;
    }
}
