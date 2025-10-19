package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;

/**
 * Abstract base class for .NET sub-project factories
 *
 * @author ChrisLE
 */
public abstract class AbstractDotNetSubProjectFactory implements ProjectFactory {
    private String _projectFileName = "";

    /**
     * Get the project file extension (without dot)
     * @return The extension (e.g., "csproj", "vbproj")
     */
    protected abstract String getProjectFileExtension();

    /**
     * Create a new project instance
     * @param dir The project directory
     * @param state The project state
     * @param projectFileName The project file name (without extension)
     * @return A new project instance
     */
    protected abstract Project createProject(FileObject dir, ProjectState state, String projectFileName);

    @Override
    public boolean isProject(FileObject projectDirectory) {
        FileObject[] data = projectDirectory.getChildren();

        for (FileObject fileObject : data) {
            if (fileObject.hasExt(getProjectFileExtension()) && !fileObject.isFolder()) {
                _projectFileName = fileObject.getName();
                // Remove extension if present
                _projectFileName = _projectFileName.replaceAll("\\." + getProjectFileExtension() + "$", "");
                return true;
            }
        }

        return false;
    }

    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? createProject(dir, state, _projectFileName) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        // leave unimplemented for the moment
    }

    protected String getProjectFileName() {
        return _projectFileName;
    }
}
