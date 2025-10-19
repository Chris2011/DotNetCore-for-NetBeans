package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.csharp.nodes.DependenciesNode;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 * NodeFactory for Dependencies node - works with all .NET project types
 * Registered via layer.xml for org-csharp-subproject, org-vb-subproject,
 * org-fsharp-subproject, and org-vcpp-subproject
 *
 * @author chrl
 */
public class DependenciesNodeFactory implements NodeFactory {

    @Override
    public NodeList createNodes(Project project) {
        return new ReferencesNodeList(project);
    }

    private class ReferencesNodeList implements NodeList<Node> {

        Project project;

        public ReferencesNodeList(Project project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            List<Node> result = new ArrayList<>();

            result.add(new DependenciesNode(project));
            return result;
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
