package org.chrisle.netbeans.plugins.csharp4netbeans.subproject.nodes.ReferencesNode;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.chrisle.netbeans.plugins.csharp4netbeans.subproject.CSharpSubProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author chrl
 */
@NodeFactory.Registration(projectType = "org-csharp-subproject")
public class ReferencesNodeFactory implements NodeFactory {

    @Override
    public NodeList createNodes(Project project) {
        CSharpSubProject p = project.getLookup().lookup(CSharpSubProject.class);
        assert p != null;
        return new ReferencesNodeList(p);
    }

    private class ReferencesNodeList implements NodeList<Node> {

        CSharpSubProject project;

        public ReferencesNodeList(CSharpSubProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
            List<Node> result = new ArrayList<>();
            
            result.add(new ReferencesNode(project));
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
