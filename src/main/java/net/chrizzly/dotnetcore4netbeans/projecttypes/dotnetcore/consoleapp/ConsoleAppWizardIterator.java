package net.chrizzly.dotnetcore4netbeans.projecttypes.dotnetcore.consoleapp;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import net.chrizzly.dotnetcore4netbeans.project.csharp.CSharpProjectType;
import net.chrizzly.dotnetcore4netbeans.solution.Sln;
import net.chrizzly.dotnetcore4netbeans.utils.ProjectGenerator;
import net.chrizzly.dotnetcore4netbeans.utils.SlnGenerator;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.api.templates.TemplateRegistration;
import org.netbeans.api.templates.TemplateRegistrations;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.netbeans.spi.project.ui.templates.support.Templates;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

// TODO define position attribute
@TemplateRegistrations({
    @TemplateRegistration(folder = "Project/.NET/.NET Core", displayName = "#ConsoleApp_displayName", description = "ConsoleAppDescription.html", iconBase = "net/chrizzly/dotnetcore4netbeans/projecttypes/dotnetcore/consoleapp/console-16.png"),
    @TemplateRegistration(folder = "Project/.NET", displayName = "#ConsoleApp_displayName", description = "ConsoleAppDescription.html", iconBase = "net/chrizzly/dotnetcore4netbeans/projecttypes/dotnetcore/consoleapp/console-16.png"),
})
@Messages("ConsoleApp_displayName=Console App (.NET Core)")
public class ConsoleAppWizardIterator implements WizardDescriptor./*Progress*/InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;
    private static File _slnFile;

    public ConsoleAppWizardIterator() {
    }

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
    public Set/*<FileObject>*/ instantiate(/*ProgressHandle handle*/) throws IOException {
        Set<FileObject> resultSet = new LinkedHashSet<>();
        Map<String, Object> properties = wiz.getProperties();
        
        File dirF = FileUtil.normalizeFile((File) wiz.getProperty("projdir"));
        dirF.mkdirs();

        FileObject template = Templates.getTemplate(wiz);
        FileObject slnDir = FileUtil.toFileObject(dirF);
        createProjectFiles(template.getInputStream(), slnDir, wiz);

        // Always open top dir as a project:
        resultSet.add(slnDir);
        // Look for nested projects to open as well:
        Enumeration<? extends FileObject> e = slnDir.getFolders(true);
        while (e.hasMoreElements()) {
            FileObject subfolder = e.nextElement();
            if (ProjectManager.getDefault().isProject(subfolder)) {
                resultSet.add(subfolder);
            }
        }

        File parent = dirF.getParentFile();
        if (parent != null && parent.exists()) {
            ProjectChooser.setProjectsFolder(parent);
        }

        return resultSet;
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

    private static void createProjectFiles(InputStream source, FileObject projectRoot, WizardDescriptor wiz) throws IOException {
        try {
            Map<String, Object> properties = wiz.getProperties();
            createSln(projectRoot);
            createProj(wiz, projectRoot);
        } finally {
            source.close();
        }
    }

    private static void createSln(FileObject projectRoot) throws IOException {
        // Create Sln folder and file.
        Sln sln = new Sln();

        sln.setSlnName(projectRoot.getName());
        sln.setSlnPath(projectRoot.getPath());
        
        SlnGenerator slnGenerator = new SlnGenerator(sln);
        slnGenerator.createSlnFile();
        
        _slnFile = sln.getSlnFile();
    }

    private static void createProj(WizardDescriptor wiz, FileObject projectRoot) {
        // Create proj folder and file.
        CSharpProjectType proj = new CSharpProjectType();

        proj.setProjName(wiz.getProperty("name").toString());
        proj.setSlnPath(projectRoot.getPath());

        ProjectGenerator projGenerator = new ProjectGenerator(proj);
        projGenerator.createProjFolder();
        try {
            projGenerator.addProjectSettingsToSln(_slnFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}