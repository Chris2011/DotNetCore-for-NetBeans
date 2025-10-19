package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.FileObject;

/**
 * Parser for .sln files to extract project structure and dependencies.
 */
public class SolutionParser {

    private static final Logger LOG = Logger.getLogger(SolutionParser.class.getName());

    public static class SolutionStructure {

        private final List<ProjectInfo> projects = new ArrayList<>();
        private final List<SolutionFolder> folders = new ArrayList<>();
        private final Map<String, List<String>> folderProjects = new HashMap<>();
        private final Map<String, List<String>> folderChildren = new HashMap<>();
        private final Map<String, SolutionFolder> folderByGuid = new HashMap<>();
        private final Map<String, ProjectInfo> projectByGuid = new HashMap<>();
        private final Map<String, String> parentByGuid = new HashMap<>();

        public List<ProjectInfo> getProjects() {
            return projects;
        }

        public List<SolutionFolder> getFolders() {
            return folders;
        }

        public Map<String, List<String>> getFolderProjects() {
            return folderProjects;
        }

        public Map<String, List<String>> getFolderChildren() {
            return folderChildren;
        }

        public Map<String, SolutionFolder> getFolderByGuid() {
            return folderByGuid;
        }

        public Map<String, ProjectInfo> getProjectByGuid() {
            return projectByGuid;
        }

        public Map<String, String> getParentByGuid() {
            return parentByGuid;
        }

        public List<SolutionFolder> getRootFolders() {
            List<SolutionFolder> roots = new ArrayList<>();
            for (SolutionFolder folder : folders) {
                String cleanGuid = cleanGuid(folder.getFolderGuid());
                String parentGuid = parentByGuid.get(cleanGuid);
                if (parentGuid == null || !folderByGuid.containsKey(parentGuid)) {
                    roots.add(folder);
                }
            }
            return roots;
        }

        public List<ProjectInfo> getRootProjects() {
            List<ProjectInfo> roots = new ArrayList<>();
            for (ProjectInfo project : projects) {
                String cleanGuid = cleanGuid(project.getProjectGuid());
                String parentGuid = parentByGuid.get(cleanGuid);
                if (parentGuid == null || !folderByGuid.containsKey(parentGuid)) {
                    roots.add(project);
                }
            }
            return roots;
        }
    }

    public static class ProjectInfo {

        private String name;
        private String relativePath;
        private String projectGuid;
        private FileObject projectFile;

        public ProjectInfo(String name, String relativePath, String projectGuid) {
            this.name = name;
            this.relativePath = relativePath;
            this.projectGuid = projectGuid;
        }

        public String getName() {
            return name;
        }

        public String getRelativePath() {
            return relativePath;
        }

        public String getProjectGuid() {
            return projectGuid;
        }

        public FileObject getProjectFile() {
            return projectFile;
        }

        public void setProjectFile(FileObject projectFile) {
            this.projectFile = projectFile;
        }
    }

    public static class SolutionFolder {

        private String name;
        private String folderGuid;
        private final List<String> items = new ArrayList<>();

        public SolutionFolder(String name, String folderGuid) {
            this.name = name;
            this.folderGuid = folderGuid;
        }

        public String getName() {
            return name;
        }

        public String getFolderGuid() {
            return folderGuid;
        }

        public List<String> getItems() {
            return items;
        }

        public void addItem(String itemPath) {
            if (itemPath != null && !itemPath.trim().isEmpty()) {
                items.add(itemPath.trim());
            }
        }
    }

    /**
     * Parse a .sln file and return the solution structure.
     */
    public static SolutionStructure parseSolution(FileObject solutionDir) {
        SolutionStructure structure = new SolutionStructure();

        // Find .sln file
        FileObject slnFile = null;
        for (FileObject child : solutionDir.getChildren()) {
            if ("sln".equals(child.getExt())) {
                slnFile = child;
                break;
            }
        }

        if (slnFile == null) {
            LOG.warning("No .sln file found in directory: " + solutionDir.getPath());
            return structure;
        }

        try {
            Path slnPath = Paths.get(slnFile.getPath());
            List<String> lines = Files.readAllLines(slnPath);

            boolean inNestedProjects = false;
            boolean inProjectSection = false;
            SolutionFolder currentFolder = null;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();

                // Parse project entries: Project("{GUID}") = "ProjectName", "RelativePath", "{ProjectGUID}"
                if (line.startsWith("Project(")) {
                    currentFolder = parseProjectLine(line, structure, solutionDir);
                    inProjectSection = false;
                } else if (line.contains("EndProject")) {
                    currentFolder = null;
                    inProjectSection = false;
                } else if (currentFolder != null && line.contains("ProjectSection(SolutionItems)")) {
                    inProjectSection = true;
                } else if (inProjectSection && line.contains("EndProjectSection")) {
                    inProjectSection = false;
                } else if (inProjectSection && line.contains(" = ")) {
                    // Parse solution item: relativePath = relativePath
                    String[] parts = line.split("=");
                    if (parts.length >= 1) {
                        String itemPath = parts[0].trim();
                        currentFolder.addItem(itemPath);
                    }
                } else if (line.contains("GlobalSection(NestedProjects)")) {
                    inNestedProjects = true;
                } else if (line.contains("EndGlobalSection") && inNestedProjects) {
                    inNestedProjects = false;
                } else if (inNestedProjects && line.contains(" = ")) {
                    parseNestedProjectLine(line, structure);
                }
            }

            // Resolve project files
            resolveProjectFiles(structure, solutionDir);

        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to parse solution file: " + slnFile.getPath(), e);
        }

