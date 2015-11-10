package org.chrisle.netbeans.plugins.csharp4netbeans.subproject.nodes.ReferencesNode;

import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author chrl
 */
public class ReferencesNode extends AbstractNode {

    @StaticResource
    private static final String IMAGE = "org/chrisle/netbeans/plugins/csharp4netbeans/resources/references.png";

    public ReferencesNode(Project project) {
        super(Children.create(new ReferencesChildFactory(project), true));
        setDisplayName("References");
        setIconBaseWithExtension(IMAGE);
    }
}
