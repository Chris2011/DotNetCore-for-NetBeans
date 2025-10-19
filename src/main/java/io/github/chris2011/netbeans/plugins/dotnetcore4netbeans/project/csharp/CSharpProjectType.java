package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.csharp;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetProjectType;

/**
 * C# Project Type implementation
 *
 * @author ChrisLE
 */
public class CSharpProjectType extends AbstractDotNetProjectType {
    private static final String VCSHARP_GUID = "FAE04EC0-301F-11D3-BF4B-00C04F79EFBC";
    private static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/csproj.svg";
    private static final String NODE_FACTORY_PATH = "Projects/org-csharp-subproject/Nodes";

    @Override
    public String getProjectTypeGuid() {
        return VCSHARP_GUID;
    }

    @Override
    public String getProjectFileExtension() {
        return "csproj";
    }

    @Override
    public String getIconResourcePath() {
        return PROJECT_ICON;
    }

    @Override
    public String getDisplayName() {
        return "C# Project";
    }

    @Override
    public String getNodeFactoryPath() {
        return NODE_FACTORY_PATH;
    }
}