package net.chrizzly.dotnetcore4netbeans.projecttypes.dotnetcore.consoleapp;

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
import net.chrizzly.dotnetcore4netbeans.executables.CliExecuter;
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

// TODO define position attribute
@TemplateRegistrations({
    @TemplateRegistration(folder = "Project/.NET/.NET Core", displayName = "#ConsoleApp_displayName", description = "ConsoleAppDescription.html", iconBase = "net/chrizzly/dotnetcore4netbeans/projecttypes/dotnetcore/consoleapp/console-16.png"),
    @TemplateRegistration(folder = "Project/.NET", displayName = "#ConsoleApp_displayName", description = "ConsoleAppDescription.html", iconBase = "net/chrizzly/dotnetcore4netbeans/projecttypes/dotnetcore/consoleapp/console-16.png"),})
@Messages("ConsoleApp_displayName=Console App (.NET Core)")
public class ConsoleAppWizardIterator implements WizardDescriptor.InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;
//    private static File _slnFile;

//    public ConsoleAppWizardIterator() {
//    }
    public static ConsoleAppWizardIterator createIterator() {
        return new ConsoleAppWizardIterator();
    }

    private WizardDescriptor.Panel[] createPanels() {
        return new WizardDescriptor.Panel[]{
            new ConsoleAppWizardPanel(),};
    }

    private String[] createSteps() {
        return new String[]{
            NbBundle.getMessage(ConsoleAppWizardIterator.class, "LBL_CreateProjectStep")
        };
    }

    @Override
    public Set<FileObject> instantiate() throws IOException {
        Runnable runnable = createDotNetCliApp();

        // execute async in separate thread
        RequestProcessor.getDefault().post(runnable);

        return Collections.emptySet();

//        Set<FileObject> resultSet = new LinkedHashSet<>();
//        Map<String, Object> properties = wiz.getProperties();
//        
//        File dirF = FileUtil.normalizeFile((File) wiz.getProperty("projdir"));
//        dirF.mkdirs();
//
//        FileObject template = Templates.getTemplate(wiz);
//        FileObject slnDir = FileUtil.toFileObject(dirF);
//        createProjectFiles(template.getInputStream(), slnDir, wiz);
//
//        // Always open top dir as a project:
//        resultSet.add(slnDir);
//        // Look for nested projects to open as well:
//        Enumeration<? extends FileObject> e = slnDir.getFolders(true);
//        while (e.hasMoreElements()) {
//            FileObject subfolder = e.nextElement();
//            if (ProjectManager.getDefault().isProject(subfolder)) {
//                resultSet.add(subfolder);
//            }
//        }
//
//        File parent = dirF.getParentFile();
//        if (parent != null && parent.exists()) {
//            ProjectChooser.setProjectsFolder(parent);
//        }
//
//        return resultSet;
    }

    @Override
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

    @Override
    public void uninitialize(WizardDescriptor wiz) {
        this.wiz.putProperty("projdir", null);
        this.wiz.putProperty("name", null);
        this.wiz = null;
        panels = null;
    }

    @Override
    public String name() {
        return MessageFormat.format("{0} of {1}",
                new Object[]{index + 1, panels.length});
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

    // If nothing unusual changes in the middle of the wizard, simply:
    @Override
    public final void addChangeListener(ChangeListener l) {
    }

    @Override
    public final void removeChangeListener(ChangeListener l) {
    }

//    private static void createProjectFiles(InputStream source, FileObject projectRoot, WizardDescriptor wiz) throws IOException {
//        try {
//            // TODO: Call the CLI with the project root and the name.
//            Map<String, Object> properties = wiz.getProperties();
////            createSln(projectRoot);
////            createProj(wiz, projectRoot);
//        } finally {
//            source.close();
//        }
//    }
//    private static void createSln(FileObject projectRoot) throws IOException {
    // Create Sln folder and file.
//        Sln sln = new Sln();
//
//        sln.setSlnName(projectRoot.getName());
//        sln.setSlnPath(projectRoot.getPath());
//        
//        SlnGenerator slnGenerator = new SlnGenerator(sln);
//        slnGenerator.createSlnFile();
//        
//        _slnFile = sln.getSlnFile();
//    }
//    private static void createProj(WizardDescriptor wiz, FileObject projectRoot) {
    // Create proj folder and file.
//        CSharpProjectType proj = new CSharpProjectType();
//
//        proj.setProjName(wiz.getProperty("name").toString());
//        proj.setSlnPath(projectRoot.getPath());
//
//        ProjectGenerator projGenerator = new ProjectGenerator(proj);
//        projGenerator.createProjFolder();
//        try {
//            projGenerator.addProjectSettingsToSln(_slnFile);
//        } catch (IOException ex) {
//            Exceptions.printStackTrace(ex);
//        }
//    }
    private Runnable createDotNetCliApp() {
        final File solutionDir = FileUtil.normalizeFile(new File(((String)wiz.getProperty("solutionDir") + File.separatorChar + (String)wiz.getProperty("solutionName"))));
        final String solutionName = "" + wiz.getProperty("solutionName");
        final String projectName = "" + wiz.getProperty("projectName");
        final Boolean inSameDir = (Boolean)wiz.getProperty("sameDir");

        return () -> {
            final ProgressHandle ph = ProgressHandle.createHandle("Creating project via .NET Core CLI...");

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
                ph.progress(String.format("Executing 'dotnet new %s'", projectName));

//                ExecutionService exeService = ExecutionService.newService(new CliExecuter(parentLocation, projectName, "C#", "console"), descriptor, String.format("Executing 'dotnet new %s'", projectName));
//                ExecutionService createSln = ExecutionService.newService(CliExecuter.createSlnFile(parentLocation, "new", projectName), descriptor, String.format("Executing 'dotnet new %s'", projectName));
                ExecutionService createProjectService = ExecutionService.newService(CliExecuter.createProjectWithSln(solutionDir, inSameDir, "new", "console", "C#", solutionName, projectName), descriptor, String.format("Executing 'dotnet new %s'", projectName));
                Integer exitCode = null;

//                Future<Integer> slnServiceFuture = createSln.run();

                // this will run the process
                Future<Integer> projectFuture = createProjectService.run();
                try {
                    // wait for end of execution of shell command
                    exitCode = projectFuture.get();
//                    exitCode = slnServiceFuture.get();
                } catch (InterruptedException | ExecutionException ex) {
                    NotificationDisplayer.getDefault().notify(".NET Core CLI execution was aborted", NotificationDisplayer.Priority.HIGH.getIcon(), String.format("The execution of 'dotnet new %s' was aborted. Please see the IDE Log.", projectName), null);

                    return;
                } catch (CancellationException ex) {
                    NotificationDisplayer.getDefault().notify(".NET Core CLI execution was canceled", NotificationDisplayer.Priority.HIGH.getIcon(), String.format("The execution of 'dotnet new %s' was canceled by the user.", projectName), null);

                    return;
                }

                if (exitCode != null) {
                    if (exitCode != 0) {
                        NotificationDisplayer.getDefault().notify(".NET Core CLI execution was aborted", NotificationDisplayer.Priority.HIGH.getIcon(), String.format("The execution of 'dotnet new %s' was aborted. Please see the IDE Log.", projectName), null);

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
