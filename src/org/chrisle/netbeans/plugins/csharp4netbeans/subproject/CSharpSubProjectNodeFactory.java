package org.chrisle.netbeans.plugins.csharp4netbeans.subproject;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author ChrisLE
 */
@NodeFactory.Registration(projectType = "org-csharp-subproject", position = 30)
public class CSharpSubProjectNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        CSharpSubProjectProvider subProjectProvider = project.getLookup().lookup(CSharpSubProjectProvider.class);
        assert subProjectProvider != null;
        return new SubProjectNodeList(subProjectProvider.getSubprojects());
    }

    private class SubProjectNodeList implements NodeList<Project> {

        Set<? extends Project> subprojects;

        public SubProjectNodeList(Set<? extends Project> subprojects) {
            this.subprojects = subprojects;
        }

        @Override
        public List<Project> keys() {
            List<Project> result = new ArrayList<>();
            subprojects.stream().forEach((oneReportSubProject) -> {
                result.add(oneReportSubProject);
            });
            return result;
        }

        @Override
        public Node node(Project node) {
            FilterNode fn = null;
//            try {
//                fn = new FilterNode(DataObject.find(node.getProjectDirectory()).getNodeDelegate()) {
//                    @Override
//                    public Image getIcon(int type) {
//                        return null;
//                    }
//
//                    @Override
//                    public Image getOpenedIcon(int type) {
//                        return null;
//                    }
//                };
//            } catch (DataObjectNotFoundException ex) {
//                Exceptions.printStackTrace(ex);
//            }
            return fn;
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