package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.projecttypes.dotnetstandard.classlib;

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
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

// DEPRECATED: This wizard is replaced by UniversalDotNetWizardIterator
// Keeping only the legacy location for backward compatibility
@TemplateRegistrations({
    @TemplateRegistration(folder = "Project/.NET/.NET Standard", displayName = "#ClassLibrary_displayName", description = "ClassLibraryDescription.html", iconBase = "io/github/chris2011/netbeans/plugins/dotnetcore4netbeans/projecttypes/dotnetstandard/classlib/dll-16.png", position = 200)
})
@Messages("ClassLibrary_displayName=Class Library")
public class ClassLibraryWizardIterator implements WizardDescriptor./*Progress*/InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;

    public ClassLibraryWizardIterator() {
    }

    public static ClassLibraryWizardIterator createIterator() {
        return new ClassLibraryWizardIterator();
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new ClassLibraryWizardPanel(),};
    }

    private String[] createSteps() {
        return new String[]{
            NbBundle.getMessage(ClassLibraryWizardIterator.class, "LBL_CreateProjectStep")
        };
    }

    @Override
    public Set<FileObject> instantiate() throws IOException {
        Runnable runnable = createDotNetCliClassLibrary();

        // execute async in separate thread
        RequestProcessor.getDefault().post(runnable);

        return Collections.emptySet();
    }

    public void initialize(WizardDescriptor wiz) {
        this.wiz = wiz;
        index = 0;
        panels = createPanels();
        // Make sure list of steps is accurate.
        String[] steps = createSteps();
        for (int i = 0; i < panels.length; i++) {
            Component c = panels[i].getComponent();
            if (steps[i] == null) {
                // Default step name to component name of panel.
                // Mainly useful for getting the name of the target
                // chooser to appear in the list of steps.
                steps[i] = c.getName();
            }
            if (c instanceof JComponent) { // assume Swing components
                JComponent jc = (JComponent) c;
                // Step #.
                // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                jc.putClientProperty("WizardPanel_contentSelectedIndex", i);
                // Step name (actually the whole list for reference).
                jc.putClientProperty("WizardPanel_contentData", steps);
            }
        }
    }

    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir", null);
        this.wiz.putProperty("name", null);
        this.wiz = null;
        panels = null;
    }

    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[]{index + 1, panels.length});
    }

    public boolean hasNext() {
        return index < panels.length - 1;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public void nextPanel() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        index++;
    }

    public void previousPanel() {
        if (!hasPrevious()) {
            throw new NoSuchElementException();
        }
        index--;
    }

    public WizardDescriptor.Panel current() {
        return panels[index];
    }

    // If nothing unusual changes in the middle of the wizard, simply:
    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }

    private Runnable createDotNetCliClassLibrary() {
        // Get project directory and name from existing properties
        final File projdir = (File) wiz.getProperty("projdir");
        final String name = (String) wiz.getProperty("name");

        // Use sensible defaults for CLI creation
        final File solutionDir = projdir;
        final String solutionName = name != null ? name : "ClassLibrary";
        final String projectName = name != null ? name : "ClassLibrary";
        final Boolean inSameDir = Boolean.FALSE; // Default to separate directories

        return () -> {
            final ProgressHandle ph = ProgressHandle.createHandle("Creating class library via .NET Core CLI...");

            try {
                ph.start();

                File normalizedFilePath = FileUtil.normalizeFile(solutionDir);

                ExecutionDescriptor descriptor = new ExecutionDescriptor()
                        .controllable(true)
                        .frontWindow(true)
                        // disable rerun
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
                        // we handle the progress ourself
                        .showProgress(false);

                // integrate as subtask in the same progress bar
                ph.progress(String.format("Executing 'dotnet new classlib %s'", projectName));

                ExecutionService createProjectService = ExecutionService.newService(CliExecuter.createProjectWithSln(solutionDir, inSameDir, "new", "classlib", "C#", solutionName, projectName), descriptor, String.format("Executing 'dotnet new classlib %s'", projectName));
                Integer exitCode = null;

                // this will run the process
                Future<Integer> projectFuture = createProjectService.run();
                try {
                    // wait for end of execution of shell command
                    exitCode = projectFuture.get();
                } catch (InterruptedException | ExecutionException ex) {
                    NotificationDisplayer.getDefault().notify(".NET Core CLI execution was aborted", NotificationDisplayer.Priority.HIGH.getIcon(), String.format("The execution of 'dotnet new classlib %s' was aborted. Please see the IDE Log.", projectName), null);

                    return;
                } catch (CancellationException ex) {
                    NotificationDisplayer.getDefault().notify(".NET Core CLI execution was canceled", NotificationDisplayer.Priority.HIGH.getIcon(), String.format("The execution of 'dotnet new classlib %s' was canceled by the user.", projectName), null);

                    return;
                }

                if (exitCode != null) {
                    if (exitCode != 0) {
                        NotificationDisplayer.getDefault().notify(".NET Core CLI execution was aborted", NotificationDisplayer.Priority.HIGH.getIcon(), String.format("The execution of 'dotnet new classlib %s' was aborted. Please see the IDE Log.", projectName), null);

                        return;
                    }

                    if (exitCode == 0) {
                        NotificationDisplayer.getDefault().notify(String.format("Project %s was successfully created", projectName), NotificationDisplayer.Priority.NORMAL.getIcon(), String.format("%s created successfully.", projectName), null);

                        ph.progress("Opening project");

                        FileObject dir = FileUtil.toFileObject(solutionDir);
                        dir.refresh();
                        // TODO show error and abort if generation failed (f.e. missing package.json whatever)

                        Project p = FileOwnerQuery.getOwner(dir);

                        if (null != p) {
                            OpenProjects.getDefault().open(new Project[]{p}, true, true);

                            return;
                        }

                        // TODO show error and abort if no project found (can happen when JS plugins are disabled)
                        System.out.println("smth");
                    }
                }
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                ph.finish();
            }
        };
    }
}
