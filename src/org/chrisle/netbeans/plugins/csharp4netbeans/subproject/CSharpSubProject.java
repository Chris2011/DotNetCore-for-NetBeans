package org.chrisle.netbeans.plugins.csharp4netbeans.subproject;

import java.awt.Image;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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
 * @author ChrisLE
 */
public class CSharpSubProject implements Project {
    private final FileObject _projectDir;
    private final ProjectState _state;
    private Lookup lkp;

    CSharpSubProject(FileObject dir, ProjectState state) {
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
                // register your features here
                this,
                new Info(),
                new CSharpSubProjectLogicalView(this)
            });
        }

        return lkp;
    }

    private final class CSharpSubProjectLogicalView implements LogicalViewProvider {
        @StaticResource()
        public static final String PROJECT_ICON = "org/chrisle/netbeans/plugins/csharp4netbeans/resources/references.png";

        private final CSharpSubProject _project;

        public CSharpSubProjectLogicalView(CSharpSubProject project) {
            this._project = project;
        }

        @Override
        public Node createLogicalView() {
//            try {
//                return new ReferencesNode(_project);
//            try {
//                //Obtain the project directory's node:
//                FileObject projectDirectory = _project.getProjectDirectory();
//                DataFolder projectFolder = DataFolder.findFolder(projectDirectory);
//                Node nodeOfProjectFolder = projectFolder.getNodeDelegate();
//
//                //Decorate the project directory's node:
//                return new ProjectNode(nodeOfProjectFolder, _project);
//            } catch (DataObjectNotFoundException donfe) {
//                Exceptions.printStackTrace(donfe);
//                //Fallback-the directory couldn't be created -
//                //read-only filesystem or something evil happened
//                return new AbstractNode(Children.LEAF);
//            }
//            } catch (DataObjectNotFoundException ex) {
//                Exceptions.printStackTrace(ex);
//            }

            return null;
        }
//
//        private final class ProjectNode extends FilterNode {
//            final CSharpSubProject project;
//
//            public ProjectNode(Node node, CSharpSubProject project) throws DataObjectNotFoundException {
//                super(node,
////                        null,
//                        NodeFactorySupport.createCompositeChildren(project, "Projects/org-csharp-subproject/Nodes"),
////                        new FilterNode.Children(node), // Change back to the original
//                        new ProxyLookup(
//                                new Lookup[]{
//                                    Lookups.singleton(project),
//                                    node.getLookup()
//                                }));
//                this.project = project;
//            }
////
//            @Override
//            public Action[] getActions(boolean arg0) {
//                return new Action[]{
//                    CommonProjectActions.newFileAction(),
//                    CommonProjectActions.copyProjectAction(),
//                    CommonProjectActions.deleteProjectAction(),
//                    CommonProjectActions.closeProjectAction()
//                };
//            }
//
//            @Override
//            public Image getIcon(int type) {
//                return ImageUtilities.loadImage(PROJECT_ICON);
//            }
//
//            @Override
//            public Image getOpenedIcon(int type) {
//                return getIcon(type);
//            }
//
//            @Override
//            public String getDisplayName() {
//                return project.getProjectDirectory().getName();
//            }
//        }

        @Override
        public Node findPath(Node root, Object target) {
            //leave unimplemented for now
            return null;
        }
    }

    private final class Info implements ProjectInformation {
        @StaticResource()
        public static final String CSHARP_ICON = "org/chrisle/netbeans/plugins/csharp4netbeans/resources/cs-project-folder.png";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(CSHARP_ICON));
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
            return CSharpSubProject.this;
        }
    }
}