        return structure;
    }

    private static SolutionFolder parseProjectLine(String line, SolutionStructure structure, FileObject solutionDir) {
        try {
            // Extract the parts between quotes
            String[] parts = line.split("\"");
            if (parts.length >= 6) {
                String projectName = parts[3];
                String relativePath = parts[5];
                String projectGuid = parts.length > 7 ? parts[7] : "";

                // Check if it's a solution folder (special GUID)
                String folderTypeGuid = parts[1];
                String cleanFolderTypeGuid = folderTypeGuid != null ? folderTypeGuid.toUpperCase() : "";
                if ("{2150E333-8FDC-42A3-9474-1A3956D46DE8}".equals(cleanFolderTypeGuid)) {
                    SolutionFolder folder = new SolutionFolder(projectName, projectGuid);
                    structure.getFolders().add(folder);
                    String cleanGuid = cleanGuid(projectGuid);
                    if (!cleanGuid.isEmpty()) {
                        structure.getFolderByGuid().put(cleanGuid, folder);
                    }
                    return folder;
                } else if (isProjectEntry(relativePath)) {
                    // This is a project
                    ProjectInfo project = new ProjectInfo(projectName, relativePath, projectGuid);
                    structure.getProjects().add(project);
                    String cleanGuid = cleanGuid(projectGuid);
                    if (!cleanGuid.isEmpty()) {
                        structure.getProjectByGuid().put(cleanGuid, project);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "Failed to parse project line: " + line, e);
        }
        return null;
    }

    private static void parseNestedProjectLine(String line, SolutionStructure structure) {
        try {
            // Format: {ProjectGUID} = {ParentFolderGUID}
            String[] parts = line.split("=");
            if (parts.length == 2) {
                String childGuid = cleanGuid(parts[0].trim());
                String parentGuid = cleanGuid(parts[1].trim());

                if (childGuid.isEmpty()) {
                    return;
                }

                structure.getParentByGuid().put(childGuid, parentGuid);

                if (!parentGuid.isEmpty() && structure.getFolderByGuid().containsKey(parentGuid)) {
                    if (structure.getProjectByGuid().containsKey(childGuid)) {
                        structure.getFolderProjects()
                            .computeIfAbsent(parentGuid, guid -> new ArrayList<>())
                            .add(childGuid);
                    } else if (structure.getFolderByGuid().containsKey(childGuid)) {
                        structure.getFolderChildren()
                            .computeIfAbsent(parentGuid, guid -> new ArrayList<>())
                            .add(childGuid);
                    }
                }
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "Failed to parse nested project line: " + line, e);
        }
    }

    private static void resolveProjectFiles(SolutionStructure structure, FileObject solutionDir) {
        for (ProjectInfo project : structure.getProjects()) {
            try {
                // Convert relative path to FileObject
                String relativePath = project.getRelativePath().replace('\\', '/');
                FileObject projectFile = solutionDir.getFileObject(relativePath);

                if (projectFile != null && projectFile.isValid()) {
                    project.setProjectFile(projectFile);
                }
            } catch (Exception e) {
                LOG.log(Level.FINE, "Failed to resolve project file: " + project.getRelativePath(), e);
            }
        }
    }

    private static boolean isProjectEntry(String relativePath) {
        if (relativePath == null) {
            return false;
        }

        String normalized = relativePath.trim().toLowerCase();
        return normalized.endsWith("proj");
    }

    public static String cleanGuid(String guid) {
        if (guid == null) {
            return "";
        }

        String trimmed = guid.trim();
        if (trimmed.isEmpty()) {
            return "";
        }

        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        return trimmed.toUpperCase();
    }
}
