package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * Abstract base class for .NET sub-projects
 *
 * @author ChrisLE
 */
public abstract class AbstractDotNetSubProject implements Project {
    private final FileObject _projectDir;
    private final ProjectState _state;
    private final String _projectName;
    private Lookup lkp;

    protected AbstractDotNetSubProject(FileObject dir, ProjectState state, String projectName) {
        this._projectDir = dir;
        this._state = state;
        this._projectName = projectName;
    }

    @Override
    public FileObject getProjectDirectory() {
        return this._projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(createLookupContent());
        }
        return lkp;
    }

    protected Object[] createLookupContent() {
        return new Object[]{
            this,
            new Info(),
            new DotNetSubProjectLogicalView(this)
        };
    }

    protected String getProjectName() {
        return _projectName;
    }

    protected ProjectState getState() {
        return _state;
    }

    /**
     * Get the icon resource path for this project type
     * @return The resource path
     */
    protected abstract String getProjectIconPath();

    /**
     * Get the small icon resource path for project information
     * @return The resource path
     */
    protected abstract String getProjectSmallIconPath();

    /**
     * Get the node factory path for logical view
     * @return The path for NodeFactorySupport
     */
    protected abstract String getNodeFactoryPath();

    /**
     * Get additional actions for the project node
     * @return Array of additional actions, or empty array
     */
    protected Action[] getAdditionalActions() {
        return new Action[0];
    }

    private final class DotNetSubProjectLogicalView implements LogicalViewProvider {
        private final AbstractDotNetSubProject _project;

        public DotNetSubProjectLogicalView(AbstractDotNetSubProject project) {
            this._project = project;
        }

        @Override
        public Node createLogicalView() {
            try {
                FileObject projectDirectory = _project.getProjectDirectory();
                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
                return new ProjectNode(nodeOfProjectFolder, _project);
            } catch (DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
                return new AbstractNode(Children.LEAF);
            }
        }

        private final class ProjectNode extends FilterNode {
            final AbstractDotNetSubProject project;

            public ProjectNode(Node node, AbstractDotNetSubProject project) throws DataObjectNotFoundException {
                super(node,
                        org.openide.nodes.Children.create(new ProjectFilesChildFactory(node, project), true),
                        new ProxyLookup(
                                new Lookup[]{
                                    Lookups.singleton(project),
                                    node.getLookup()
                                }));
                this.project = project;
            }

            @Override
            public Action[] getActions(boolean arg0) {
                Action[] baseActions = new Action[]{
                    CommonProjectActions.newFileAction(),
                    CommonProjectActions.copyProjectAction(),
                    CommonProjectActions.deleteProjectAction(),
                    CommonProjectActions.closeProjectAction()
                };

                Action[] additionalActions = project.getAdditionalActions();
                if (additionalActions.length == 0) {
                    return baseActions;
                }

                Action[] allActions = new Action[baseActions.length + additionalActions.length];
                System.arraycopy(baseActions, 0, allActions, 0, baseActions.length);
                System.arraycopy(additionalActions, 0, allActions, baseActions.length, additionalActions.length);
                return allActions;
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(project.getProjectIconPath());
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return _projectName;
            }
        }

        @Override
        public Node findPath(Node root, Object target) {
            return null;
        }
    }

    private final class Info implements ProjectInformation {
        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(getProjectSmallIconPath()));
        }

        @Override
        public String getName() {
            return getProjectDirectory().getName();
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
        }

        @Override
        public Project getProject() {
            return AbstractDotNetSubProject.this;
        }
    }

    /**
     * ChildFactory that combines project files/folders with custom nodes (like Dependencies)
     * Sorted like Visual Studio: custom nodes first, then folders, then files
     */
    private static class ProjectFilesChildFactory extends ChildFactory<Object> {
        private final Node originalNode;
        private final AbstractDotNetSubProject project;

        public ProjectFilesChildFactory(Node originalNode, AbstractDotNetSubProject project) {
            this.originalNode = originalNode;
            this.project = project;
        }

        @Override
        protected boolean createKeys(List<Object> toPopulate) {
            // 1. Add custom nodes from NodeFactories (Dependencies, Properties, etc.) - FIRST
            Children customChildren = NodeFactorySupport.createCompositeChildren(project, project.getNodeFactoryPath());
            Node[] customNodes = customChildren.getNodes(true);
            toPopulate.addAll(Arrays.asList(customNodes));

            // 2. Separate folders and files from original children
            List<Node> folders = new ArrayList<>();
            List<Node> files = new ArrayList<>();

            Node[] originalChildren = originalNode.getChildren().getNodes(true);
            for (Node child : originalChildren) {
                FileObject fo = child.getLookup().lookup(FileObject.class);
                if (fo != null && fo.isFolder()) {
                    folders.add(child);
                } else {
                    files.add(child);
                }
            }

            // 3. Add folders (sorted) - SECOND
            toPopulate.addAll(folders);

            // 4. Add files (sorted) - LAST
            toPopulate.addAll(files);

            return true;
        }

        @Override
        protected Node createNodeForKey(Object key) {
            if (key instanceof Node) {
                Node node = (Node) key;
                try {
                    // Clone the node to avoid "node already belongs to another parent" error
                    return node.cloneNode();
                } catch (Exception ex) {
                    // If cloning fails, wrap in FilterNode
                    return new FilterNode(node);
                }
            }
            return null;
        }
    }
}
