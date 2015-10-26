package org.chrisle.netbeans.plugins.csharp4netbeans.subproject;

import org.chrisle.netbeans.plugins.csharp4netbeans.subproject.nodes.ReferencesNode;
import java.awt.Image;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
import org.chrisle.netbeans.plugins.csharp4netbeans.subproject.nodes.PropertiesNode;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataFolder;
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
public class CSharpSubProjectNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        CSharpSubProjectProvider rsp = project.getLookup().lookup(CSharpSubProjectProvider.class);
        assert rsp != null;
        return new CSharpSubProjectNodeList(rsp.getSubprojects());
    }

//    @Override
//    public NodeList<?> createNodes(Project project) {
//        try {
//            ReferencesNode rn = new ReferencesNode(project);
////            PropertiesNode pn = new PropertiesNode(project);
//
//            return NodeFactorySupport.fixedNodeList(rn);
////        CSharpSubProjectProvider subProjectProvider = project.getLookup().lookup(CSharpSubProjectProvider.class);
////        assert subProjectProvider != null;
////        return new SubProjectNodeList(subProjectProvider.getSubprojects());
//        } catch (DataObjectNotFoundException ex) {
//            Exceptions.printStackTrace(ex);
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//
//        return NodeFactorySupport.fixedNodeList();
//    }
    private class CSharpSubProjectNodeList implements NodeList<Project> {

        Set<? extends Project> subprojects;

        public CSharpSubProjectNodeList(Set<? extends Project> subprojects) {
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

        @StaticResource
        private static final String IMAGE = "org/chrisle/netbeans/plugins/csharp4netbeans/resources/cs-project-nb.png";

        @Override
        public Node node(Project node) {
            FilterNode fn = null;
            try {
                fn = new FilterNode(DataObject.find(node.getProjectDirectory()).getNodeDelegate()) {
                    @Override
                    public Image getIcon(int type) {
                        return ImageUtilities.loadImage(IMAGE);
                    }

                    @Override
                    public Image getOpenedIcon(int type) {
                        return ImageUtilities.loadImage(IMAGE);
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
