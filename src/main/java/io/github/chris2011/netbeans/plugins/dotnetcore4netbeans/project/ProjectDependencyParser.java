package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.project;

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

            // Try to resolve versions from Directory.Packages.props if needed
            resolveVersionsFromCentralPackageManagement(projectFile, dependencies);

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

            // If version is not an attribute, try to find it as a child element
            if (version == null || version.trim().isEmpty()) {
                NodeList versionNodes = packageElement.getElementsByTagName("Version");
                if (versionNodes.getLength() > 0) {
                    version = versionNodes.item(0).getTextContent().trim();
                }
            }

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

    /**
     * Resolve package versions from Directory.Packages.props if they are not already set.
     * This supports Central Package Management in .NET projects.
     */
    private static void resolveVersionsFromCentralPackageManagement(FileObject projectFile, ProjectDependencies dependencies) {
        // Check if any package references are missing versions
        boolean needsVersionResolution = false;
        for (PackageReference packageRef : dependencies.getPackageReferences()) {
            if (packageRef.getVersion() == null || packageRef.getVersion().trim().isEmpty()) {
                needsVersionResolution = true;
                break;
            }
        }

        if (!needsVersionResolution) {
            return; // All versions are already set
        }

        // Find Directory.Packages.props by walking up the directory tree
        FileObject packagesPropsFile = findDirectoryPackagesProps(projectFile);
        if (packagesPropsFile == null) {
            return;
        }

        // Parse Directory.Packages.props and extract version mappings
        java.util.Map<String, String> versionMap = parseDirectoryPackagesProps(packagesPropsFile);

        // Update package references with versions from the map
        for (PackageReference packageRef : dependencies.getPackageReferences()) {
            if (packageRef.getVersion() == null || packageRef.getVersion().trim().isEmpty()) {
                String version = versionMap.get(packageRef.getName());
                if (version != null) {
                    // Update the version using reflection or recreate the object
                    // Since PackageReference fields are private, we need to create a new list
                }
            }
        }

        // Recreate the package references list with resolved versions
        List<PackageReference> updatedReferences = new ArrayList<>();
        for (PackageReference packageRef : dependencies.getPackageReferences()) {
            String version = packageRef.getVersion();
            if (version == null || version.trim().isEmpty()) {
                version = versionMap.get(packageRef.getName());
            }
            updatedReferences.add(new PackageReference(packageRef.getName(), version));
        }
        dependencies.getPackageReferences().clear();
        dependencies.getPackageReferences().addAll(updatedReferences);
    }

    /**
     * Find Directory.Packages.props by walking up the directory tree from the project file.
     */
    private static FileObject findDirectoryPackagesProps(FileObject projectFile) {
        FileObject currentDir = projectFile.getParent();

        // Walk up the directory tree (max 10 levels to avoid infinite loops)
        for (int i = 0; i < 10 && currentDir != null; i++) {
            FileObject packagesProps = currentDir.getFileObject("Directory.Packages.props");
            if (packagesProps != null && packagesProps.isValid()) {
                return packagesProps;
            }
            currentDir = currentDir.getParent();
        }

        return null;
    }

    /**
     * Parse Directory.Packages.props and extract package version mappings.
     */
    private static java.util.Map<String, String> parseDirectoryPackagesProps(FileObject packagesPropsFile) {
        java.util.Map<String, String> versionMap = new java.util.HashMap<>();

        try (InputStream inputStream = packagesPropsFile.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Element root = document.getDocumentElement();
            NodeList packageVersionNodes = root.getElementsByTagName("PackageVersion");

            for (int i = 0; i < packageVersionNodes.getLength(); i++) {
                Element packageElement = (Element) packageVersionNodes.item(i);
                String name = packageElement.getAttribute("Include");
                String version = packageElement.getAttribute("Version");

                if (name != null && !name.trim().isEmpty() && version != null && !version.trim().isEmpty()) {
                    versionMap.put(name.trim(), version.trim());
                }
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "Failed to parse Directory.Packages.props: " + packagesPropsFile.getPath(), e);
        }

        return versionMap;
    }
}