package org.chrisle.netbeans.plugins.csharp4netbeans.subproject.nodes.ReferencesNode;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.chrisle.netbeans.plugins.csharp4netbeans.subproject.CSharpSubProject;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.NodeFactory;
import org.netbeans.spi.project.ui.support.NodeList;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
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
//        ReferencesNode p = project.getLookup().lookup(ReferencesNode.class);
        CSharpSubProject p = project.getLookup().lookup(CSharpSubProject.class);
        assert p != null;
        return new ReferencesNodeList(p);

        //Optionally, only return a new node
        //if some item is in the project's lookup:
        //MyCoolLookupItem item = project.getLookup().lookup(MyCoolLookupItem.class);
        //if (item != null) {
//        try {
//            ReferencesNode nd = new ReferencesNode(project);
//            return NodeFactorySupport.fixedNodeList(nd);
//        } catch (DataObjectNotFoundException ex) {
//            Exceptions.printStackTrace(ex);
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
        //}
        //If the above try/catch fails, e.g.,
        //our item isn't in the lookup,
        //then return an empty list of nodes:
//        return NodeFactorySupport.fixedNodeList();
    }

    private class ReferencesNodeList implements NodeList<Node> {

        CSharpSubProject project;

        public ReferencesNodeList(CSharpSubProject project) {
            this.project = project;
        }

        @Override
        public List<Node> keys() {
//            FileObject textsFolder = project.getProjectDirectory().getFileObject(project.getProjectDirectory().getName() + ".csproj");            
            List<Node> result = new ArrayList<>();
            
            result.add(new ReferencesNode(project));
//            if (textsFolder != null) {
//                for (FileObject textsFolderFile : textsFolder.getChildren()) {
//                    try {
//                        result.add(DataObject.find(textsFolderFile).getNodeDelegate());
//                    } catch (DataObjectNotFoundException ex) {
//                        Exceptions.printStackTrace(ex);
//                    }
//                }
//            }
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
