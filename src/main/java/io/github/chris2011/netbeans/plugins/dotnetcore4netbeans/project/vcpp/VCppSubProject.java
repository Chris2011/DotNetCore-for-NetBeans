package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.vcpp;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetSubProject;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 * Visual C++ Sub-Project implementation
 *
 * @author ChrisLE
 */
public class VCppSubProject extends AbstractDotNetSubProject {

    @StaticResource()
    public static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/vcxproj.svg";

    VCppSubProject(FileObject dir, ProjectState state, String vcxProjName) {
        super(dir, state, vcxProjName);
    }

    @Override
    protected String getProjectIconPath() {
        return PROJECT_ICON;
    }

    @Override
    protected String getProjectSmallIconPath() {
        return PROJECT_ICON;
    }

    @Override
    protected String getNodeFactoryPath() {
        return "Projects/org-vcpp-subproject/Nodes";
    }
}
