package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.vcpp;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetProjectType;

/**
 * Visual C++ Project Type implementation
 *
 * @author ChrisLE
 */
public class VCppProjectType extends AbstractDotNetProjectType {
    // Visual C++ project type GUID
    private static final String VCPP_GUID = "8BC9CEB8-8B4A-11D0-8D11-00A0C91BC942";
    private static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/vcxproj.svg";
    private static final String NODE_FACTORY_PATH = "Projects/org-vcpp-subproject/Nodes";

    @Override
    public String getProjectTypeGuid() {
        return VCPP_GUID;
    }

    @Override
    public String getProjectFileExtension() {
        return "vcxproj";
    }

    @Override
    public String getIconResourcePath() {
        return PROJECT_ICON;
    }

    @Override
    public String getDisplayName() {
        return "Visual C++ Project";
    }

    @Override
    public String getNodeFactoryPath() {
        return NODE_FACTORY_PATH;
    }
}
