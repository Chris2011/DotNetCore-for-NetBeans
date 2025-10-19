package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.csharp.nodes.DependenciesNode;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.ProjectDependencyParser;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.openide.filesystems.FileObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;

/**
 * Factory for creating dependency category nodes (Frameworks, Packages, Projects)
 *
 * @author chrl
 */
class DependenciesChildFactory extends ChildFactory<Object> {

    private final Project project;

    public DependenciesChildFactory(Project project) {
        this.project = project;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        FileObject projectFile = findProjectFile();
        if (projectFile == null) {
            return true;
        }

        ProjectDependencyParser.ProjectDependencies dependencies
            = ProjectDependencyParser.parseDependencies(projectFile);

        // Add Frameworks category if there are framework references
        List<ProjectDependencyParser.FrameworkReference> frameworkReferences =
            new ArrayList<>(dependencies.getFrameworkReferences());
        Collections.sort(frameworkReferences, new Comparator<ProjectDependencyParser.FrameworkReference>() {
            @Override
            public int compare(ProjectDependencyParser.FrameworkReference o1, ProjectDependencyParser.FrameworkReference o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        if (!frameworkReferences.isEmpty()) {
            toPopulate.add(new DependencyCategory("Frameworks", DependencyType.FRAMEWORK, frameworkReferences));
        }

        // Add Packages category if there are package references
        List<ProjectDependencyParser.PackageReference> packageReferences =
            new ArrayList<>(dependencies.getPackageReferences());
        Collections.sort(packageReferences, new Comparator<ProjectDependencyParser.PackageReference>() {
            @Override
            public int compare(ProjectDependencyParser.PackageReference o1, ProjectDependencyParser.PackageReference o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        if (!packageReferences.isEmpty()) {
            toPopulate.add(new DependencyCategory("Packages", DependencyType.PACKAGE, packageReferences));
        }

        // Add Projects category if there are project references
        List<ProjectDependencyParser.ProjectReference> projectReferences =
            new ArrayList<>(dependencies.getProjectReferences());
        Collections.sort(projectReferences, new Comparator<ProjectDependencyParser.ProjectReference>() {
            @Override
            public int compare(ProjectDependencyParser.ProjectReference o1, ProjectDependencyParser.ProjectReference o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });

        if (!projectReferences.isEmpty()) {
            toPopulate.add(new DependencyCategory("Projects", DependencyType.PROJECT, projectReferences));
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(Object key) {
        if (key instanceof DependencyCategory) {
            return new DependencyCategoryNode((DependencyCategory) key);
        }
        return null;
    }

    private FileObject findProjectFile() {
        FileObject projectDir = project.getProjectDirectory();
        if (projectDir == null) {
            return null;
        }

        // Find the project file (supports .csproj, .vbproj, .fsproj, .vcxproj)
        for (FileObject child : projectDir.getChildren()) {
            if (!child.isFolder()) {
                String ext = child.getExt().toLowerCase();
                if ("csproj".equals(ext) || "vbproj".equals(ext) ||
                    "fsproj".equals(ext) || "vcxproj".equals(ext)) {
                    return child;
                }
            }
        }
        return null;
    }

    // Nested classes for dependency organization
    private enum DependencyType {
        FRAMEWORK,
        PACKAGE,
        PROJECT
    }

    private static class DependencyCategory {
        private final String displayName;
        private final DependencyType type;
        private final List<?> entries;

        DependencyCategory(String displayName, DependencyType type, List<?> entries) {
            this.displayName = displayName;
            this.type = type;
            this.entries = entries;
        }

        public String getDisplayName() {
            return displayName;
        }

        public DependencyType getType() {
            return type;
        }

        public List<?> getEntries() {
            return entries;
        }
    }

    private static class DependencyCategoryNode extends AbstractNode {
        private final DependencyCategory category;

        DependencyCategoryNode(DependencyCategory category) {
            super(Children.create(new DependencyLeafFactory(category), true));
            this.category = category;
            setDisplayName(category.getDisplayName());
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage(resolveCategoryIcon(category.getType()), true);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        private String resolveCategoryIcon(DependencyType type) {
            switch (type) {
                case PACKAGE:
                    return DependencyNode.PACKAGE_ICON;
                case PROJECT:
                    return DependencyNode.PROJECT_ICON;
                case FRAMEWORK:
                default:
                    return DependencyNode.FRAMEWORK_ICON;
            }
        }
    }

    private static class DependencyLeafFactory extends ChildFactory<Object> {
        private final DependencyCategory category;

        DependencyLeafFactory(DependencyCategory category) {
            this.category = category;
        }

        @Override
        protected boolean createKeys(List<Object> toPopulate) {
            toPopulate.addAll(category.getEntries());
            return true;
        }

        @Override
        protected Node createNodeForKey(Object key) {
            return new DependencyNode(key, category.getType());
        }
    }

    private static class DependencyNode extends AbstractNode {
        @StaticResource
        public static final String PACKAGE_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/package.svg";
        @StaticResource
        public static final String PROJECT_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/proj.svg";
        @StaticResource
        public static final String FRAMEWORK_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/framework.svg";

        private final Object dependency;
        private final DependencyType type;

        DependencyNode(Object dependency, DependencyType type) {
            super(Children.LEAF, Lookups.singleton(dependency));
            this.dependency = dependency;
            this.type = type;
            configurePresentation();
        }

        private void configurePresentation() {
            switch (type) {
                case PACKAGE: {
                    ProjectDependencyParser.PackageReference packageRef =
                        (ProjectDependencyParser.PackageReference) dependency;
                    setDisplayName(packageRef.toString());
                    setShortDescription("NuGet Package: " + packageRef.getName());
                    break;
                }
                case PROJECT: {
                    ProjectDependencyParser.ProjectReference projectRef =
                        (ProjectDependencyParser.ProjectReference) dependency;
                    setDisplayName(projectRef.getName());
                    setShortDescription("Project Reference: " + projectRef.getPath());
                    break;
                }
                case FRAMEWORK:
                default: {
                    ProjectDependencyParser.FrameworkReference frameworkRef =
                        (ProjectDependencyParser.FrameworkReference) dependency;
                    setDisplayName(frameworkRef.getName());
                    setShortDescription("Framework Reference: " + frameworkRef.getName());
                    break;
                }
            }
        }

        @Override
        public Image getIcon(int type) {
            switch (this.type) {
                case PACKAGE:
                    return ImageUtilities.loadImage(PACKAGE_ICON, true);
                case PROJECT:
                    return ImageUtilities.loadImage(PROJECT_ICON, true);
                case FRAMEWORK:
                default:
                    return ImageUtilities.loadImage(FRAMEWORK_ICON, true);
            }
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
}
