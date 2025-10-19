package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.vb;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetProjectType;

/**
 * VB.NET Project Type implementation
 *
 * @author ChrisLE
 */
public class VBProjectType extends AbstractDotNetProjectType {
    // Visual Basic project type GUID
    private static final String VBNET_GUID = "F184B08F-C81C-45F6-A57F-5ABD9991F28F";
    private static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/vbproj.svg";
    private static final String NODE_FACTORY_PATH = "Projects/org-vb-subproject/Nodes";

    @Override
    public String getProjectTypeGuid() {
        return VBNET_GUID;
    }

    @Override
    public String getProjectFileExtension() {
        return "vbproj";
    }

    @Override
    public String getIconResourcePath() {
        return PROJECT_ICON;
    }

    @Override
    public String getDisplayName() {
        return "VB.NET Project";
    }

    @Override
    public String getNodeFactoryPath() {
        return NODE_FACTORY_PATH;
    }
}
