package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.fsharp;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetSubProject;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 * F# Sub-Project implementation
 *
 * @author ChrisLE
 */
public class FSharpSubProject extends AbstractDotNetSubProject {
    @StaticResource()
    public static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/fsproj.svg";

//    @StaticResource()
//    public static final String FSHARP_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/fsharp-project-folder.png";

    FSharpSubProject(FileObject dir, ProjectState state, String fsProjName) {
        super(dir, state, fsProjName);
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
        return "Projects/org-fsharp-subproject/Nodes";
    }
}
