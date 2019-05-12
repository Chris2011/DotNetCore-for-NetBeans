package net.chrizzly.dotnetcore4netbeans.executables;

import com.google.common.base.Joiner;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import net.chrizzly.dotnetcore4netbeans.options.DotnetCliOptions;

/**
 *
 * @author Chrl
 */
public final class CliExecuter implements Callable<Process> {
    private static File folder;
//    private static String appPath;
    private static Logger LOG;
    private static List<String> commands = new ArrayList();
    private static final String dotnetCli = DotnetCliOptions.getInstance().getDotnetCli();

    public CliExecuter() {
        CliExecuter.LOG = Logger.getLogger(CliExecuter.class.getName());
    }

//    public static Callable<Process> createSlnFile(File folder, String cliCommand, String projectName) {
//        CliExecuter.folder = folder;
//        CliExecuter.commands = Arrays.asList(dotnetCli, cliCommand, "sln", "-n", projectName);
//        
//        return new CliExecuter();
//    }
    public static Callable<Process> createProjectWithSln(File solutionDirectory, boolean togetherInSameFolder, String cliCommand, String projectType, String language, String slnName, String projectName) {
        if (!solutionDirectory.exists()) {
            solutionDirectory.mkdir();
        }

        CliExecuter.folder = solutionDirectory;
        CliExecuter.commands.add("cmd"); // TODO: Check for Windows/Linux/Mac
        CliExecuter.commands.add("/C");

        List<String> chainedCommands = new ArrayList<>();
        chainedCommands.addAll(Arrays.asList("\"" + dotnetCli + "\"", cliCommand, "sln", "-n", slnName)); // C:\Program Files\dotnet\dotnet.exe new sln -n projectName
        chainedCommands.add("&&");
        
        if (togetherInSameFolder) {
            chainedCommands.addAll(Arrays.asList("\"" + dotnetCli + "\"", cliCommand, projectType, "-lang", language, "-n", projectName, "-o", solutionDirectory.getAbsolutePath())); // C:\Program Files\dotnet\dotnet.exe new console -lang C# -n projectName -o solutionDirectory
            chainedCommands.add("&&");
            chainedCommands.addAll(Arrays.asList("\"" + dotnetCli + "\"", "sln", slnName + ".sln", "add", projectName + ".csproj")); // C:\Program Files\dotnet\dotnet.exe new console -lang C# -n projectName
        } else {
            chainedCommands.addAll(Arrays.asList("\"" + dotnetCli + "\"", cliCommand, projectType, "-lang", language, "-n", projectName)); // C:\Program Files\dotnet\dotnet.exe new console -lang C# -n projectName
            chainedCommands.add("&&");
            chainedCommands.addAll(Arrays.asList("\"" + dotnetCli + "\"", "sln", slnName + ".sln", "add", solutionDirectory.getAbsolutePath() + File.separatorChar + projectName + File.separatorChar + projectName + ".csproj")); // C:\Program Files\dotnet\dotnet.exe new console -lang C# -n projectName
        }

        CliExecuter.commands.add("\"" + Joiner.on(" ").join(chainedCommands) + "\"");

        return new CliExecuter();
    }

    @Override
    public Process call() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(CliExecuter.commands);

        LOG.log(Level.INFO, "------------------------------------------");
        LOG.log(Level.INFO, processBuilder.toString());
        LOG.log(Level.INFO, processBuilder.command().toString());
        LOG.log(Level.INFO, dotnetCli);
        LOG.log(Level.INFO, CliExecuter.folder.getAbsolutePath());

        processBuilder.directory(CliExecuter.folder); //NOI18N
        processBuilder.redirectErrorStream(true);

        LOG.log(Level.INFO, processBuilder.directory().getAbsolutePath());
        LOG.log(Level.INFO, "------------------------------------------");

        return processBuilder.start();
    }
}
