package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.vb;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetSubProject;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 * VB.NET Sub-Project implementation
 *
 * @author ChrisLE
 */
public class VBSubProject extends AbstractDotNetSubProject {

    @StaticResource()
    public static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/vbproj.svg";

    VBSubProject(FileObject dir, ProjectState state, String vbProjName) {
        super(dir, state, vbProjName);
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
        return "Projects/org-vb-subproject/Nodes";
    }
}
