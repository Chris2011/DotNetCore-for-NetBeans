package org.chrisle.netbeans.plugins.csharp4netbeans.solution;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.chrisle.netbeans.plugins.csharp4netbeans.subproject.CSharpSubProjectProvider;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author ChrisLE
 */
@NodeFactory.Registration(projectType = "org-csharp-project", position = 20)
public class CSharpSolutiontNodeFactory implements NodeFactory {

    @StaticResource()
    public static final String SUBPROJECT_ICON = "org/chrisle/netbeans/plugins/csharp4netbeans/resources/cs-project-nb.png";

    @Override
    public NodeList<?> createNodes(Project project) {
        CSharpSubProjectProvider subProjectProvider = project.getLookup().lookup(CSharpSubProjectProvider.class);
        assert subProjectProvider != null;
        return new CSharpSolutionNodeList(subProjectProvider.getSubprojects());
    }

    private class CSharpSolutionNodeList implements NodeList<Project> {

        Set<? extends Project> subprojects;

        public CSharpSolutionNodeList(Set<? extends Project> subprojects) {
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
            try {
                fn = new FilterNode(DataObject.find(node.getProjectDirectory()).getNodeDelegate()) {
                    @Override
                    public Image getIcon(int type) {
                        return ImageUtilities.loadImage(SUBPROJECT_ICON);
                    }

                    @Override
                    public Image getOpenedIcon(int type) {
                        return ImageUtilities.loadImage(SUBPROJECT_ICON);
                    }
                };
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
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