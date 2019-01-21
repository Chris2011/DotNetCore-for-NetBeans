package net.chrizzly.csharp4netbeans.project.csharp.nodes.PropertiesNode;

import java.awt.Image;
import java.io.IOException;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.util.ImageUtilities;

/**
 *
 * @author chrl
 */
public class PropertiesNode extends FilterNode {
    @StaticResource
    private static final String IMAGE = "net/chrizzly/csharp4netbeans/properties.png";

    public PropertiesNode(Project proj) throws DataObjectNotFoundException, IOException {
        super(DataObject.find(proj.getProjectDirectory().getFileObject("Properties") != null ? proj.getProjectDirectory().getFileObject("Properties") : proj.getProjectDirectory().getCanonicalFileObject()).getNodeDelegate());
    }

    @Override
    public String getDisplayName() {
        return "Properties";
    }

    //Next, we add icons, for the default state, which is
    //closed, and the opened state; we will make them the same.
    //
    //Icons in project logical views are
    //based on combinations--you can combine the node's own icon
    //with a distinguishing badge that is merged with it. Here we
    //first obtain the icon from a data folder, then we add our
    //badge to it by merging it via a NetBeans API utility method:
    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(IMAGE);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return ImageUtilities.loadImage(IMAGE);
    }
}