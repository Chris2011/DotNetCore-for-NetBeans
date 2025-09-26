package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.openide.filesystems.FileObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Parser for .csproj files to extract dependencies and references.
 */
public class ProjectDependencyParser {

    private static final Logger LOG = Logger.getLogger(ProjectDependencyParser.class.getName());

    public static class ProjectDependencies {
        private List<PackageReference> packageReferences = new ArrayList<>();
        private List<ProjectReference> projectReferences = new ArrayList<>();
        private List<FrameworkReference> frameworkReferences = new ArrayList<>();
        private String targetFramework;

        public List<PackageReference> getPackageReferences() { return packageReferences; }
        public List<ProjectReference> getProjectReferences() { return projectReferences; }
        public List<FrameworkReference> getFrameworkReferences() { return frameworkReferences; }
        public String getTargetFramework() { return targetFramework; }
        public void setTargetFramework(String targetFramework) { this.targetFramework = targetFramework; }
    }

    public static class PackageReference {
        private String name;
        private String version;

        public PackageReference(String name, String version) {
            this.name = name;
            this.version = version;
        }

        public String getName() { return name; }
        public String getVersion() { return version; }

        @Override
        public String toString() {
            return name + (version != null ? " (" + version + ")" : "");
        }
    }

    public static class ProjectReference {
        private String name;
        private String path;

        public ProjectReference(String name, String path) {
            this.name = name;
            this.path = path;
        }

        public String getName() { return name; }
        public String getPath() { return path; }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class FrameworkReference {
        private String name;

        public FrameworkReference(String name) {
            this.name = name;
        }

        public String getName() { return name; }

        @Override
        public String toString() {
            return name;
        }
    }

    /**
     * Parse a .csproj file and extract dependencies.
     */
    public static ProjectDependencies parseDependencies(FileObject projectFile) {
        ProjectDependencies dependencies = new ProjectDependencies();

        if (projectFile == null || !projectFile.isValid()) {
            return dependencies;
        }

        try (InputStream inputStream = projectFile.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Element root = document.getDocumentElement();

            // Parse TargetFramework
            parseTargetFramework(root, dependencies);

            // Parse PackageReference elements
            parsePackageReferences(root, dependencies);

            // Parse ProjectReference elements
            parseProjectReferences(root, dependencies);

            // Parse FrameworkReference elements
            parseFrameworkReferences(root, dependencies);

        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to parse project file: " + projectFile.getPath(), e);
        }

        return dependencies;
    }

    private static void parseTargetFramework(Element root, ProjectDependencies dependencies) {
        NodeList targetFrameworkNodes = root.getElementsByTagName("TargetFramework");
        if (targetFrameworkNodes.getLength() > 0) {
            String targetFramework = targetFrameworkNodes.item(0).getTextContent().trim();
            dependencies.setTargetFramework(targetFramework);
        } else {
            // Try TargetFrameworks (plural)
            NodeList targetFrameworksNodes = root.getElementsByTagName("TargetFrameworks");
            if (targetFrameworksNodes.getLength() > 0) {
                String targetFrameworks = targetFrameworksNodes.item(0).getTextContent().trim();
                // Take the first framework if multiple are specified
                String firstFramework = targetFrameworks.split(";")[0];
                dependencies.setTargetFramework(firstFramework);
            }
        }
    }

    private static void parsePackageReferences(Element root, ProjectDependencies dependencies) {
        NodeList packageNodes = root.getElementsByTagName("PackageReference");
        for (int i = 0; i < packageNodes.getLength(); i++) {
            Element packageElement = (Element) packageNodes.item(i);
            String name = packageElement.getAttribute("Include");
            String version = packageElement.getAttribute("Version");

            if (name != null && !name.trim().isEmpty()) {
                dependencies.getPackageReferences().add(new PackageReference(name.trim(), version));
            }
        }
    }

    private static void parseProjectReferences(Element root, ProjectDependencies dependencies) {
        NodeList projectNodes = root.getElementsByTagName("ProjectReference");
        for (int i = 0; i < projectNodes.getLength(); i++) {
            Element projectElement = (Element) projectNodes.item(i);
            String path = projectElement.getAttribute("Include");

            if (path != null && !path.trim().isEmpty()) {
                // Extract project name from path
                String projectName = extractProjectNameFromPath(path);
                dependencies.getProjectReferences().add(new ProjectReference(projectName, path.trim()));
            }
        }
    }

    private static void parseFrameworkReferences(Element root, ProjectDependencies dependencies) {
        NodeList frameworkNodes = root.getElementsByTagName("FrameworkReference");
        for (int i = 0; i < frameworkNodes.getLength(); i++) {
            Element frameworkElement = (Element) frameworkNodes.item(i);
            String name = frameworkElement.getAttribute("Include");

            if (name != null && !name.trim().isEmpty()) {
                dependencies.getFrameworkReferences().add(new FrameworkReference(name.trim()));
            }
        }
    }

    private static String extractProjectNameFromPath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }

        // Convert backslashes to forward slashes
        path = path.replace('\\', '/');

        // Get the filename without extension
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        if (fileName.contains(".")) {
            fileName = fileName.substring(0, fileName.lastIndexOf('.'));
        }

        return fileName;
    }
}