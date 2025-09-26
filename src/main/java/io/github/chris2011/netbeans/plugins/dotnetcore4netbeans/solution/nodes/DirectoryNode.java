package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
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

    @StaticResource
    public static final String FOLDER_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/folder.svg";

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

        private final FileObject directory;

        public DirectoryChildrenFactory(FileObject directory) {
            this.directory = directory;
        }

        @Override
        protected boolean createKeys(List<FileObject> toPopulate) {
            for (FileObject child : directory.getChildren()) {
                if (shouldShowFile(child)) {
                    toPopulate.add(child);
                }
            }
            return true;
        }

        private boolean shouldShowFile(FileObject file) {
            if (file.isFolder()) {
                // Show all folders except build/temp directories
                String name = file.getName();
                return !name.equals("bin") && !name.equals("obj") && !name.equals("target") &&
                       !name.equals("node_modules") && !name.startsWith(".");
            }

            // Show project files
            String ext = file.getExt();
            if ("csproj".equals(ext) || "fsproj".equals(ext) || "vbproj".equals(ext)) {
                return true;
            }

            // Show common important files
            String name = file.getName();
            return "README.md".equals(name) || "readme.md".equals(name) ||
                   "LICENSE".equals(name) || "license".equals(name) ||
                   ".gitignore".equals(name) || "app.config".equals(name) ||
                   "web.config".equals(name) || "appsettings.json".equals(name) ||
                   name.endsWith(".sln") || name.endsWith(".cs") ||
                   name.endsWith(".json") || name.endsWith(".xml");
        }

        @Override
        protected Node createNodeForKey(FileObject key) {
            if (key.isFolder()) {
                return new DirectoryNode(key);
            } else if ("csproj".equals(key.getExt()) || "fsproj".equals(key.getExt()) || "vbproj".equals(key.getExt())) {
                // Create a project info for this project file
                io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser.ProjectInfo projectInfo =
                    new io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser.ProjectInfo(
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
    }
}