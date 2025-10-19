package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.CSharpSolution;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
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

    public static final String FOLDER_ICON = "org/netbeans/swing/plaf/resources/hidpi-folder-closed.png";

    private final SolutionParser.SolutionFolder folder;

    public SolutionFolderNode(SolutionParser.SolutionFolder folder, CSharpSolution solution) {
        super(Children.create(new SolutionFolderChildrenFactory(folder, solution), true), Lookups.fixed(folder, solution));
        this.folder = folder;
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
     * Children factory for solution folder contents (projects within the
     * folder)
     */
    private static class SolutionFolderChildrenFactory extends ChildFactory<Object> {

        private final SolutionParser.SolutionFolder folder;
        private final CSharpSolution solution;

        public SolutionFolderChildrenFactory(SolutionParser.SolutionFolder folder, CSharpSolution solution) {
            this.folder = folder;
            this.solution = solution;
        }

        @Override
        protected boolean createKeys(List<Object> toPopulate) {
            try {
                FileObject solutionDir = solution.getProjectDirectory();
                SolutionParser.SolutionStructure structure = SolutionParser.parseSolution(solutionDir);

                // Get projects in this folder
                String folderGuid = SolutionParser.cleanGuid(folder.getFolderGuid());

                List<SolutionParser.SolutionFolder> childFolders = new ArrayList<>();
                List<SolutionParser.ProjectInfo> childProjects = new ArrayList<>();
                List<FileObject> files = new ArrayList<>();

                List<String> childFolderGuids = structure.getFolderChildren().get(folderGuid);
                if (childFolderGuids != null) {
                    for (String childGuid : childFolderGuids) {
                        SolutionParser.SolutionFolder childFolder = structure.getFolderByGuid().get(childGuid);
                        if (childFolder != null) {
                            childFolders.add(childFolder);
                        }
                    }
                }

                List<String> projectGuids = structure.getFolderProjects().get(folderGuid);

                if (projectGuids != null) {
                    for (String projectGuid : projectGuids) {
                        SolutionParser.ProjectInfo project = structure.getProjectByGuid().get(projectGuid);
                        if (project != null) {
                            childProjects.add(project);
                        }
                    }
                }

                // Add solution items (files) from this folder
                for (String itemPath : folder.getItems()) {
                    try {
                        String normalizedPath = itemPath.replace('\\', '/');
                        FileObject itemFile = solutionDir.getFileObject(normalizedPath);
                        if (itemFile != null && itemFile.isValid()) {
                            files.add(itemFile);
                        }
                    } catch (Exception e) {
                        // Ignore individual file errors
                    }
                }

                childFolders.sort(Comparator.comparing(SolutionParser.SolutionFolder::getName, String.CASE_INSENSITIVE_ORDER));
                childProjects.sort(Comparator.comparing(SolutionParser.ProjectInfo::getName, String.CASE_INSENSITIVE_ORDER));
                files.sort(Comparator.comparing(FileObject::getNameExt, String.CASE_INSENSITIVE_ORDER));

                toPopulate.addAll(childFolders);
                toPopulate.addAll(childProjects);
                toPopulate.addAll(files);
            } catch (Exception e) {
                // Ignore errors for now
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(Object key) {
            if (key instanceof SolutionParser.ProjectInfo) {
                return new SolutionProjectNode((SolutionParser.ProjectInfo) key);
            } else if (key instanceof SolutionParser.SolutionFolder) {
                return new SolutionFolderNode((SolutionParser.SolutionFolder) key, solution);
            } else if (key instanceof FileObject) {
                try {
                    FileObject fo = (FileObject) key;
                    org.openide.loaders.DataObject dataObject = org.openide.loaders.DataObject.find(fo);
                    return dataObject.getNodeDelegate().cloneNode();
                } catch (Exception e) {
                    // If something goes wrong, return null
                }
            }
            return null;
        }
    }
}
