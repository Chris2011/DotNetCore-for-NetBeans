package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.CSharpSolution;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * Children factory that creates Visual Studio-like structure: - Solution
 * Folders - Projects (from .sln file)
 */
public class SolutionProjectChildren extends ChildFactory<Object> {

    private final CSharpSolution solution;

    public SolutionProjectChildren(CSharpSolution solution) {
        this.solution = solution;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        try {
            FileObject solutionDir = solution.getProjectDirectory();
            SolutionParser.SolutionStructure structure = SolutionParser.parseSolution(solutionDir);

            List<SolutionParser.SolutionFolder> rootFolders = new ArrayList<>(structure.getRootFolders());
            rootFolders.sort(Comparator.comparing(SolutionParser.SolutionFolder::getName, String.CASE_INSENSITIVE_ORDER));
            toPopulate.addAll(rootFolders);

            List<SolutionParser.ProjectInfo> rootProjects = new ArrayList<>();
            for (SolutionParser.ProjectInfo project : structure.getRootProjects()) {
                if (!isProjectInVirtualFolder(project, structure)) {
                    rootProjects.add(project);
                }
            }

            rootProjects.sort(Comparator.comparing(SolutionParser.ProjectInfo::getName, String.CASE_INSENSITIVE_ORDER));
            toPopulate.addAll(rootProjects);
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Object key) {
        if (key instanceof SolutionParser.SolutionFolder) {
            // Virtual solution folder from .sln
            return new SolutionFolderNode((SolutionParser.SolutionFolder) key, solution);
        } else if (key instanceof SolutionParser.ProjectInfo) {
            // Project from .sln
            return new SolutionProjectNode((SolutionParser.ProjectInfo) key);
        }
        return null;
    }

    private boolean isProjectInVirtualFolder(SolutionParser.ProjectInfo project, SolutionParser.SolutionStructure structure) {
        String cleanProjectGuid = SolutionParser.cleanGuid(project.getProjectGuid());
        String parentGuid = structure.getParentByGuid().get(cleanProjectGuid);

        return parentGuid != null && structure.getFolderByGuid().containsKey(parentGuid);
    }

    /**
     * Create Children instance for this factory.
     */
    public static Children create(CSharpSolution solution) {
        return Children.create(new SolutionProjectChildren(solution), true);
    }
}
