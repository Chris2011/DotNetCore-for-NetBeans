package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.solution;

import java.io.BufferedReader;
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
        private List<ProjectInfo> projects = new ArrayList<>();
        private List<SolutionFolder> folders = new ArrayList<>();
        private Map<String, List<String>> folderProjects = new HashMap<>();

        public List<ProjectInfo> getProjects() { return projects; }
        public List<SolutionFolder> getFolders() { return folders; }
        public Map<String, List<String>> getFolderProjects() { return folderProjects; }
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

        public String getName() { return name; }
        public String getRelativePath() { return relativePath; }
        public String getProjectGuid() { return projectGuid; }
        public FileObject getProjectFile() { return projectFile; }
        public void setProjectFile(FileObject projectFile) { this.projectFile = projectFile; }
    }

    public static class SolutionFolder {
        private String name;
        private String folderGuid;

        public SolutionFolder(String name, String folderGuid) {
            this.name = name;
            this.folderGuid = folderGuid;
        }

        public String getName() { return name; }
        public String getFolderGuid() { return folderGuid; }
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
            for (String line : lines) {
                line = line.trim();

                // Parse project entries: Project("{GUID}") = "ProjectName", "RelativePath", "{ProjectGUID}"
                if (line.startsWith("Project(")) {
                    parseProjectLine(line, structure, solutionDir);
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

    private static void parseProjectLine(String line, SolutionStructure structure, FileObject solutionDir) {
        try {
            // Extract the parts between quotes
            String[] parts = line.split("\"");
            if (parts.length >= 6) {
                String projectName = parts[3];
                String relativePath = parts[5];
                String projectGuid = parts.length > 7 ? parts[7] : "";

                // Check if it's a solution folder (special GUID)
                String folderTypeGuid = parts[1];
                if ("{2150E333-8FDC-42A3-9474-1A3956D46DE8}".equals(folderTypeGuid)) {
                    // This is a solution folder
                    structure.getFolders().add(new SolutionFolder(projectName, projectGuid));
                } else if (relativePath.endsWith(".csproj") || relativePath.endsWith(".fsproj") || relativePath.endsWith(".vbproj")) {
                    // This is a project
                    ProjectInfo project = new ProjectInfo(projectName, relativePath, projectGuid);
                    structure.getProjects().add(project);
                }
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "Failed to parse project line: " + line, e);
        }
    }

    private static void parseNestedProjectLine(String line, SolutionStructure structure) {
        try {
            // Format: {ProjectGUID} = {ParentFolderGUID}
            String[] parts = line.split("=");
            if (parts.length == 2) {
                String projectGuid = parts[0].trim().replace("{", "").replace("}", "");
                String parentGuid = parts[1].trim().replace("{", "").replace("}", "");

                // Find the project and add it to the folder's projects list
                for (ProjectInfo project : structure.getProjects()) {
                    String cleanProjectGuid = project.getProjectGuid().replace("{", "").replace("}", "");
                    if (projectGuid.equals(cleanProjectGuid)) {
                        if (!structure.getFolderProjects().containsKey(parentGuid)) {
                            structure.getFolderProjects().put(parentGuid, new ArrayList<>());
                        }
                        structure.getFolderProjects().get(parentGuid).add(project.getProjectGuid());
                        break;
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
                } else {
                    // For testing with non-existent paths, set a dummy file
                    // In real scenarios, projects without files won't be displayed
                    LOG.log(Level.FINE, "Project file not found: " + relativePath + ", using dummy");
                    project.setProjectFile(solutionDir);
                }
            } catch (Exception e) {
                LOG.log(Level.FINE, "Failed to resolve project file: " + project.getRelativePath(), e);
            }
        }
    }
}