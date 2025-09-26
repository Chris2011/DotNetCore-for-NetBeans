package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.projecttypes.universal;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a .NET CLI project template with categorization information.
 */
public class DotNetTemplate {
    private final String displayName;
    private final String shortName;
    private final List<String> languages;
    private final List<String> tags;
    private final String category;
    private final String description;

    public DotNetTemplate(String displayName, String shortName, String languages, String tags) {
        this.displayName = displayName;
        this.shortName = shortName;
        this.languages = parseLanguages(languages);
        this.tags = parseTags(tags);
        this.category = determineCategory(this.tags);
        this.description = generateDescription();
    }

    private List<String> parseLanguages(String languages) {
        if (languages == null || languages.trim().isEmpty()) {
            return Arrays.asList("C#");
        }
        // Remove brackets and split by comma
        String cleaned = languages.replaceAll("[\\[\\]]", "");
        return Arrays.asList(cleaned.split(","));
    }

    private List<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Arrays.asList();
        }
        return Arrays.asList(tags.split("/"));
    }

    private String determineCategory(List<String> tags) {
        // Categorize based on tags
        for (String tag : tags) {
            switch (tag.toLowerCase()) {
                case "web":
                    return "Web Development";
                case "winforms":
                case "wpf":
                    return "Desktop Development";
                case "android":
                case "ios":
                case "maui":
                    return "Mobile Development";
                case "test":
                case "mstest":
                case "nunit":
                case "xunit":
                    return "Testing";
                case "library":
                case "common":
                    return "Libraries";
                case "config":
                    return "Configuration";
                case "solution":
                    return "Solution";
                case "grpc":
                    return "Services";
            }
        }

        // Default categorization based on template name
        String lowerName = displayName.toLowerCase();
        if (lowerName.contains("web") || lowerName.contains("api") || lowerName.contains("blazor")) {
            return "Web Development";
        } else if (lowerName.contains("winforms") || lowerName.contains("wpf")) {
            return "Desktop Development";
        } else if (lowerName.contains("android") || lowerName.contains("ios") || lowerName.contains("maui")) {
            return "Mobile Development";
        } else if (lowerName.contains("test")) {
            return "Testing";
        } else if (lowerName.contains("library") || lowerName.contains("lib")) {
            return "Libraries";
        } else if (lowerName.contains("console")) {
            return "Console Applications";
        }

        return "Other";
    }

    private String generateDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(displayName);

        if (!languages.isEmpty()) {
            desc.append(" (").append(String.join(", ", languages)).append(")");
        }

        if (!tags.isEmpty()) {
            desc.append(" - ").append(String.join(", ", tags));
        }

        return desc.toString();
    }

    // Getters
    public String getDisplayName() { return displayName; }
    public String getShortName() { return shortName; }
    public List<String> getLanguages() { return languages; }
    public List<String> getTags() { return tags; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }

    public boolean supportsLanguage(String language) {
        return languages.contains(language);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DotNetTemplate template = (DotNetTemplate) obj;
        return Objects.equals(shortName, template.shortName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortName);
    }
}