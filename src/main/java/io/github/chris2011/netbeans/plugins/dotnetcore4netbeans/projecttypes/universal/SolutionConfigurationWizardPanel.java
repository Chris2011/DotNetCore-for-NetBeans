package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.projecttypes.universal;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;

/**
 * Third wizard panel for configuring solution details.
 */
public class SolutionConfigurationWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    private SolutionConfigurationVisualPanel component;
    private WizardDescriptor wizard;

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new SolutionConfigurationVisualPanel(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isValid() {
        return component != null && component.isValid();
    }

    @Override
    public void addChangeListener(javax.swing.event.ChangeListener l) {
        this.changeListener = l;
        if (component != null) {
            component.addChangeListener(l);
        }
    }

    @Override
    public void removeChangeListener(javax.swing.event.ChangeListener l) {
        if (this.changeListener == l) {
            this.changeListener = null;
        }
        if (component != null) {
            component.removeChangeListener(l);
        }
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wizard = wiz;
        if (component != null) {
            component.readSettings(wiz);
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        if (component != null) {
            component.storeSettings(wiz);
        }
    }

    void fireChangeEvent() {
        // Notify wizard that validation state changed
        if (changeListener != null) {
            changeListener.stateChanged(new javax.swing.event.ChangeEvent(this));
        }
    }

    private javax.swing.event.ChangeListener changeListener;

    /**
     * Visual panel for solution configuration.
     */
    static class SolutionConfigurationVisualPanel extends JPanel {

        private final SolutionConfigurationWizardPanel wizardPanel;
        private JRadioButton createSolutionWithProjectRadio;
        private JRadioButton createSolutionOnlyRadio;
        private JRadioButton addToExistingSolutionRadio;
        private JTextField solutionNameField;
        private JTextField solutionDirectoryField;
        private JButton browseButton;
        private JCheckBox sameDirectoryCheck;
        private JTextField existingSolutionField;
        private JButton browseSolutionButton;
        private DotNetTemplate selectedTemplate;
        private String projectName;
        private javax.swing.event.ChangeListener changeListener;

        public SolutionConfigurationVisualPanel(SolutionConfigurationWizardPanel wizardPanel) {
            this.wizardPanel = wizardPanel;
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            // Solution mode selection
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 3;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 10, 5, 10);
            add(new JLabel("<html><b>Choose how to create your project:</b></html>"), gbc);

            ButtonGroup modeGroup = new ButtonGroup();

            gbc.gridy = 1;
            gbc.insets = new Insets(5, 20, 5, 10);
            createSolutionWithProjectRadio = new JRadioButton("Create new solution with project", true);
            createSolutionWithProjectRadio.addActionListener(this::modeChanged);
            modeGroup.add(createSolutionWithProjectRadio);
            add(createSolutionWithProjectRadio, gbc);

            gbc.gridy = 2;
            createSolutionOnlyRadio = new JRadioButton("Create empty solution only");
            createSolutionOnlyRadio.addActionListener(this::modeChanged);
            modeGroup.add(createSolutionOnlyRadio);
            add(createSolutionOnlyRadio, gbc);

            gbc.gridy = 3;
            addToExistingSolutionRadio = new JRadioButton("Add project to existing solution");
            addToExistingSolutionRadio.addActionListener(this::modeChanged);
            modeGroup.add(addToExistingSolutionRadio);
            add(addToExistingSolutionRadio, gbc);

            // Solution name
            gbc.gridwidth = 1;
            gbc.gridy = 4;
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(20, 10, 5, 5);
            add(new JLabel("Solution Name:"), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(20, 5, 5, 5);
            solutionNameField = new JTextField("MySolution", 20);
            solutionNameField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { fireChange(); }
                @Override
                public void removeUpdate(DocumentEvent e) { fireChange(); }
                @Override
                public void changedUpdate(DocumentEvent e) { fireChange(); }
            });
            add(solutionNameField, gbc);

            // Solution directory
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            gbc.insets = new Insets(5, 10, 5, 5);
            add(new JLabel("Solution Directory:"), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(5, 5, 5, 5);
            solutionDirectoryField = new JTextField(getDefaultSolutionDirectory(), 20);
            solutionDirectoryField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { fireChange(); }
                @Override
                public void removeUpdate(DocumentEvent e) { fireChange(); }
                @Override
                public void changedUpdate(DocumentEvent e) { fireChange(); }
            });
            add(solutionDirectoryField, gbc);

            gbc.gridx = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            gbc.insets = new Insets(5, 5, 5, 10);
            browseButton = new JButton("Browse...");
            browseButton.addActionListener(this::browseForDirectory);
            add(browseButton, gbc);

            // Same directory option
            gbc.gridx = 0;
            gbc.gridy = 6;
            gbc.gridwidth = 3;
            gbc.insets = new Insets(10, 10, 5, 10);
            sameDirectoryCheck = new JCheckBox("Place solution and project in same directory");
            sameDirectoryCheck.addActionListener(e -> fireChange());
            add(sameDirectoryCheck, gbc);

            // Existing solution field (initially hidden)
            gbc.gridwidth = 1;
            gbc.gridy = 7;
            gbc.gridx = 0;
            gbc.insets = new Insets(20, 10, 5, 5);
            add(new JLabel("Existing Solution:"), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(20, 5, 5, 5);
            existingSolutionField = new JTextField(20);
            existingSolutionField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { fireChange(); }
                @Override
                public void removeUpdate(DocumentEvent e) { fireChange(); }
                @Override
                public void changedUpdate(DocumentEvent e) { fireChange(); }
            });
            add(existingSolutionField, gbc);

            gbc.gridx = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            gbc.insets = new Insets(20, 5, 5, 10);
            browseSolutionButton = new JButton("Browse...");
            browseSolutionButton.addActionListener(this::browseForSolution);
            add(browseSolutionButton, gbc);

            // Initially hide existing solution components
            updateUIForMode();
        }

        private void modeChanged(ActionEvent e) {
            updateUIForMode();
            fireChange();
        }

        private void updateUIForMode() {
            boolean showSolutionFields = !addToExistingSolutionRadio.isSelected();
            boolean showExistingFields = addToExistingSolutionRadio.isSelected();
            boolean showProjectOptions = createSolutionWithProjectRadio.isSelected();

            solutionNameField.setVisible(showSolutionFields);
            solutionDirectoryField.setVisible(showSolutionFields);
            browseButton.setVisible(showSolutionFields);
            sameDirectoryCheck.setVisible(showProjectOptions);

            existingSolutionField.setVisible(showExistingFields);
            browseSolutionButton.setVisible(showExistingFields);

            // Update solution name based on project name
            if (showSolutionFields && projectName != null && !projectName.trim().isEmpty()) {
                if (createSolutionOnlyRadio.isSelected()) {
                    solutionNameField.setText(projectName + "Solution");
                } else {
                    solutionNameField.setText(projectName);
                }
            }

            // Update labels
            getComponent(0).setVisible(!showExistingFields);  // Solution name label
            getComponent(4).setVisible(!showExistingFields);  // Solution directory label
            getComponent(10).setVisible(showExistingFields);  // Existing solution label

            revalidate();
            repaint();
        }

        private void browseForDirectory(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setCurrentDirectory(new File(solutionDirectoryField.getText()));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                solutionDirectoryField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private void browseForSolution(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Solution Files", "sln"));

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                existingSolutionField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }

        private String getDefaultSolutionDirectory() {
            File projectsFolder = ProjectChooser.getProjectsFolder();
            return projectsFolder.getAbsolutePath();
        }

        private void fireChange() {
            if (changeListener != null) {
                changeListener.stateChanged(new javax.swing.event.ChangeEvent(this));
            }
        }

        public boolean isValid() {
            try {
                if (addToExistingSolutionRadio.isSelected()) {
                    String solutionPath = existingSolutionField.getText().trim();
                    if (solutionPath.isEmpty()) {
                        return false;
                    }

                    File solutionFile = new File(solutionPath);
                    return solutionFile.exists() && solutionFile.isFile() &&
                           solutionFile.getName().toLowerCase().endsWith(".sln");
                } else {
                    String solutionName = solutionNameField.getText().trim();
                    String solutionDir = solutionDirectoryField.getText().trim();

                    if (solutionName.isEmpty() || solutionDir.isEmpty()) {
                        return false;
                    }

                    if (!isValidFileName(solutionName)) {
                        return false;
                    }

                    File directory = new File(solutionDir);
                    return directory.exists() && directory.isDirectory() && directory.canWrite();
                }
            } catch (Exception e) {
                // Handle any file system exceptions gracefully
                return false;
            }
        }

        private boolean isValidFileName(String name) {
            if (name.isEmpty()) return false;
            String invalidChars = "<>:\"/\\|?*";
            for (char c : invalidChars.toCharArray()) {
                if (name.indexOf(c) >= 0) return false;
            }
            return true;
        }

        public void addChangeListener(javax.swing.event.ChangeListener l) {
            this.changeListener = l;
        }

        public void removeChangeListener(javax.swing.event.ChangeListener l) {
            if (this.changeListener == l) {
                this.changeListener = null;
            }
        }

        public void readSettings(WizardDescriptor wizard) {
            // Update based on selected template and project name
            selectedTemplate = (DotNetTemplate) wizard.getProperty("projectTemplate");
            projectName = (String) wizard.getProperty("projectName");

            if (selectedTemplate != null) {
                // Hide solution creation for solution templates
                if ("sln".equals(selectedTemplate.getShortName()) || "solution".equals(selectedTemplate.getShortName())) {
                    createSolutionOnlyRadio.setSelected(true);
                    createSolutionWithProjectRadio.setVisible(false);
                    addToExistingSolutionRadio.setVisible(false);
                }
            }

            // Update solution name based on project name
            if (projectName != null && !projectName.trim().isEmpty()) {
                if (createSolutionOnlyRadio.isSelected()) {
                    solutionNameField.setText(projectName + "Solution");
                } else {
                    solutionNameField.setText(projectName);
                }
            }

            updateUIForMode();

            // Restore previous settings if available
            String existingSolutionMode = (String) wizard.getProperty("solutionMode");
            if (existingSolutionMode != null) {
                switch (existingSolutionMode) {
                    case "solutionOnly":
                        createSolutionOnlyRadio.setSelected(true);
                        break;
                    case "addToExisting":
                        addToExistingSolutionRadio.setSelected(true);
                        break;
                    default:
                        createSolutionWithProjectRadio.setSelected(true);
                        break;
                }
                updateUIForMode();
            }

            String existingSolutionName = (String) wizard.getProperty("solutionName");
            if (existingSolutionName != null) {
                solutionNameField.setText(existingSolutionName);
            }

            File existingSolutionDir = (File) wizard.getProperty("solutionDir");
            if (existingSolutionDir != null) {
                solutionDirectoryField.setText(existingSolutionDir.getAbsolutePath());
            }

            Boolean existingSameDir = (Boolean) wizard.getProperty("sameDir");
            if (existingSameDir != null) {
                sameDirectoryCheck.setSelected(existingSameDir);
            }
        }

        public void storeSettings(WizardDescriptor wizard) {
            if (createSolutionOnlyRadio.isSelected()) {
                wizard.putProperty("solutionMode", "solutionOnly");
            } else if (addToExistingSolutionRadio.isSelected()) {
                wizard.putProperty("solutionMode", "addToExisting");
            } else {
                wizard.putProperty("solutionMode", "projectWithSolution");
            }

            wizard.putProperty("solutionName", solutionNameField.getText().trim());
            wizard.putProperty("sameDir", sameDirectoryCheck.isSelected());

            if (addToExistingSolutionRadio.isSelected()) {
                File solutionFile = new File(existingSolutionField.getText().trim());
                wizard.putProperty("solutionDir", solutionFile.getParentFile());
                wizard.putProperty("solutionName", solutionFile.getName().replace(".sln", ""));
            } else {
                File solutionDir = FileUtil.normalizeFile(new File(solutionDirectoryField.getText().trim()));
                wizard.putProperty("solutionDir", solutionDir);
            }
        }

        @Override
        public String getName() {
            return "Configure Solution";
        }
    }
}