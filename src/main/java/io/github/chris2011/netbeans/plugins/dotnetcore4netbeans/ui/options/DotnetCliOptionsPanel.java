package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.ui.options;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.executables.DotnetCliExecutable;
import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.options.FileUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

@OptionsPanelController.Keywords(keywords = {"#KW.DotnetCliOptionsPanel"}, location = ".NET Core", tabTitle = ".NET Core CLI")
public final class DotnetCliOptionsPanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(DotnetCliOptionsPanel.class.getName());
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    public DotnetCliOptionsPanel() {
        assert EventQueue.isDispatchThread();

        initComponents();

        init();
    }

    private void init() {
        DocumentListener defaultDocumentListener = new DefaultDocumentListener();
        dotnetCliPathTextField.getDocument().addDocumentListener(defaultDocumentListener);

        // Initialize version label
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setText("Version wird geladen...");

        // Load version when path changes
        updateVersion();
    }

    public static DotnetCliOptionsPanel create() {
        DotnetCliOptionsPanel panel = new DotnetCliOptionsPanel();

        return panel;
    }

    public void addChangeListener(ChangeListener listener) {
        changeSupport.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeSupport.removeChangeListener(listener);
    }

    public String getDotnetCli() {
        return dotnetCliPathTextField.getText();
    }

    public void setDotnetCli(String dotnetCli) {
        dotnetCliPathTextField.setText(dotnetCli);
        updateVersion();
    }

    private void updateVersion() {
        String dotnetPath = getDotnetCli();
        if (dotnetPath == null || dotnetPath.trim().isEmpty()) {
            versionLabel.setText("");
            return;
        }

        // Update version asynchronously with error handling
        CompletableFuture.supplyAsync(() -> getDotnetVersion(dotnetPath))
            .thenAcceptAsync(version -> {
                SwingUtilities.invokeLater(() -> versionLabel.setText(version));
            })
            .exceptionally(throwable -> {
                LOG.log(Level.WARNING, "Failed to update .NET CLI version", throwable);
                SwingUtilities.invokeLater(() -> versionLabel.setText("(Fehler beim Laden)"));
                return null;
            });
    }

    private String getDotnetVersion(String dotnetPath) {
        if (dotnetPath == null || dotnetPath.trim().isEmpty()) {
            return "(Kein Pfad angegeben)";
        }

        try {
            File dotnetFile = new File(dotnetPath.trim());
            if (!dotnetFile.exists()) {
                return "(Datei nicht gefunden)";
            }
            if (!dotnetFile.canExecute()) {
                return "(Datei nicht ausführbar)";
            }

            ProcessBuilder processBuilder = new ProcessBuilder(dotnetPath.trim(), "--version");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            try {
                // Set timeout of 5 seconds for version check
                boolean finished = process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS);

                if (!finished) {
                    process.destroyForcibly();
                    return "(Timeout - keine Antwort)";
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String version = reader.readLine();
                    int exitCode = process.exitValue();

                    if (exitCode == 0 && version != null && !version.trim().isEmpty()) {
                        return "(Version " + version.trim() + ")";
                    } else {
                        return "(Version nicht verfügbar)";
                    }
                }
            } finally {
                // Ensure process is properly cleaned up
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            LOG.log(Level.FINE, "Version check was interrupted for: " + dotnetPath, ex);
            return "(Unterbrochen)";
        } catch (Exception ex) {
            LOG.log(Level.FINE, "Could not determine .NET CLI version for: " + dotnetPath, ex);
            return "(Fehler bei Versionsprüfung)";
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        dotnetCliFolderBrowseButton = new javax.swing.JButton();
        dotnetCliPathTextField = new javax.swing.JTextField();
        dotnetCliPathBrowseButton = new javax.swing.JButton();
        versionLabel = new javax.swing.JLabel();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(DotnetCliOptionsPanel.class, "DotnetCliOptionsPanel.jLabel1.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dotnetCliFolderBrowseButton, org.openide.util.NbBundle.getMessage(DotnetCliOptionsPanel.class, "DotnetCliOptionsPanel.dotnetCliFolderBrowseButton.text")); // NOI18N
        dotnetCliFolderBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dotnetCliFolderBrowseButtonActionPerformed(evt);
            }
        });

        dotnetCliPathTextField.setText(org.openide.util.NbBundle.getMessage(DotnetCliOptionsPanel.class, "DotnetCliOptionsPanel.dotnetCliPathTextField.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(dotnetCliPathBrowseButton, org.openide.util.NbBundle.getMessage(DotnetCliOptionsPanel.class, "DotnetCliOptionsPanel.dotnetCliPathBrowseButton.text")); // NOI18N
        dotnetCliPathBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dotnetCliPathBrowseButtonActionPerformed(evt);
            }
        });

        versionLabel.setForeground(java.awt.Color.GRAY);
        versionLabel.setText(" "); // NOI18N
        versionLabel.setFont(versionLabel.getFont().deriveFont(versionLabel.getFont().getSize() - 1f));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionLabel)
                    .addComponent(dotnetCliPathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dotnetCliFolderBrowseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dotnetCliPathBrowseButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(dotnetCliPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dotnetCliFolderBrowseButton)
                    .addComponent(dotnetCliPathBrowseButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(versionLabel))
        );
    }// </editor-fold>//GEN-END:initComponents

    @NbBundle.Messages("DotnetCliOptionsPanel.browse.title=Select .NET Core CLI")
    private void dotnetCliFolderBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dotnetCliFolderBrowseButtonActionPerformed
        File file = new FileChooserBuilder(DotnetCliOptionsPanel.class)
            .setFilesOnly(true)
            .setTitle(Bundle.DotnetCliOptionsPanel_browse_title())
            .showOpenDialog();
        if (file != null) {
            dotnetCliPathTextField.setText(file.getAbsolutePath());
            updateVersion();
        }
    }//GEN-LAST:event_dotnetCliFolderBrowseButtonActionPerformed

    @NbBundle.Messages("DotnetCliOptionsPanel.executable.notFound=No .NET Core CLI executable found.")
    private void dotnetCliPathBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dotnetCliPathBrowseButtonActionPerformed
        List<String> dotnetCliPaths = FileUtils.findFileOnUsersPath(DotnetCliExecutable.DOTNET_CLI_NAME);

        if (dotnetCliPaths.isEmpty()) {
            StatusDisplayer.getDefault().setStatusText(Bundle.DotnetCliOptionsPanel_executable_notFound());
        } else {
            dotnetCliPathTextField.setText(dotnetCliPaths.get(0));
            updateVersion();
        }
    }//GEN-LAST:event_dotnetCliPathBrowseButtonActionPerformed

//    void load() {
//        String dotnetCli = NbPreferences.forModule(DotnetCliOptionsPanel.class).get("dotnetCliExecutableLocation", "");
//        dotnetCliPathTextField.setText(dotnetCli);
//    }
//    void store() {
//        NbPreferences.forModule(DotnetCliOptionsPanel.class).put("dotnetCliExecutableLocation", dotnetCliPathTextField.getText());
//    }
    boolean valid() {
        // TODO check whether form is consistent and complete
        return true;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField dotnetCliPathTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton dotnetCliFolderBrowseButton;
    private javax.swing.JButton dotnetCliPathBrowseButton;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables

    void fireChange() {
        changeSupport.fireChange();
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        fireChange();
    }

    private final class DefaultDocumentListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            processUpdate();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            processUpdate();
        }

        private void processUpdate() {
            fireChange();
            updateVersion();
        }

    }
}
