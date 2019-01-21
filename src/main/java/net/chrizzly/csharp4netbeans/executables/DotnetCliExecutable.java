package net.chrizzly.csharp4netbeans.executables;

import java.awt.EventQueue;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;
import net.chrizzly.csharp4netbeans.options.DotnetCliOptions;
import net.chrizzly.csharp4netbeans.options.FileUtils;
import net.chrizzly.csharp4netbeans.options.StringUtils;
import net.chrizzly.csharp4netbeans.ui.options.DotnetCliOptionsPanelController;
import net.chrizzly.csharp4netbeans.validator.DotnetCliOptionsValidator;
import net.chrizzly.csharp4netbeans.validator.ValidationResult;
import org.netbeans.api.annotations.common.CheckForNull;
import org.netbeans.api.annotations.common.NullAllowed;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.options.OptionsDisplayer;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;


public class DotnetCliExecutable {

    public static final String DOTNET_CLI_NAME;

    protected final Project project;
    protected final String dotnetCliPath;


    static {
        if (Utilities.isWindows()) {
            DOTNET_CLI_NAME = "dotnet.exe"; // NOI18N
        } else {
            DOTNET_CLI_NAME = "dotnet"; // NOI18N
        }
    }

    DotnetCliExecutable(String dotnetCliPath, @NullAllowed Project project) {
        assert dotnetCliPath != null;

        this.dotnetCliPath = dotnetCliPath;
        this.project = project;
    }

    @CheckForNull
    public static DotnetCliExecutable getDefault(@NullAllowed Project project, boolean showOptions) {
        ValidationResult result = new DotnetCliOptionsValidator()
                .validateDotnetCli()
                .getResult();
        if (validateResult(result) != null) {
            if (showOptions) {
                OptionsDisplayer.getDefault().open(DotnetCliOptionsPanelController.OPTIONS_PATH);
            }

            return null;
        }

        return createExecutable(DotnetCliOptions.getInstance().getDotnetCli(), project);
    }

    private static DotnetCliExecutable createExecutable(String dotnetCli, Project project) {
        if (Utilities.isMac()) {
            return new DotnetCliExecutable.MacDotnetCliExecutable(dotnetCli, project);
        }
        return new DotnetCliExecutable(dotnetCli, project);
    }

    String getCommand() {
        return dotnetCliPath;
    }

    @NbBundle.Messages({
        "# {0} - project name",
        "DotnetCliExecutable.generate=.NET Core CLI ({0})",
    })
    public Future<Integer> generate(FileObject target, boolean less) {
        assert !EventQueue.isDispatchThread();
        assert project != null;

        String projectName = ProjectUtils.getInformation(project).getDisplayName();
        Future<Integer> task = getExecutable(Bundle.DotnetCliExecutable_generate(projectName))
                .additionalParameters(getGenerateParams(target, less))
                .run(getDescriptor());

        assert task != null : dotnetCliPath;
        return task;
    }

    private ExternalExecutable getExecutable(String title) {
        assert title != null;
        return new ExternalExecutable(getCommand())
                .workDir(getWorkDir())
                .displayName(title)
                .optionsPath(DotnetCliOptionsPanelController.OPTIONS_PATH)
                .noOutput(false);
    }

    private ExecutionDescriptor getDescriptor() {
        assert project != null;
        return ExternalExecutable.DEFAULT_EXECUTION_DESCRIPTOR
                .showSuspended(true)
                .optionsPath(DotnetCliOptionsPanelController.OPTIONS_PATH)
                .outLineBased(true)
                .errLineBased(true)
                .postExecution(() -> project.getProjectDirectory().refresh());
    }

    private File getWorkDir() {
        if (project == null) {
            return FileUtils.TMP_DIR;
        }

        File workDir = FileUtil.toFile(project.getProjectDirectory());

        assert workDir != null : project.getProjectDirectory();

        return workDir;
    }

    private List<String> getGenerateParams(FileObject target, boolean less) {
        List<String> params = new ArrayList<>(3);
//        params.add(FORCE_PARAM);
        params.add(FileUtil.toFile(target).getAbsolutePath());
        return getParams(params);
    }

    List<String> getParams(List<String> params) {
        assert params != null;
        return params;
    }

    @CheckForNull
    private static String validateResult(ValidationResult result) {
        if (result.isFaultless()) {
            return null;
        }
        if (result.hasErrors()) {
            return result.getFirstErrorMessage();
        }
        return result.getFirstWarningMessage();
    }

    //~ Inner classes

    private static final class MacDotnetCliExecutable extends DotnetCliExecutable {

        private static final String BASH_COMMAND = "/bin/bash -lc"; // NOI18N


        MacDotnetCliExecutable(String dotnetCliPath, Project project) {
            super(dotnetCliPath, project);
        }

        @Override
        String getCommand() {
            return BASH_COMMAND;
        }

        @Override
        List<String> getParams(List<String> params) {
            StringBuilder sb = new StringBuilder(200);
            sb.append("\""); // NOI18N
            sb.append(dotnetCliPath);
            sb.append("\" \""); // NOI18N
            sb.append(StringUtils.implode(super.getParams(params), "\" \"")); // NOI18N
            sb.append("\""); // NOI18N
            return Collections.singletonList(sb.toString());
        }

    }

}
