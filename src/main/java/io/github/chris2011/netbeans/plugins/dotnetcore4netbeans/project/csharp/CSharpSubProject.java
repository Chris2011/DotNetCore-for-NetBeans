package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.csharp;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetSubProject;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 * C# Sub-Project implementation
 *
 * @author ChrisLE
 */
public class CSharpSubProject extends AbstractDotNetSubProject {
    @StaticResource()
    public static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/csproj.svg";

    @StaticResource()
    public static final String CSHARP_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/csharp-project-folder.png";

    CSharpSubProject(FileObject dir, ProjectState state, String csProjName) {
        super(dir, state, csProjName);
    }

    @Override
    protected String getProjectIconPath() {
        return PROJECT_ICON;
    }

    @Override
    protected String getProjectSmallIconPath() {
        return CSHARP_ICON;
    }

    @Override
    protected String getNodeFactoryPath() {
        return "Projects/org-csharp-subproject/Nodes";
    }
}