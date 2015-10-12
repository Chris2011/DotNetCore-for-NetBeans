package org.chrisle.netbeans.plugins.csharp4netbeans.subproject;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeListener;
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
@NodeFactory.Registration(projectType = "org-csharp-subproject", position = 30)
public class CSharpSubProjectNodeFactory implements NodeFactory {

    @Override
    public NodeList<?> createNodes(Project project) {
        try {
            ReferencesNode rn = new ReferencesNode(project);
            return NodeFactorySupport.fixedNodeList(rn);
//        CSharpSubProjectProvider subProjectProvider = project.getLookup().lookup(CSharpSubProjectProvider.class);
//        assert subProjectProvider != null;
//        return new SubProjectNodeList(subProjectProvider.getSubprojects());
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }

        return NodeFactorySupport.fixedNodeList();
    }

//    private class SubProjectNodeList implements NodeList<Project> {
//
//        Set<? extends Project> subprojects;
//
//        public SubProjectNodeList(Set<? extends Project> subprojects) {
//            this.subprojects = subprojects;
//        }
//
//        @Override
//        public List<Project> keys() {
//            List<Project> result = new ArrayList<>();
//            subprojects.stream().forEach((oneReportSubProject) -> {
//                result.add(oneReportSubProject);
//            });
//            return result;
//        }
//
//        @StaticResource
//        private static final String IMAGE = "org/chrisle/netbeans/plugins/csharp4netbeans/resources/references.png";
//
//        @Override
//        public Node node(Project node) {
//            return null;
////            FilterNode fn = null;
////            try {
////                fn = new FilterNode(DataObject.find(node.getProjectDirectory()).getNodeDelegate()) {
////                    @Override
////                    public Image getIcon(int type) {
////                        return ImageUtilities.loadImage(IMAGE);
////                    }
////
////                    @Override
////                    public Image getOpenedIcon(int type) {
////                        return ImageUtilities.loadImage(IMAGE);
////                    }
////                };
////            } catch (DataObjectNotFoundException ex) {
////                Exceptions.printStackTrace(ex);
////            }
////            return fn;
////            ReferencesNode nd = null;
////
////            try {
////                nd = new ReferencesNode(node);
////
////                return nd;
////            } catch (DataObjectNotFoundException ex) {
////                Exceptions.printStackTrace(ex);
////            }
//
////            return nd;
////            FilterNode fn = null;
////            try {
////                fn = new FilterNode(DataObject.find(node.getProjectDirectory()).getNodeDelegate()) {
////                    @Override
////                    public Image getIcon(int type) {
////                        DataFolder root = DataFolder.findFolder(FileUtil.getConfigRoot());
////                        Image original = root.getNodeDelegate().getIcon(type);
////
////                        return ImageUtilities.mergeImages(original, ImageUtilities.loadImage(IMAGE), 7, 7);
////                    }
////
////                    @Override
////                    public Image getOpenedIcon(int type) {
////                        return null;
////                    }
////                };
////            } catch (DataObjectNotFoundException ex) {
////                Exceptions.printStackTrace(ex);
////            }
////            return fn;
//        }
//
//        @Override
//        public void addNotify() {
//        }
//
//        @Override
//        public void removeNotify() {
//        }
//
//        @Override
//        public void addChangeListener(ChangeListener cl) {
//        }
//
//        @Override
//        public void removeChangeListener(ChangeListener cl) {
//        }
//    }
}