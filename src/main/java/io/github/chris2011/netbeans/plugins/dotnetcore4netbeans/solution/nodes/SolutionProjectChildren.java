package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.nodes;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.CSharpSolution;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution.SolutionParser;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * Children factory that creates Visual Studio-like structure:
 * - Solution Folders
 * - Projects (from .sln file)
 */
public class SolutionProjectChildren extends ChildFactory<Object> {

    private final CSharpSolution solution;

    public SolutionProjectChildren(CSharpSolution solution) {
        this.solution = solution;
    }

    @Override
    protected boolean createKeys(List<Object> toPopulate) {
        try {
            FileObject solutionDir = solution.getProjectDirectory();
            SolutionParser.SolutionStructure structure = SolutionParser.parseSolution(solutionDir);

            // First, add real directories that contain projects or other interesting content
            addRealDirectories(solutionDir, toPopulate, structure);

            // Then add solution folders (virtual folders from .sln)
            for (SolutionParser.SolutionFolder folder : structure.getFolders()) {
                // Only add solution folders that aren't already represented by real directories
                if (!isRealDirectory(solutionDir, folder.getName())) {
                    toPopulate.add(folder);
                }
            }

            // Finally, add root-level projects (projects not in any folder)
            for (SolutionParser.ProjectInfo project : structure.getProjects()) {
                if (project.getProjectFile() != null && !isProjectInAnyFolder(project, structure, solutionDir)) {
                    toPopulate.add(project);
                }
            }

        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Object key) {
        if (key instanceof FileObject) {
            // Real directory or project file
            FileObject fileObject = (FileObject) key;
            if (fileObject.isFolder()) {
                return new DirectoryNode(fileObject);
            } else if ("csproj".equals(fileObject.getExt()) || "fsproj".equals(fileObject.getExt()) || "vbproj".equals(fileObject.getExt())) {
                SolutionParser.ProjectInfo projectInfo = new SolutionParser.ProjectInfo(
                    fileObject.getName(),
                    fileObject.getPath(),
                    ""
                );
                projectInfo.setProjectFile(fileObject);
                return new SolutionProjectNode(projectInfo);
            }
        } else if (key instanceof SolutionParser.SolutionFolder) {
            // Virtual solution folder from .sln
            return new SolutionFolderNode((SolutionParser.SolutionFolder) key, solution);
        } else if (key instanceof SolutionParser.ProjectInfo) {
            // Project from .sln
            return new SolutionProjectNode((SolutionParser.ProjectInfo) key);
        }
        return null;
    }

    private void addRealDirectories(FileObject solutionDir, List<Object> toPopulate, SolutionParser.SolutionStructure structure) {
        // Look for common directory patterns that should be shown as folders
        String[] commonDirs = {"src", "common", "modules", "settings-ui", "tests", "test", "lib", "libs", "tools", "docs"};

        for (FileObject child : solutionDir.getChildren()) {
            if (child.isFolder() && shouldShowDirectory(child, commonDirs)) {
                toPopulate.add(child);
            }
        }

        // Also check for src subdirectories if src exists
        FileObject srcDir = solutionDir.getFileObject("src");
        if (srcDir != null && srcDir.isFolder()) {
            for (FileObject srcChild : srcDir.getChildren()) {
                if (srcChild.isFolder() && hasInterestingContent(srcChild)) {
                    toPopulate.add(srcChild);
                }
            }
        }
    }

    private boolean shouldShowDirectory(FileObject dir, String[] commonDirs) {
        String name = dir.getName();

        // Skip build/temp directories
        if (name.equals("bin") || name.equals("obj") || name.equals("target") ||
            name.equals("node_modules") || name.startsWith(".")) {
            return false;
        }

        // Show if it's a common directory name OR contains projects
        for (String commonDir : commonDirs) {
            if (name.equals(commonDir)) {
                return true;
            }
        }

        return hasInterestingContent(dir);
    }

    private boolean hasInterestingContent(FileObject dir) {
        // Check if directory contains projects or other directories with projects
        for (FileObject child : dir.getChildren()) {
            if (child.isFolder()) {
                // Has subdirectories - potentially interesting
                if (containsProjectFiles(child)) {
                    return true;
                }
            } else if ("csproj".equals(child.getExt()) || "fsproj".equals(child.getExt()) || "vbproj".equals(child.getExt())) {
                return true;
            }
        }
        return false;
    }

    private boolean isRealDirectory(FileObject solutionDir, String folderName) {
        FileObject dir = solutionDir.getFileObject(folderName);
        if (dir != null && dir.isFolder()) {
            return true;
        }

        // Also check in src subdirectory
        FileObject srcDir = solutionDir.getFileObject("src");
        if (srcDir != null) {
            FileObject srcSubdir = srcDir.getFileObject(folderName);
            return srcSubdir != null && srcSubdir.isFolder();
        }

        return false;
    }

    private boolean isProjectInAnyFolder(SolutionParser.ProjectInfo project, SolutionParser.SolutionStructure structure, FileObject solutionDir) {
        // Check if project is in a solution folder
        if (isProjectInFolder(project, structure)) {
            return true;
        }

        // Check if project is in a real directory that we're showing
        String projectPath = project.getRelativePath();
        if (projectPath.contains("/") || projectPath.contains("\\")) {
            // Project is in a subdirectory
            String dirPath = projectPath.substring(0, Math.max(projectPath.lastIndexOf('/'), projectPath.lastIndexOf('\\')));
            FileObject projectDir = solutionDir.getFileObject(dirPath.replace('\\', '/'));
            return projectDir != null && projectDir.isFolder();
        }

        return false;
    }

    private boolean isProjectInFolder(SolutionParser.ProjectInfo project, SolutionParser.SolutionStructure structure) {
        String projectGuid = project.getProjectGuid().replace("{", "").replace("}", "");

        // Check all folders to see if this project is in any of them
        for (List<String> projectGuids : structure.getFolderProjects().values()) {
            for (String folderProjectGuid : projectGuids) {
                if (projectGuid.equals(folderProjectGuid) ||
                    projectGuid.equals(folderProjectGuid.replace("{", "").replace("}", ""))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void fallbackToDirectoryScanning(FileObject solutionDir, List<Object> toPopulate) {
        for (FileObject child : solutionDir.getChildren()) {
            if (child.isFolder()) {
                // Check if folder contains projects
                if (containsProjectFiles(child)) {
                    toPopulate.add(child);
                }
            } else if ("csproj".equals(child.getExt()) || "fsproj".equals(child.getExt()) || "vbproj".equals(child.getExt())) {
                toPopulate.add(child);
            }
        }
    }

    private boolean containsProjectFiles(FileObject dir) {
        for (FileObject child : dir.getChildren()) {
            if ("csproj".equals(child.getExt()) || "fsproj".equals(child.getExt()) || "vbproj".equals(child.getExt())) {
                return true;
            }
            if (child.isFolder() && containsProjectFiles(child)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create Children instance for this factory.
     */
    public static Children create(CSharpSolution solution) {
        return Children.create(new SolutionProjectChildren(solution), true);
    }
}