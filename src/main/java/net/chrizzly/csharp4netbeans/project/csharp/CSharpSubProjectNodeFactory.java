package net.chrizzly.csharp4netbeans.project.csharp;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.SubprojectProvider;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author ChrisLE
 */
@NodeFactory.Registration(projectType = "org-csharp-project", position = 20)
public class CSharpSubProjectNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        return new CSharpSubProjectNodeList(project);
    }

    private class CSharpSubProjectNodeList implements NodeList<Project> {
        Project subproject;

        public CSharpSubProjectNodeList(Project project) {
            this.subproject = project;
        }

        @Override
        public List<Project> keys() {
            return new ArrayList<> (subproject.getLookup().lookup(SubprojectProvider.class).getSubprojects());
        }

//        @StaticResource
//        private static final String IMAGE = "net/chrizzly/csharp4netbeans/cs-project-nb.png";

        @Override
        public Node node(final Project project) {
            Node node = project.getLookup().lookup(LogicalViewProvider.class).createLogicalView();
            
            return new FilterNode(node, new FilterNode.Children(node));
            
//            FilterNode fn = null;
//            try {
//                fn = new FilterNode(DataObject.find(project.getProjectDirectory()).getNodeDelegate()) {
//                    @Override
//                    public Image getIcon(int type) {
//                        return ImageUtilities.loadImage(IMAGE);
//                    }
//
//                    @Override
//                    public Image getOpenedIcon(int type) {
//                        return ImageUtilities.loadImage(IMAGE);
//                    }
//                };
//            } catch (DataObjectNotFoundException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//            return fn;
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }
    }
}
