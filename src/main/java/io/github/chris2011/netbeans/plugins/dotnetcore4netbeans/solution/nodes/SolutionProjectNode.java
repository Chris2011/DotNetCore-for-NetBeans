package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project.ProjectDependencyParser;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.netbeans.spi.project.ui.support.ProjectSensitiveActions;
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
    public static final String CSPROJ_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/csproj.svg";
    @StaticResource
    public static final String FSPROJ_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/fsproj.svg";
    @StaticResource
    public static final String DOCPROJ_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/docproj.svg";
    @StaticResource
    public static final String VBPROJ_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/vbproj.svg";
    @StaticResource
    public static final String VCXPROJ_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/vcxproj.svg";

    private final SolutionParser.ProjectInfo projectInfo;
    private final String projectExtension;

    public SolutionProjectNode(SolutionParser.ProjectInfo projectInfo) {
        super(Children.create(new ProjectChildrenFactory(projectInfo), true), Lookups.singleton(projectInfo));
        this.projectInfo = projectInfo;
        this.projectExtension = determineProjectExtension(projectInfo);
        setDisplayName(projectInfo.getName());
        setShortDescription(buildShortDescription(projectInfo));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(resolveProjectIcon(), true);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{
            new OpenProjectAction(),
            null, // Separator
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
    public Action getPreferredAction() {
        return new OpenProjectAction();
    }

    public SolutionParser.ProjectInfo getProjectInfo() {
        return projectInfo;
    }

    /**
     * Action to open the subproject in NetBeans
     */
    private class OpenProjectAction extends javax.swing.AbstractAction {

        public OpenProjectAction() {
            super("Open Project");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FileObject projectFile = projectInfo.getProjectFile();
            if (projectFile == null || !projectFile.isValid()) {
                return;
            }

            try {
                // Get the project directory (parent of the .csproj file)
                FileObject projectDir = projectFile.getParent();

                // Try to find the project
                Project project = ProjectManager.getDefault().findProject(projectDir);

                if (project != null) {
                    // Open the project
                    OpenProjects.getDefault().open(new Project[]{project}, false);
                }
            } catch (IOException | IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     * Factory for creating child nodes of a project.
     */
    private static class ProjectChildrenFactory extends ChildFactory<Object> {

        private static final String DEPENDENCIES_KEY = "#DEPENDENCIES";
        private static final Comparator<FileObject> FILE_OBJECT_COMPARATOR = new Comparator<FileObject>() {
            @Override
            public int compare(FileObject o1, FileObject o2) {
                return o1.getNameExt().compareToIgnoreCase(o2.getNameExt());
            }
        };
        private static final Set<String> SKIPPED_DIRECTORIES = new HashSet<>(Arrays.asList(
            "bin", "obj", "node_modules", "packages", "target", ".vs", ".git"
        ));
        private static final Set<String> SKIPPED_FILE_EXTENSIONS = new HashSet<>(Arrays.asList(
            "user", "cache"
        ));

        private final SolutionParser.ProjectInfo projectInfo;

        ProjectChildrenFactory(SolutionParser.ProjectInfo projectInfo) {
            this.projectInfo = projectInfo;
        }

        @Override
        protected boolean createKeys(List<Object> toPopulate) {
            toPopulate.add(DEPENDENCIES_KEY);

            FileObject projectDir = getProjectDirectory();
            if (projectDir != null) {
                List<FileObject> folders = new ArrayList<>();
                List<FileObject> files = new ArrayList<>();

                for (FileObject child : projectDir.getChildren()) {
                    if (!shouldInclude(child)) {
                        continue;
                    }

                    if (child.isFolder()) {
                        folders.add(child);
                    } else {
                        files.add(child);
                    }
                }

                Collections.sort(folders, FILE_OBJECT_COMPARATOR);
                Collections.sort(files, FILE_OBJECT_COMPARATOR);

                toPopulate.addAll(folders);
                toPopulate.addAll(files);
            }

            return true;
        }

        @Override
        protected Node createNodeForKey(Object key) {
            if (DEPENDENCIES_KEY.equals(key)) {
                return new DependenciesNode(projectInfo);
            }

            if (key instanceof FileObject) {
                FileObject fo = (FileObject) key;
                if (fo.isFolder()) {
                    return new DirectoryNode(fo);
                }

                try {
                    DataObject dataObject = DataObject.find(fo);
                    return dataObject.getNodeDelegate();
                } catch (Exception e) {
                    Exceptions.printStackTrace(e);
                }
            }

            return null;
        }

        private FileObject getProjectDirectory() {
            FileObject projectFile = projectInfo.getProjectFile();
            return projectFile != null ? projectFile.getParent() : null;
        }

        private boolean shouldInclude(FileObject child) {
            if (child == null || !child.isValid()) {
                return false;
            }

            if (child.isFolder()) {
                String name = child.getName();
                String normalized = name.toLowerCase();
                if (SKIPPED_DIRECTORIES.contains(normalized)) {
                    return false;
                }
                return !name.startsWith(".");
            }

            String ext = child.getExt();
            if (ext != null && SKIPPED_FILE_EXTENSIONS.contains(ext.toLowerCase())) {
                return false;
            }

            return true;
        }
    }

    /**
     * Dependencies node for NuGet packages and references
     */
    private static class DependenciesNode extends AbstractNode {

        @StaticResource
        public static final String DEPENDENCIES_ICON = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/dependencies.svg";

        DependenciesNode(SolutionParser.ProjectInfo projectInfo) {
            super(Children.create(new DependenciesChildrenFactory(projectInfo), true));
            setDisplayName("Dependencies");
            setShortDescription("Project Dependencies");
        }

        @Override
        public Image getIcon(int type) {
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

    /**
     * Factory for creating dependency nodes grounded by type
     */
    private static class DependenciesChildrenFactory extends ChildFactory<Object> {

        private final SolutionParser.ProjectInfo projectInfo;

        DependenciesChildrenFactory(SolutionParser.ProjectInfo projectInfo) {
            this.projectInfo = projectInfo;
        }

        @Override
        protected boolean createKeys(List<Object> toPopulate) {
            FileObject projectFile = projectInfo.getProjectFile();
//            if (projectFile != null) {
//                ProjectDependencyParser.ProjectDependencies dependencies
//                    = ProjectDependencyParser.parseDependencies(projectFile);
            if (projectFile == null) {
                return true;
            }

            ProjectDependencyParser.ProjectDependencies dependencies
                = ProjectDependencyParser.parseDependencies(projectFile);

            List<ProjectDependencyParser.FrameworkReference> frameworkReferences = new ArrayList<>(dependencies.getFrameworkReferences());
            Collections.sort(frameworkReferences, new Comparator<ProjectDependencyParser.FrameworkReference>() {
                @Override
                public int compare(ProjectDependencyParser.FrameworkReference o1, ProjectDependencyParser.FrameworkReference o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });

            if (!frameworkReferences.isEmpty()) {
                toPopulate.add(new DependencyCategory("Frameworks", DependencyType.FRAMEWORK, frameworkReferences));
            }

            List<ProjectDependencyParser.PackageReference> packageReferences = new ArrayList<>(dependencies.getPackageReferences());
            Collections.sort(packageReferences, new Comparator<ProjectDependencyParser.PackageReference>() {
                @Override
                public int compare(ProjectDependencyParser.PackageReference o1, ProjectDependencyParser.PackageReference o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });

            if (!packageReferences.isEmpty()) {
                toPopulate.add(new DependencyCategory("Packages", DependencyType.PACKAGE, packageReferences));
            }

            List<ProjectDependencyParser.ProjectReference> projectReferences = new ArrayList<>(dependencies.getProjectReferences());
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
            List<Object> sorted = new ArrayList<>(category.getEntries());

            switch (category.getType()) {
                case PACKAGE:
                    Collections.sort(sorted, new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            ProjectDependencyParser.PackageReference p1 = (ProjectDependencyParser.PackageReference) o1;
                            ProjectDependencyParser.PackageReference p2 = (ProjectDependencyParser.PackageReference) o2;
                            return p1.getName().compareToIgnoreCase(p2.getName());
                        }
                    });
                    break;
                case PROJECT:
                    Collections.sort(sorted, new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            ProjectDependencyParser.ProjectReference p1 = (ProjectDependencyParser.ProjectReference) o1;
                            ProjectDependencyParser.ProjectReference p2 = (ProjectDependencyParser.ProjectReference) o2;
                            return p1.getName().compareToIgnoreCase(p2.getName());
                        }
                    });
                    break;
                case FRAMEWORK:
                default:
                    Collections.sort(sorted, new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            ProjectDependencyParser.FrameworkReference f1 = (ProjectDependencyParser.FrameworkReference) o1;
                            ProjectDependencyParser.FrameworkReference f2 = (ProjectDependencyParser.FrameworkReference) o2;
                            return f1.getName().compareToIgnoreCase(f2.getName());
                        }
                    });
                    break;
            }

            toPopulate.addAll(sorted);
            return true;
        }

        @Override
        protected Node createNodeForKey(Object key) {
            return new DependencyNode(key, category.getType());
        }
    }

    /**
     * Node for individual dependencies
     */
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
                    ProjectDependencyParser.PackageReference packageRef = (ProjectDependencyParser.PackageReference) dependency;
                    setDisplayName(packageRef.toString());
                    setShortDescription("NuGet Package: " + packageRef.getName());
                    break;
                }
                case PROJECT: {
                    ProjectDependencyParser.ProjectReference projectRef = (ProjectDependencyParser.ProjectReference) dependency;
                    setDisplayName(projectRef.getName());
                    setShortDescription("Project Reference: " + projectRef.getPath());
                    break;
                }
                case FRAMEWORK:
                default: {
                    ProjectDependencyParser.FrameworkReference frameworkRef = (ProjectDependencyParser.FrameworkReference) dependency;
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

    private static String determineProjectExtension(SolutionParser.ProjectInfo projectInfo) {
        FileObject projectFile = projectInfo.getProjectFile();
        if (projectFile != null) {
            String ext = projectFile.getExt();
            if (ext != null) {
                return ext.toLowerCase();
            }
        }

        String relativePath = projectInfo.getRelativePath();
        if (relativePath != null) {
            int dotIndex = relativePath.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < relativePath.length() - 1) {
                return relativePath.substring(dotIndex + 1).toLowerCase();
            }
        }

        return "";
    }

    private String buildShortDescription(SolutionParser.ProjectInfo projectInfo) {
        FileObject projectFile = projectInfo.getProjectFile();
        String location = projectFile != null ? projectFile.getPath() : projectInfo.getRelativePath();
        String typeLabel = projectExtension.isEmpty() ? "Project" : ("." + projectExtension + " project");
        if (location == null || location.isEmpty()) {
            return typeLabel;
        }
        return typeLabel + ": " + location;
    }

    private String resolveProjectIcon() {
        if ("fsproj".equals(projectExtension)) {
            return FSPROJ_ICON;
        }

        if ("vbproj".equals(projectExtension)) {
            return VBPROJ_ICON;
        }

        if ("docproj".equals(projectExtension)) {
            return DOCPROJ_ICON;
        }

        if ("vcxproj".equals(projectExtension)) {
            return VCXPROJ_ICON;
        }

        return CSPROJ_ICON;
    }
}
