package org.chrisle.netbeans.plugins.csharp4netbeans.subproject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.chrisle.netbeans.plugins.csharp4netbeans.solution.CSharpSolution;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.project.SubprojectProvider;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author ChrisLE
 */
public class CSharpSubProjectProvider implements SubprojectProvider {

    private final CSharpSolution project;

    public CSharpSubProjectProvider(CSharpSolution project) {
        this.project = project;
    }

    @Override
    public Set<? extends Project> getSubprojects() {
        return loadProjects(project.getProjectDirectory());
    }

    private Set loadProjects(FileObject dir) {
        Set newProjects = new HashSet();
//        FileObject reportsFolder = dir.getFileObject("reports");
        
        if (dir != null) {
            for (FileObject childFolder : dir.getChildren()) {
                try {
                    Project subp = ProjectManager.getDefault().findProject(childFolder);
                    if (subp != null && subp instanceof CSharpSubProject) {
                        newProjects.add((CSharpSubProject) subp);
                    }
                } catch (IOException | IllegalArgumentException ex) {
                    // TODO: Figure out, what happens here.
//                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return Collections.unmodifiableSet(newProjects);
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
    }
}