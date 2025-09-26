package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.ProjectDependencyParser;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 * Visual Studio-like Project node within a Solution.
 */
public class SolutionProjectNode extends AbstractNode {

    @StaticResource
    public static final String CSPROJ_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/cs-project-nb.png";

    private final SolutionParser.ProjectInfo projectInfo;

    public SolutionProjectNode(SolutionParser.ProjectInfo projectInfo) {
        super(Children.create(new ProjectChildrenFactory(projectInfo), true), Lookups.singleton(projectInfo));
        this.projectInfo = projectInfo;
        setDisplayName(projectInfo.getName());
        setShortDescription("C# Project: " + projectInfo.getName());
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(CSPROJ_ICON);
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

    public SolutionParser.ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    /**
     * Factory for creating child nodes of a project (Dependencies, Source Files, etc.)
     */
    private static class ProjectChildrenFactory extends ChildFactory<String> {

        private final SolutionParser.ProjectInfo projectInfo;

        public ProjectChildrenFactory(SolutionParser.ProjectInfo projectInfo) {
            this.projectInfo = projectInfo;
        }

        @Override
        protected boolean createKeys(List<String> toPopulate) {
            // Add standard Visual Studio project structure
            toPopulate.add("Dependencies");
            toPopulate.add("Source Files");
            return true;
        }

        @Override
        protected Node createNodeForKey(String key) {
            switch (key) {
                case "Dependencies":
                    return new DependenciesNode(projectInfo);
                case "Source Files":
                    return new SourceFilesNode(projectInfo);
                default:
                    return null;
            }
        }
    }

    /**
     * Dependencies node for NuGet packages and references
     */
    private static class DependenciesNode extends AbstractNode {

        @StaticResource
        public static final String DEPENDENCIES_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/references.png";

        private final SolutionParser.ProjectInfo projectInfo;

        public DependenciesNode(SolutionParser.ProjectInfo projectInfo) {
            super(Children.create(new DependenciesChildrenFactory(projectInfo), true));
            this.projectInfo = projectInfo;
            setDisplayName("Dependencies");
            setShortDescription("Project Dependencies and NuGet Packages");
        }

        @Override
        public Image getIcon(int type) {
            // Fallback to a generic folder icon if dependencies icon doesn't exist
            return ImageUtilities.loadImage(DEPENDENCIES_ICON, true);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public boolean canRename() {
            return false;
        }

        @Override
        public boolean canDestroy() {
            return false;
        }
    }

    /**
     * Factory for creating dependency nodes
     */
    private static class DependenciesChildrenFactory extends ChildFactory<Object> {

        private final SolutionParser.ProjectInfo projectInfo;

        public DependenciesChildrenFactory(SolutionParser.ProjectInfo projectInfo) {
            this.projectInfo = projectInfo;
        }

        @Override
        protected boolean createKeys(List<Object> toPopulate) {
            FileObject projectFile = projectInfo.getProjectFile();
            if (projectFile != null) {
                ProjectDependencyParser.ProjectDependencies dependencies =
                    ProjectDependencyParser.parseDependencies(projectFile);

                // Add framework references
                for (ProjectDependencyParser.FrameworkReference frameworkRef : dependencies.getFrameworkReferences()) {
                    toPopulate.add(frameworkRef);
                }

                // Add package references
                for (ProjectDependencyParser.PackageReference packageRef : dependencies.getPackageReferences()) {
                    toPopulate.add(packageRef);
                }

                // Add project references
                for (ProjectDependencyParser.ProjectReference projectRef : dependencies.getProjectReferences()) {
                    toPopulate.add(projectRef);
                }
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(Object key) {
            if (key instanceof ProjectDependencyParser.FrameworkReference) {
                return new DependencyNode((ProjectDependencyParser.FrameworkReference) key);
            } else if (key instanceof ProjectDependencyParser.PackageReference) {
                return new DependencyNode((ProjectDependencyParser.PackageReference) key);
            } else if (key instanceof ProjectDependencyParser.ProjectReference) {
                return new DependencyNode((ProjectDependencyParser.ProjectReference) key);
            }
            return null;
        }
    }

    /**
     * Node for individual dependencies
     */
    private static class DependencyNode extends AbstractNode {

        @StaticResource
        public static final String PACKAGE_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/package.svg";
        @StaticResource
        public static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/cs-project-nb.png";
        @StaticResource
        public static final String FRAMEWORK_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/framework.svg";

        private final Object dependency;

        public DependencyNode(ProjectDependencyParser.PackageReference packageRef) {
            super(Children.LEAF, Lookups.singleton(packageRef));
            this.dependency = packageRef;
            setDisplayName(packageRef.toString());
            setShortDescription("NuGet Package: " + packageRef.getName());
        }

        public DependencyNode(ProjectDependencyParser.ProjectReference projectRef) {
            super(Children.LEAF, Lookups.singleton(projectRef));
            this.dependency = projectRef;
            setDisplayName(projectRef.getName());
            setShortDescription("Project Reference: " + projectRef.getPath());
        }

        public DependencyNode(ProjectDependencyParser.FrameworkReference frameworkRef) {
            super(Children.LEAF, Lookups.singleton(frameworkRef));
            this.dependency = frameworkRef;
            setDisplayName(frameworkRef.getName());
            setShortDescription("Framework Reference: " + frameworkRef.getName());
        }

        @Override
        public Image getIcon(int type) {
            if (dependency instanceof ProjectDependencyParser.PackageReference) {
                return ImageUtilities.loadImage(PACKAGE_ICON, true);
            } else if (dependency instanceof ProjectDependencyParser.ProjectReference) {
                return ImageUtilities.loadImage(PROJECT_ICON, true);
            } else if (dependency instanceof ProjectDependencyParser.FrameworkReference) {
                return ImageUtilities.loadImage(FRAMEWORK_ICON, true);
            }
            return super.getIcon(type);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public boolean canRename() {
            return false;
        }

        @Override
        public boolean canDestroy() {
            return false;
        }
    }

    /**
     * Source Files node for .cs files
     */
    private static class SourceFilesNode extends AbstractNode {

        @StaticResource
        public static final String SOURCE_FILES_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/cs-project-folder.png";

        private final SolutionParser.ProjectInfo projectInfo;

        public SourceFilesNode(SolutionParser.ProjectInfo projectInfo) {
            super(Children.create(new SourceFilesChildrenFactory(projectInfo), true));
            this.projectInfo = projectInfo;
            setDisplayName("Source Files");
            setShortDescription("C# Source Files");
        }

        @Override
        public Image getIcon(int type) {
            // Fallback to a generic folder icon if source files icon doesn't exist
            return ImageUtilities.loadImage(SOURCE_FILES_ICON, true);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public boolean canRename() {
            return false;
        }

        @Override
        public boolean canDestroy() {
            return false;
        }
    }

    /**
     * Factory for creating source file nodes
     */
    private static class SourceFilesChildrenFactory extends ChildFactory<FileObject> {

        private final SolutionParser.ProjectInfo projectInfo;

        public SourceFilesChildrenFactory(SolutionParser.ProjectInfo projectInfo) {
            this.projectInfo = projectInfo;
        }

        @Override
        protected boolean createKeys(List<FileObject> toPopulate) {
            FileObject projectFile = projectInfo.getProjectFile();
            if (projectFile != null) {
                FileObject projectDir = projectFile.getParent();
                findCsFiles(projectDir, toPopulate);
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(FileObject fileObject) {
            try {
                DataObject dataObject = DataObject.find(fileObject);
                return dataObject.getNodeDelegate();
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
                return null;
            }
        }

        private void findCsFiles(FileObject dir, List<FileObject> csFiles) {
            if (dir != null && dir.isFolder()) {
                for (FileObject child : dir.getChildren()) {
                    if (child.isFolder() && !child.getName().startsWith(".") &&
                        !child.getName().equals("bin") && !child.getName().equals("obj")) {
                        // Recursively search subdirectories, skip build directories
                        findCsFiles(child, csFiles);
                    } else if ("cs".equals(child.getExt())) {
                        csFiles.add(child);
                    }
                }
            }
        }
    }
}