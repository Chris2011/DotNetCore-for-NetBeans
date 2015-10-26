package org.chrisle.netbeans.plugins.csharp4netbeans.solution;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.chrisle.netbeans.plugins.csharp4netbeans.subproject.CSharpSubProjectProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author chrl
 */
public class CSharpSolution implements Project {
    private final FileObject _projectDir;
    private final ProjectState _state;
    private Lookup lkp;

    CSharpSolution(FileObject dir, ProjectState state) {
        this._projectDir = dir;
        this._state = state;
    }

    @Override
    public FileObject getProjectDirectory() {
        return this._projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                this,
                new Info(),
                new CSharpSolutionLogicalView(this),
                new CSharpSubProjectProvider(this)
            });
        }

        return lkp;
    }

    private final class CSharpSolutionLogicalView implements LogicalViewProvider {
        @StaticResource()
        public static final String SOLUTION_ICON = "org/chrisle/netbeans/plugins/csharp4netbeans/resources/sln-file-nb.png";

        private final CSharpSolution _project;

        public CSharpSolutionLogicalView(CSharpSolution project) {
            this._project = project;
        }

        @Override
        public Node createLogicalView() {
            try {
                //Obtain the project directory's node:
                FileObject projectDirectory = _project.getProjectDirectory();
                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();

                //Decorate the project directory's node:
                return new ProjectNode(nodeOfProjectFolder, _project);
            } catch (DataObjectNotFoundException donfe) {
                Exceptions.printStackTrace(donfe);
                //Fallback-the directory couldn't be created -
                //read-only filesystem or something evil happened
                return new AbstractNode(Children.LEAF);
            }
        }

        private final class ProjectNode extends FilterNode {
            final CSharpSolution project;

            public ProjectNode(Node node, CSharpSolution project) throws DataObjectNotFoundException {
                super(node,
                        NodeFactorySupport.createCompositeChildren(project, "Projects/org-csharp-project/Nodes"),
                        new ProxyLookup(
                            new Lookup[]{
                                Lookups.singleton(project),
                                node.getLookup()
                            }));
                this.project = project;
            }

            @Override
            public Action[] getActions(boolean arg0) {
                return new Action[]{
                    CommonProjectActions.newFileAction(),
                    CommonProjectActions.copyProjectAction(),
                    CommonProjectActions.deleteProjectAction(),
                    CommonProjectActions.closeProjectAction()
                };
            }

            @Override
            public Image getIcon(int type) {
                return ImageUtilities.loadImage(SOLUTION_ICON);
            }

            @Override
            public Image getOpenedIcon(int type) {
                return getIcon(type);
            }

            @Override
            public String getDisplayName() {
                return "Solution '" + project.getProjectDirectory().getName() + "' (Number of projects)";
            }
        }

        @Override
        public Node findPath(Node root, Object target) {
            //leave unimplemented for now
            return null;
        }
    }

    /**
     * Sets icon for the project inside the project opening wizard.
     */
    private final class Info implements ProjectInformation {
        @StaticResource()
        public static final String SLN_ICON = "org/chrisle/netbeans/plugins/csharp4netbeans/resources/sln-file-folder.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(SLN_ICON));
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
            //do nothing, won't change
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public Project getProject() {
            return CSharpSolution.this;
        }
    }
}