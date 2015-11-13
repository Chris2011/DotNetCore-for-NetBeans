package org.chrisle.netbeans.plugins.csharp4netbeans.subproject.nodes.ProjectFilesNode;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author chrl
 */
@NodeFactory.Registration(projectType = "org-csharp-subproject")
public class ProjectFilesNodeFactory implements NodeFactory {

    @Override
    public NodeList createNodes(Project project) {
        return new ProjectFilesNodeList(project);
    }

    private class ProjectFilesNodeList implements NodeList<Node> {

        private final Project _project;

        public ProjectFilesNodeList(Project project) {
            this._project = project;
        }

        @Override
        public List<Node> keys() {
            List<Node> nodes = new ArrayList<>();

            for (FileObject fileObject :  this._project.getProjectDirectory().getChildren()) {
                try {
                    if (!fileObject.getExt().equals("csproj")) {
                        nodes.add(DataObject.find(fileObject).getNodeDelegate());
                    }
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            return nodes;
        }

        @Override
        public void addChangeListener(ChangeListener cl) {
        }

        @Override
        public void removeChangeListener(ChangeListener cl) {
        }

        @Override
        public Node node(Node node) {
            return new FilterNode(node);
        }

        @Override
        public void addNotify() {
        }

        @Override
        public void removeNotify() {
        }
    }
}
