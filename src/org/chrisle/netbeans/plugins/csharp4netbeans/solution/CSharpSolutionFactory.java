package org.chrisle.netbeans.plugins.csharp4netbeans.solution;

import java.io.IOException;
import java.util.Enumeration;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author ChrisLE
 */
@ServiceProvider(service = ProjectFactory.class)
public class CSharpSolutionFactory implements ProjectFactory {

    public static final String PROJECT_FILE_EXT = "sln";
    public String _slnFileName = "";

    //Specifies when a project is a project, i.e.,
    //if "*.sln" is present in a folder:
    @Override
    public boolean isProject(FileObject projectDirectory) {
        Enumeration<? extends FileObject> data = projectDirectory.getData(true);
        
        while (data.hasMoreElements()) {
            FileObject nextElement = data.nextElement();
            
            if (nextElement.hasExt(PROJECT_FILE_EXT)) {
                _slnFileName = nextElement.getName().replace(".sln$", "");
                
                return projectDirectory.getFileObject(_slnFileName) != null;
            }
        }

        return false;
    }

    //Specifies when the project will be opened, i.e., if the project exists:
    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new CSharpSolution(dir, state, _slnFileName) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        // leave unimplemented for the moment
    }
}
