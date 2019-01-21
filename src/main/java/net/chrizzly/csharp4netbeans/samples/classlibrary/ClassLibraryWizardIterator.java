package net.chrizzly.csharp4netbeans.samples.classlibrary;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.ZipInputStream;
import javax.swing.JComponent;
import javax.swing.event.ChangeListener;
import net.chrizzly.csharp4netbeans.project.csharp.CSharpProjectType;
import net.chrizzly.csharp4netbeans.solution.Sln;
import net.chrizzly.csharp4netbeans.utils.ProjectGenerator;
import net.chrizzly.csharp4netbeans.utils.SlnGenerator;
import org.netbeans.api.project.Project;
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
    @TemplateRegistration(folder = "Project/C#", displayName = "#ClassLibrary_displayName", description = "ClassLibraryDescription.html", iconBase = "net/chrizzly/csharp4netbeans/samples/classlibrary/ClassLibrary.png"),
    @TemplateRegistration(folder = "Project/C#/Windows", displayName = "#ClassLibrary_displayName", description = "ClassLibraryDescription.html", iconBase = "net/chrizzly/csharp4netbeans/samples/classlibrary/ClassLibrary.png")
})
@Messages("ClassLibrary_displayName=Class Library")
public class ClassLibraryWizardIterator implements WizardDescriptor./*Progress*/InstantiatingIterator {

    private int index;
    private WizardDescriptor.Panel[] panels;
    private WizardDescriptor wiz;
    private static File _slnFile;

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

    public Set/*<FileObject>*/ instantiate(/*ProgressHandle handle*/) throws IOException {
        Set<FileObject> resultSet = new LinkedHashSet<FileObject>();
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
                jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
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
                new Object[]{new Integer(index + 1), new Integer(panels.length)});
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

//            ZipInputStream str = new ZipInputStream(source);
//            ZipEntry entry;
//
//            while ((entry = str.getNextEntry()) != null) {
//                if (entry.isDirectory()) {
//                    FileUtil.createFolder(projectRoot, entry.getName());
//                } else {
//                    FileObject fo = FileUtil.createData(projectRoot, entry.getName());
////                    if ("nbproject/project.xml".equals(entry.getName())) {
////                        // Special handling for setting name of Ant-based projects; customize as needed:
////                        filterProjectXML(fo, str, projectRoot.getName());
////                    } else {
//                        writeFile(str, fo);
////                    }
//                }
//            }
    }


    private static void writeFile(ZipInputStream str, FileObject fo) throws IOException {
        OutputStream out = fo.getOutputStream();
        try {
            FileUtil.copy(str, out);
        } finally {
            out.close();
        }
    }

//    private static void filterProjectXML(FileObject fo, ZipInputStream str, String name) throws IOException {
//        try {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            FileUtil.copy(str, baos);
//            Document doc = XMLUtil.parse(new InputSource(new ByteArrayInputStream(baos.toByteArray())), false, false, null, null);
//            NodeList nl = doc.getDocumentElement().getElementsByTagName("name");
//            if (nl != null) {
//                for (int i = 0; i < nl.getLength(); i++) {
//                    Element el = (Element) nl.item(i);
//                    if (el.getParentNode() != null && "data".equals(el.getParentNode().getNodeName())) {
//                        NodeList nl2 = el.getChildNodes();
//                        if (nl2.getLength() > 0) {
//                            nl2.item(0).setNodeValue(name);
//                        }
//                        break;
//                    }
//                }
//            }
//            OutputStream out = fo.getOutputStream();
//            try {
//                XMLUtil.write(doc, out, "UTF-8");
//            } finally {
//                out.close();
//            }
//        } catch (Exception ex) {
//            Exceptions.printStackTrace(ex);
//            writeFile(str, fo);
//        }
//
//    }

}
