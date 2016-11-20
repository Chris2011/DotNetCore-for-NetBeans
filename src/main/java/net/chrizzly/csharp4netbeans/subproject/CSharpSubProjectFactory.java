package net.chrizzly.csharp4netbeans.subproject;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectFactory;
import org.netbeans.spi.project.ProjectState;
import org.openide.filesystems.FileObject;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author ChrisLE
 */
@ServiceProvider(service=ProjectFactory.class)
public class CSharpSubProjectFactory implements ProjectFactory {
    public static final String PROJECT_FILE_EXT = "csproj";
    public String _csProjFileName = "";
    
    //Specifies when a project is a project, i.e.,
    //if "*.csproj" is present in a folder:
    @Override
    public boolean isProject(FileObject projectDirectory) {
        FileObject[] data = projectDirectory.getChildren();
        
        for (FileObject fileObject : data) {    
            if (fileObject.hasExt(PROJECT_FILE_EXT) && !fileObject.isFolder()) {
                _csProjFileName = fileObject.getName();
                _csProjFileName = _csProjFileName.replace(".csproj$", "");
                
                return true;
            }
        }
        
        return false;
    }

    //Specifies when the project will be opened, i.e., if the project exists:
    @Override
    public Project loadProject(FileObject dir, ProjectState state) throws IOException {
        return isProject(dir) ? new CSharpSubProject(dir, state, _csProjFileName) : null;
    }

    @Override
    public void saveProject(final Project project) throws IOException, ClassCastException {
        // leave unimplemented for the moment
    }
}