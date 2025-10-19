package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 * Node representing a directory that might contain projects.
 */
public class DirectoryNode extends AbstractNode {

    public static final String FOLDER_ICON = "org/netbeans/swing/plaf/resources/hidpi-folder-closed.png";

    private final FileObject directory;

    public DirectoryNode(FileObject directory) {
        super(Children.create(new DirectoryChildrenFactory(directory), true), Lookups.singleton(directory));
        this.directory = directory;
        setDisplayName(directory.getName());
        setShortDescription("Directory: " + directory.getPath());
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

    public FileObject getDirectory() {
        return directory;
    }

    /**
     * Children factory for directory contents
     */
    private static class DirectoryChildrenFactory extends ChildFactory<FileObject> {

        private static final Comparator<FileObject> FILE_OBJECT_COMPARATOR = new Comparator<FileObject>() {
            @Override
            public int compare(FileObject o1, FileObject o2) {
                return o1.getNameExt().compareToIgnoreCase(o2.getNameExt());
            }
        };
        private static final Set<String> SKIPPED_DIRECTORIES = new HashSet<>(Arrays.asList(
            "bin", "obj", "node_modules", "packages", "target", ".vs", ".git"
        ));
        private static final Set<String> SKIPPED_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "user", "cache"
        ));

        private final FileObject directory;

        public DirectoryChildrenFactory(FileObject directory) {
            this.directory = directory;
        }

        @Override
        protected boolean createKeys(List<FileObject> toPopulate) {
            List<FileObject> folders = new ArrayList<>();
            List<FileObject> files = new ArrayList<>();

            for (FileObject child : directory.getChildren()) {
                if (!shouldShowFile(child)) {
                    continue;
                }

                if (child.isFolder()) {
                    folders.add(child);
                } else {
                    files.add(child);
                }
            }

            Collections.sort(folders, FILE_OBJECT_COMPARATOR);
            Collections.sort(files, FILE_OBJECT_COMPARATOR);

            toPopulate.addAll(folders);
            toPopulate.addAll(files);

            return true;
        }

        private boolean shouldShowFile(FileObject file) {
            if (file.isFolder()) {
                // Show all folders except build/temp directories
                String name = file.getName();

                String normalized = name.toLowerCase();
                if (SKIPPED_DIRECTORIES.contains(normalized)) {
                    return false;
                }
                return !name.startsWith(".");
            }

            String ext = file.getExt();
            if (ext != null && SKIPPED_FILE_EXTENSIONS.contains(ext.toLowerCase())) {
                return false;
            }

            // Show common important files
            return true;
        }

        @Override
        protected Node createNodeForKey(FileObject key) {
            if (key.isFolder()) {
                return new DirectoryNode(key);
            } else if (isProjectFile(key)) {
                // Create a project info for this project file
                io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser.ProjectInfo projectInfo
                    = new io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser.ProjectInfo(
                        key.getName(),
                        key.getPath(),
                        ""
                    );
                projectInfo.setProjectFile(key);
                return new SolutionProjectNode(projectInfo);
            } else {
                // Regular file - create a simple file node
                try {
                    DataObject dataObject = DataObject.find(key);
                    return dataObject.getNodeDelegate();
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                    // Fallback to a simple file node
                    return new AbstractNode(Children.LEAF, Lookups.singleton(key)) {
                        {
                            setDisplayName(key.getName());
                            setShortDescription(key.getPath());
                        }
                    };
                }
            }
        }

        private boolean isProjectFile(FileObject file) {
            String ext = file.getExt();
            return ext != null && ext.toLowerCase().endsWith("proj");
        }
    }
}
