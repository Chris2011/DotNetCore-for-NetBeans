package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.CSharpSolution;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser;
import java.awt.Image;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Visual Studio-like Solution root node.
 */
public class SolutionNode extends AbstractNode {

    @StaticResource
    public static final String SOLUTION_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/sln-file.svg";

    private final CSharpSolution solution;
    private final String solutionName;

    public SolutionNode(CSharpSolution solution, String solutionName) {
        super(SolutionProjectChildren.create(solution), Lookups.singleton(solution));
        this.solution = solution;
        this.solutionName = solutionName;

        // Count projects for display
        int projectCount = getProjectCount();
        setDisplayName("Solution '" + solutionName + "' (" + projectCount + " projects)");
        setShortDescription("C# Solution: " + solutionName + " (" + projectCount + " projects)");
    }

    private int getProjectCount() {
        try {
            FileObject solutionDir = solution.getProjectDirectory();
            SolutionParser.SolutionStructure structure = SolutionParser.parseSolution(solutionDir);
            return structure.getProjects().size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(SOLUTION_ICON);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            CommonProjectActions.newFileAction(),
            null, // Separator
            CommonProjectActions.copyProjectAction(),
            CommonProjectActions.deleteProjectAction(),
            null, // Separator
            CommonProjectActions.closeProjectAction()
        };
    }

    @Override
    public boolean canRename() {
        return false;
    }

    @Override
    public boolean canDestroy() {
        return false;
    }

    public CSharpSolution getSolution() {
        return solution;
    }

    public String getSolutionName() {
        return solutionName;
    }
}