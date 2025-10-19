package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.csharp.nodes.DependenciesNode;

import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 *
 * @author chrl
 */
public class DependenciesNode extends AbstractNode {

    @StaticResource
    private static final String IMAGE = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/dependencies.svg";

    public DependenciesNode(Project project) {
        super(Children.create(new DependenciesChildFactory(project), true));
        setDisplayName("Dependencies");
        setIconBaseWithExtension(IMAGE);
    }
}
