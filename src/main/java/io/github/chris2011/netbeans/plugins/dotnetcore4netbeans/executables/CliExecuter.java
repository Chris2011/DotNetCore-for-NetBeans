package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.executables;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.options.DotnetCliOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Chrl
 */
public final class CliExecuter implements Callable<Process> {

    private static final Logger LOG = Logger.getLogger(CliExecuter.class.getName());
    private static final String dotnetCli = DotnetCliOptions.getInstance().getDotnetCli();

    private final File folder;
    private final List<String> commands;

    private CliExecuter(File folder, List<String> commands) {
        this.folder = folder;
        this.commands = new ArrayList<>(commands); // Defensive copy
    }

    public static Callable<Process> createSolutionOnly(File solutionDirectory, String solutionName) {
        if (!solutionDirectory.exists()) {
            solutionDirectory.mkdirs();
        }

        List<String> commands = new ArrayList<>();
        commands.add(dotnetCli);
        commands.addAll(Arrays.asList("new", "sln", "-n", solutionName));
        return new CliExecuter(solutionDirectory, commands);
    }

    public static Callable<Process> createProject(File projectDirectory, String projectTemplate, String projectName, String language) {
        if (!projectDirectory.exists()) {
            projectDirectory.mkdirs();
        }

        List<String> commands = new ArrayList<>();
        commands.add(dotnetCli);
        commands.addAll(Arrays.asList("new", projectTemplate, "-lang", language, "-n", projectName));
        return new CliExecuter(projectDirectory, commands);
    }

    public static Callable<Process> addProjectToSolution(File solutionDirectory, String solutionFileName, String projectPath) {
        List<String> commands = new ArrayList<>();
        commands.add(dotnetCli);
        commands.addAll(Arrays.asList("sln", solutionFileName, "add", projectPath));
        return new CliExecuter(solutionDirectory, commands);
    }

    public static Callable<Process> createProjectWithSln(File solutionDirectory, boolean togetherInSameFolder, String cliCommand, String projectType, String language, String slnName, String projectName) {
        if (!solutionDirectory.exists()) {
            solutionDirectory.mkdirs();
        }

        List<String> commands = new ArrayList<>();
        commands.add("cmd"); // TODO: Check for Windows/Linux/Mac
        commands.add("/C");

        StringBuilder commandBuilder = new StringBuilder();

        // First command: Create solution
        commandBuilder.append("\"").append(dotnetCli).append("\" ").append(cliCommand).append(" sln -n ").append(slnName);
        commandBuilder.append(" && ");

        if (togetherInSameFolder) {
            // Second command: Create project in same folder
            commandBuilder.append("\"").append(dotnetCli).append("\" ").append(cliCommand).append(" ").append(projectType)
                .append(" -lang ").append(language).append(" -n ").append(projectName)
                .append(" -o \"").append(solutionDirectory.getAbsolutePath()).append("\"");
            commandBuilder.append(" && ");
            // Third command: Add project to solution
            commandBuilder.append("\"").append(dotnetCli).append("\" sln ").append(slnName).append(".sln add ").append(projectName).append(".csproj");
        } else {
            // Second command: Create project in subfolder
            commandBuilder.append("\"").append(dotnetCli).append("\" ").append(cliCommand).append(" ").append(projectType)
                .append(" -lang ").append(language).append(" -n ").append(projectName);
            commandBuilder.append(" && ");
            // Third command: Add project to solution
            String projectPath = solutionDirectory.getAbsolutePath() + File.separatorChar + projectName + File.separatorChar + projectName + ".csproj";
            commandBuilder.append("\"").append(dotnetCli).append("\" sln ").append(slnName).append(".sln add \"").append(projectPath).append("\"");
        }

        commands.add(commandBuilder.toString());

        return new CliExecuter(solutionDirectory, commands);
    }

    @Override
    public Process call() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(this.commands);

        LOG.log(Level.INFO, "------------------------------------------");
        LOG.log(Level.INFO, "ProcessBuilder: {0}", processBuilder.toString());
        LOG.log(Level.INFO, "Command: {0}", processBuilder.command().toString());
        LOG.log(Level.INFO, "DotNet CLI: {0}", dotnetCli);
        LOG.log(Level.INFO, "Working Directory: {0}", this.folder.getAbsolutePath());

        processBuilder.directory(this.folder);
        processBuilder.redirectErrorStream(true);

        LOG.log(Level.INFO, "Final Directory: {0}", processBuilder.directory().getAbsolutePath());
        LOG.log(Level.INFO, "------------------------------------------");

        return processBuilder.start();
    }
}
