package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.CSharpSolution;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser;
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 * Node representing a Solution Folder (like "common", "tests", etc.)
 */
public class SolutionFolderNode extends AbstractNode {

    @StaticResource
    public static final String FOLDER_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/folder.svg";

    private final SolutionParser.SolutionFolder folder;
    private final CSharpSolution solution;

    public SolutionFolderNode(SolutionParser.SolutionFolder folder, CSharpSolution solution) {
        super(Children.create(new SolutionFolderChildrenFactory(folder, solution), true), Lookups.fixed(folder, solution));
        this.folder = folder;
        this.solution = solution;
        setDisplayName(folder.getName());
        setShortDescription("Solution Folder: " + folder.getName());
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(FOLDER_ICON);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            CommonProjectActions.newFileAction(),
            null, // Separator
        };
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    public SolutionParser.SolutionFolder getFolder() {
        return folder;
    }

    /**
     * Children factory for solution folder contents (projects within the folder)
     */
    private static class SolutionFolderChildrenFactory extends ChildFactory<SolutionParser.ProjectInfo> {

        private final SolutionParser.SolutionFolder folder;
        private final CSharpSolution solution;

        public SolutionFolderChildrenFactory(SolutionParser.SolutionFolder folder, CSharpSolution solution) {
            this.folder = folder;
            this.solution = solution;
        }

        @Override
        protected boolean createKeys(List<SolutionParser.ProjectInfo> toPopulate) {
            try {
                FileObject solutionDir = solution.getProjectDirectory();
                SolutionParser.SolutionStructure structure = SolutionParser.parseSolution(solutionDir);

                // Get projects in this folder
                String folderGuid = folder.getFolderGuid().replace("{", "").replace("}", "");
                List<String> projectGuids = structure.getFolderProjects().get(folderGuid);

                if (projectGuids != null) {
                    for (String projectGuid : projectGuids) {
                        // Find the project with this GUID
                        for (SolutionParser.ProjectInfo project : structure.getProjects()) {
                            if (projectGuid.equals(project.getProjectGuid()) ||
                                projectGuid.equals(project.getProjectGuid().replace("{", "").replace("}", ""))) {
                                toPopulate.add(project);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Ignore errors for now
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(SolutionParser.ProjectInfo project) {
            return new SolutionProjectNode(project);
        }
    }
}