package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.csharp.CSharpSubProjectProvider;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes.SolutionNode;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectInformation;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.LogicalViewProvider;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.NodeFactorySupport;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author chrl
 */
public class CSharpSolution implements Project {
    private final FileObject _projectDir;
    private final ProjectState _state;
    private final String _slnName;
    private Lookup lkp;

    CSharpSolution(FileObject dir, ProjectState state, String slnName) {
        this._projectDir = dir;
        this._state = state;
        this._slnName = slnName;
    }

    @Override
    public FileObject getProjectDirectory() {
        return this._projectDir;
    }

    @Override
    public Lookup getLookup() {
        if (lkp == null) {
            lkp = Lookups.fixed(new Object[]{
                this,
                new Info(),
                new CSharpSolutionLogicalView(this),
                new CSharpSubProjectProvider(this)
            });
        }

        return lkp;
    }

    private final class CSharpSolutionLogicalView implements LogicalViewProvider {
        @StaticResource()
        public static final String SOLUTION_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/sln-file.svg";

        private final CSharpSolution _project;

        public CSharpSolutionLogicalView(CSharpSolution project) {
            this._project = project;
        }

        @Override
        public Node createLogicalView() {
            // Create a Visual Studio-like solution tree structure
            return new SolutionNode(_project, _project._slnName);
        }

        // Visual Studio-like solution tree is now handled by SolutionNode

        @Override
        public Node findPath(Node root, Object target) {
            //leave unimplemented for now
            return null;
        }
    }

    /**
     * Sets icon for the project inside the project opening wizard.
     */
    private final class Info implements ProjectInformation {
        @StaticResource()
        public static final String SLN_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/sln-file.svg";

        @Override
        public Icon getIcon() {
            return new ImageIcon(ImageUtilities.loadImage(SLN_ICON));
        }

        @Override
        public String getName() {
            return _slnName;
        }

        @Override
        public String getDisplayName() {
            return getName();
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener pcl) {
            //do nothing, won't change
        }

        @Override
        public Project getProject() {
            return CSharpSolution.this;
        }
    }
}