package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.projecttypes.universal;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.netbeans.spi.project.ui.support.ProjectChooser;
import org.openide.WizardDescriptor;
import org.openide.filesystems.FileUtil;
import org.openide.util.HelpCtx;

/**
 * Second wizard panel for configuring project details.
 */
public class ProjectConfigurationWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    private ProjectConfigurationVisualPanel component;
    private WizardDescriptor wizard;

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new ProjectConfigurationVisualPanel(this);
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
     * Visual panel for project configuration.
     */
    static class ProjectConfigurationVisualPanel extends JPanel {

        private final ProjectConfigurationWizardPanel wizardPanel;
        private JTextField projectNameField;
        private JComboBox<String> languageCombo;
        private DotNetTemplate selectedTemplate;
        private javax.swing.event.ChangeListener changeListener;

        public ProjectConfigurationVisualPanel(ProjectConfigurationWizardPanel wizardPanel) {
            this.wizardPanel = wizardPanel;
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            // Project Name
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(10, 10, 5, 5);
            add(new JLabel("Project Name:"), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(10, 5, 5, 10);
            projectNameField = new JTextField("MyProject", 20);
            projectNameField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { fireChange(); }
                @Override
                public void removeUpdate(DocumentEvent e) { fireChange(); }
                @Override
                public void changedUpdate(DocumentEvent e) { fireChange(); }
            });
            add(projectNameField, gbc);

            // Language
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0.0;
            gbc.insets = new Insets(5, 10, 5, 5);
            add(new JLabel("Language:"), gbc);

            gbc.gridx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(5, 5, 5, 10);
            languageCombo = new JComboBox<>(new String[]{"C#"});
            languageCombo.addActionListener(e -> fireChange());
            add(languageCombo, gbc);

            // Template info (read-only display)
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(20, 10, 10, 10);
            add(new JLabel("<html><body><i>Template and language options will be updated based on your selection from the previous step.</i></body></html>"), gbc);
        }

        private void fireChange() {
            if (changeListener != null) {
                changeListener.stateChanged(new javax.swing.event.ChangeEvent(this));
            }
        }

        public boolean isValid() {
            String projectName = projectNameField.getText().trim();
            return !projectName.isEmpty() && isValidProjectName(projectName);
        }

        private boolean isValidProjectName(String name) {
            // Basic validation - no invalid file system characters
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
            // Update UI based on selected template
            selectedTemplate = (DotNetTemplate) wizard.getProperty("projectTemplate");
            if (selectedTemplate != null) {
                // Update language options
                List<String> languages = selectedTemplate.getLanguages();
                languageCombo.removeAllItems();
                for (String lang : languages) {
                    languageCombo.addItem(lang);
                }

                // Set default project name based on template
                String templateName = selectedTemplate.getDisplayName();
                if (templateName.contains("Console")) {
                    projectNameField.setText("ConsoleApp");
                } else if (templateName.contains("Web") || templateName.contains("API")) {
                    projectNameField.setText("WebApp");
                } else if (templateName.contains("Library") || templateName.contains("Lib")) {
                    projectNameField.setText("ClassLibrary");
                } else if (templateName.contains("Test")) {
                    projectNameField.setText("TestProject");
                } else {
                    projectNameField.setText("MyProject");
                }

                projectNameField.selectAll();
            }

            // Set from previous wizard values if available
            String existingProjectName = (String) wizard.getProperty("projectName");
            if (existingProjectName != null && !existingProjectName.trim().isEmpty()) {
                projectNameField.setText(existingProjectName);
            }

            String existingLanguage = (String) wizard.getProperty("language");
            if (existingLanguage != null && languageCombo.getItemCount() > 0) {
                for (int i = 0; i < languageCombo.getItemCount(); i++) {
                    if (existingLanguage.equals(languageCombo.getItemAt(i))) {
                        languageCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        public void storeSettings(WizardDescriptor wizard) {
            wizard.putProperty("projectName", projectNameField.getText().trim());
            wizard.putProperty("language", (String) languageCombo.getSelectedItem());
        }

        @Override
        public String getName() {
            return "Configure Project";
        }
    }
}