package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.projecttypes.universal;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.executables.CliExecuter;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.openide.WizardDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

/**
 * Universal wizard for creating any .NET project type using the .NET CLI.
 */
@TemplateRegistrations({
    @TemplateRegistration(
        folder = "Project/.NET",
        displayName = "#UniversalDotNet_displayName",
        description = "UniversalDotNetDescription.html",
        iconBase = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/projecttypes/dotnetcore/consoleapp/console-16.png",
        position = 100
    )
})
@Messages("UniversalDotNet_displayName=.NET Project")
public class UniversalDotNetWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wizard;

    @Override
    public Set<FileObject> instantiate() throws IOException {
        Runnable runnable = createDotNetProject();
        RequestProcessor.getDefault().post(runnable);
        return Collections.emptySet();
    }

    @Override
    public void initialize(WizardDescriptor wizard) {
        this.wizard = wizard;
        index = 0;
        panels = createPanels();

        // Setup wizard steps
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.putClientProperty("WizardPanel_contentSelectedIndex", i);
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }

    @Override
    public void uninitialize(WizardDescriptor wizard) {
        this.wizard.putProperty("projectTemplate", null);
        this.wizard.putProperty("projectName", null);
        this.wizard.putProperty("solutionName", null);
        this.wizard.putProperty("solutionDir", null);
        this.wizard.putProperty("solutionMode", null);
        this.wizard = null;
        panels = null;
    }

    @Override
    public String name() {
        return MessageFormat.format("{0} of {1}", index + 1, panels.length);
    }

    @Override
    public boolean hasNext() {
        return index < panels.length - 1;
    }

    @Override
    public boolean hasPrevious() {
        return index > 0;
    }

    @Override
    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    @Override
    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    @Override
    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    @Override
    public void addChangeListener(ChangeListener l) {
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new TemplateSelectionWizardPanel(),
            new ProjectConfigurationWizardPanel(),
            new SolutionConfigurationWizardPanel()
        };
    }

    private String[] createSteps() {
        return new String[]{
            "Select Project Template",
            "Configure Project",
            "Configure Solution"
        };
    }

    private Runnable createDotNetProject() {
        final DotNetTemplate template = (DotNetTemplate) wizard.getProperty("projectTemplate");
        final String projectName = (String) wizard.getProperty("projectName");
        final String solutionName = (String) wizard.getProperty("solutionName");
        final File solutionDir = (File) wizard.getProperty("solutionDir");
        final String solutionMode = (String) wizard.getProperty("solutionMode");
        final Boolean inSameDir = (Boolean) wizard.getProperty("sameDir");

        return () -> {
            final ProgressHandle ph = ProgressHandle.createHandle("Creating .NET project via CLI...");

            try {
                ph.start();

                ExecutionDescriptor descriptor = new ExecutionDescriptor()
                    .controllable(true)
                    .frontWindow(true)
                    .rerunCondition(new ExecutionDescriptor.RerunCondition() {
                        @Override
                        public void addChangeListener(ChangeListener cl) {
                        }

                        @Override
                        public void removeChangeListener(ChangeListener cl) {
                        }

                        @Override
                        public boolean isRerunPossible() {
                            return false;
                        }
                    })
                    .showProgress(false);

                Integer exitCode = null;

                if ("solutionOnly".equals(solutionMode)) {
                    // Create only solution
                    ph.progress("Creating solution file...");
                    ExecutionService solutionService = ExecutionService.newService(
                        CliExecuter.createSolutionOnly(solutionDir, solutionName),
                        descriptor,
                        "Creating solution"
                    );
                    Future<Integer> solutionFuture = solutionService.run();
                    exitCode = solutionFuture.get();

                } else if ("projectWithSolution".equals(solutionMode)) {
                    // Create project with solution
                    ph.progress(String.format("Creating project '%s' with solution...", projectName));
                    ExecutionService projectService = ExecutionService.newService(
                        CliExecuter.createProjectWithSln(solutionDir, inSameDir != null ? inSameDir : false, "new", template.getShortName(), "C#", solutionName, projectName),
                        descriptor,
                        "Creating project with solution"
                    );
                    Future<Integer> projectFuture = projectService.run();
                    exitCode = projectFuture.get();

                } else if ("addToExisting".equals(solutionMode)) {
                    // Add project to existing solution
                    ph.progress(String.format("Creating project '%s'...", projectName));
                    File projectDir = new File(solutionDir, projectName);
                    ExecutionService projectService = ExecutionService.newService(
                        CliExecuter.createProject(projectDir, template.getShortName(), projectName, "C#"),
                        descriptor,
                        "Creating project"
                    );
                    Future<Integer> projectFuture = projectService.run();
                    exitCode = projectFuture.get();

                    if (exitCode == 0) {
                        ph.progress("Adding project to solution...");
                        String projectPath = new File(projectDir, projectName + ".csproj").getAbsolutePath();
                        ExecutionService addService = ExecutionService.newService(
                            CliExecuter.addProjectToSolution(solutionDir, solutionName + ".sln", projectPath),
                            descriptor,
                            "Adding project to solution"
                        );
                        Future<Integer> addFuture = addService.run();
                        exitCode = addFuture.get();
                    }
                }

                if (exitCode != null) {
                    if (exitCode != 0) {
                        NotificationDisplayer.getDefault().notify(
                            ".NET CLI execution failed",
                            NotificationDisplayer.Priority.HIGH.getIcon(),
                            "The .NET CLI command failed. Please see the IDE log for details.",
                            null
                        );
                        return;
                    }

                    // Success
                    NotificationDisplayer.getDefault().notify(
                        "Project created successfully",
                        NotificationDisplayer.Priority.NORMAL.getIcon(),
                        String.format("Project '%s' was created successfully.", projectName != null ? projectName : solutionName),
                        null
                    );

                    ph.progress("Opening project...");
                    FileObject dir = FileUtil.toFileObject(solutionDir);
                    if (dir != null) {
                        dir.refresh();
                        Project p = FileOwnerQuery.getOwner(dir);
                        if (p != null) {
                            OpenProjects.getDefault().open(new Project[]{p}, true, true);
                        }
                    }
                }

            } catch (InterruptedException | ExecutionException ex) {
                NotificationDisplayer.getDefault().notify(
                    ".NET CLI execution was interrupted",
                    NotificationDisplayer.Priority.HIGH.getIcon(),
                    "The .NET CLI execution was interrupted. Please try again.",
                    null
                );
            } catch (CancellationException ex) {
                NotificationDisplayer.getDefault().notify(
                    ".NET CLI execution was canceled",
                    NotificationDisplayer.Priority.HIGH.getIcon(),
                    "The .NET CLI execution was canceled by the user.",
                    null
                );
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                NotificationDisplayer.getDefault().notify(
                    "Unexpected error",
                    NotificationDisplayer.Priority.HIGH.getIcon(),
                    "An unexpected error occurred during project creation.",
                    null
                );
            } finally {
                ph.finish();
            }
        };
    }
}
