package net.chrizzly.dotnetcore4netbeans.executables;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.chrizzly.dotnetcore4netbeans.options.DotnetCliOptions;

/**
 *
 * @author Chrl
 */
public final class CliExecuter implements Callable<Process> {
    private static File folder;
    private static String appPath;
    private static Logger LOG;
    private static List<String>arguments;

    public CliExecuter() {
        CliExecuter.LOG = Logger.getLogger(CliExecuter.class.getName());
    }

    public static Callable<Process> createProject(File folder, String cliCommand, String projectType, String language, String projectName) {
        CliExecuter.folder = folder;
        CliExecuter.appPath = DotnetCliOptions.getInstance().getDotnetCli();
        CliExecuter.arguments = Arrays.asList(appPath, cliCommand, projectType, "-lang", language, "-n", projectName);
        
        return new CliExecuter();
    } 
    
    @Override
    public Process call() throws Exception {
        ProcessBuilder pb = new ProcessBuilder(arguments);

        LOG.log(Level.INFO, "------------------------------------------");
        LOG.log(Level.INFO, pb.toString());
        LOG.log(Level.INFO, pb.command().toString());
        LOG.log(Level.INFO, appPath);
        LOG.log(Level.INFO, CliExecuter.folder.getAbsolutePath());
//        LOG.log(Level.INFO, String.format("Execute \"%s\" new %s in \"%s\"", appPath, projectName, folder));

        pb.directory(CliExecuter.folder); //NOI18N
        pb.redirectErrorStream(true);

        LOG.log(Level.INFO, pb.directory().getAbsolutePath());
        LOG.log(Level.INFO, "------------------------------------------");

        return pb.start();
    }
}