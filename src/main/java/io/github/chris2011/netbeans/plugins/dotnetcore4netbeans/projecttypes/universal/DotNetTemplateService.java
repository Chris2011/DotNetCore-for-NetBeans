package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.projecttypes.universal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.options.DotnetCliOptions;

/**
 * Service to discover and manage .NET CLI project templates.
 */
public class DotNetTemplateService {
    private static final Logger LOG = Logger.getLogger(DotNetTemplateService.class.getName());
    private static final String DOTNET_CLI = DotnetCliOptions.getInstance().getDotnetCli();

    // Pattern to parse dotnet new list output
    private static final Pattern TEMPLATE_PATTERN = Pattern.compile(
        "^(.+?)\\s{2,}(.+?)\\s{2,}(\\[.+?\\])\\s{2,}(.+)$"
    );

    private List<DotNetTemplate> cachedTemplates = null;

    public CompletableFuture<List<DotNetTemplate>> getAvailableTemplatesAsync() {
        return CompletableFuture.supplyAsync(() -> {
            if (cachedTemplates != null) {
                return cachedTemplates;
            }
            return loadTemplatesFromCli();
        });
    }

    public List<DotNetTemplate> getAvailableTemplates() {
        if (cachedTemplates != null) {
            return cachedTemplates;
        }
        return loadTemplatesFromCli();
    }

    public Map<String, List<DotNetTemplate>> getTemplatesByCategory() {
        List<DotNetTemplate> templates = getAvailableTemplates();
        return templates.stream()
                .collect(Collectors.groupingBy(DotNetTemplate::getCategory));
    }

    public List<String> getAvailableCategories() {
        return new ArrayList<>(getTemplatesByCategory().keySet());
    }

    public List<DotNetTemplate> getTemplatesForCategory(String category) {
        Map<String, List<DotNetTemplate>> templatesByCategory = getTemplatesByCategory();
        return templatesByCategory.getOrDefault(category, new ArrayList<>());
    }

    public DotNetTemplate findTemplateByShortName(String shortName) {
        return getAvailableTemplates().stream()
                .filter(t -> t.getShortName().equals(shortName))
                .findFirst()
                .orElse(null);
    }

    private List<DotNetTemplate> loadTemplatesFromCli() {
        List<DotNetTemplate> templates = new ArrayList<>();

        Process process = null;
        try {
            if (DOTNET_CLI == null || DOTNET_CLI.trim().isEmpty()) {
                LOG.warning("No .NET CLI path configured, using default templates");
                return getDefaultTemplates();
            }

            ProcessBuilder pb = new ProcessBuilder(DOTNET_CLI, "new", "list");
            pb.redirectErrorStream(true);
            process = pb.start();

            try {
                // Set timeout for template loading
                boolean finished = process.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
                if (!finished) {
                    LOG.warning("Template loading timed out, using default templates");
                    process.destroyForcibly();
                    return getDefaultTemplates();
                }

                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    LOG.warning("dotnet new list exited with code: " + exitCode + ", using default templates");
                    return getDefaultTemplates();
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    boolean inTemplateSection = false;

                    while ((line = reader.readLine()) != null) {
                        // Skip until we find the template table header
                        if (line.contains("Vorlagenname") || line.contains("Template Name")) {
                            inTemplateSection = true;
                            reader.readLine(); // Skip separator line
                            continue;
                        }

                        if (!inTemplateSection || line.trim().isEmpty()) {
                            continue;
                        }

                        DotNetTemplate template = parseTemplateLine(line);
                        if (template != null) {
                            templates.add(template);
                        }
                    }
                }
            } finally {
                // Ensure process cleanup
                if (process != null && process.isAlive()) {
                    process.destroyForcibly();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            LOG.log(Level.WARNING, "Template loading was interrupted, using default templates", e);
            return getDefaultTemplates();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Failed to load .NET CLI templates, using default templates", e);
            return getDefaultTemplates();
        }

        cachedTemplates = templates;
        LOG.info("Loaded " + templates.size() + " .NET CLI templates");
        return templates;
    }

    private DotNetTemplate parseTemplateLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        try {
            Matcher matcher = TEMPLATE_PATTERN.matcher(line);
            if (matcher.find() && matcher.groupCount() >= 4) {
                String displayName = matcher.group(1) != null ? matcher.group(1).trim() : "";
                String shortName = matcher.group(2) != null ? matcher.group(2).trim() : "";
                String languages = matcher.group(3) != null ? matcher.group(3).trim() : "[C#]";
                String tags = matcher.group(4) != null ? matcher.group(4).trim() : "";

                // Skip empty or invalid templates
                if (displayName.isEmpty() || shortName.isEmpty()) {
                    return null;
                }

                return new DotNetTemplate(displayName, shortName, languages, tags);
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "Failed to parse template line: " + line, e);
        }
        return null;
    }

    private List<DotNetTemplate> getDefaultTemplates() {
        List<DotNetTemplate> defaults = new ArrayList<>();
        defaults.add(new DotNetTemplate("Console Application", "console", "[C#],F#,VB", "Common/Console"));
        defaults.add(new DotNetTemplate("Class Library", "classlib", "[C#],F#,VB", "Common/Library"));
        defaults.add(new DotNetTemplate("ASP.NET Core Web API", "webapi", "[C#],F#", "Web/WebAPI"));
        defaults.add(new DotNetTemplate("ASP.NET Core Web App", "webapp", "[C#]", "Web/MVC/Razor Pages"));
        defaults.add(new DotNetTemplate("Blazor WebAssembly App", "blazorwasm", "[C#]", "Web/Blazor/WebAssembly/PWA"));
        defaults.add(new DotNetTemplate("Windows Forms App", "winforms", "[C#],VB", "Common/WinForms"));
        defaults.add(new DotNetTemplate("WPF Application", "wpf", "[C#],VB", "Common/WPF"));
        defaults.add(new DotNetTemplate("xUnit Test Project", "xunit", "[C#],F#,VB", "Test/xUnit"));
        defaults.add(new DotNetTemplate("Solution File", "sln", "", "Solution"));
        return defaults;
    }

    public void refreshTemplates() {
        cachedTemplates = null;
        loadTemplatesFromCli();
    }
}