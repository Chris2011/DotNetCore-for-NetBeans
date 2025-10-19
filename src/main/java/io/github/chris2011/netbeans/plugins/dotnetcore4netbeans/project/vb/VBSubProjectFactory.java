package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.vb;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.AbstractDotNetSubProjectFactory;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 * Factory for VB.NET sub-projects
 *
 * @author ChrisLE
 */
@ServiceProvider(service = ProjectFactory.class)
public class VBSubProjectFactory extends AbstractDotNetSubProjectFactory {

    @Override
    protected String getProjectFileExtension() {
        return "vbproj";
    }

    @Override
    protected Project createProject(FileObject dir, ProjectState state, String projectFileName) {
        return new VBSubProject(dir, state, projectFileName);
    }
}
