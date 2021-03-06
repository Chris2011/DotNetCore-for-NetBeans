package net.chrizzly.dotnetcore4netbeans.ui.options;

import java.awt.EventQueue;
import java.io.File;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import net.chrizzly.dotnetcore4netbeans.executables.DotnetCliExecutable;
import net.chrizzly.dotnetcore4netbeans.options.FileUtils;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.ChangeSupport;
import org.openide.util.NbBundle;

@OptionsPanelController.Keywords(keywords = {"#KW.DotnetCliOptionsPanel"}, location = ".NET Core", tabTitle = ".NET Core CLI")
public final class DotnetCliOptionsPanel extends JPanel implements ChangeListener{
    private static final long serialVersionUID = 1L;
    private final ChangeSupport changeSupport = new ChangeSupport(this);

    public DotnetCliOptionsPanel() {
        assert EventQueue.isDispatchThread();

        initComponents();

        init();
    }

    private void init() {
        DocumentListener defaultDocumentListener = new DefaultDocumentListener();
        dotnetCliPathTextField.getDocument().addDocumentListener(defaultDocumentListener);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dotnetCliPathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 17, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dotnetCliFolderBrowseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dotnetCliPathBrowseButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(dotnetCliPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(dotnetCliFolderBrowseButton)
                .addComponent(dotnetCliPathBrowseButton))
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
        }
    }//GEN-LAST:event_dotnetCliFolderBrowseButtonActionPerformed

    @NbBundle.Messages("DotnetCliOptionsPanel.executable.notFound=No .NET Core CLI executable found.")
    private void dotnetCliPathBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dotnetCliPathBrowseButtonActionPerformed
        List<String> dotnetCliPaths = FileUtils.findFileOnUsersPath(DotnetCliExecutable.DOTNET_CLI_NAME);

        if (dotnetCliPaths.isEmpty()) {
            StatusDisplayer.getDefault().setStatusText(Bundle.DotnetCliOptionsPanel_executable_notFound());
        } else {
            dotnetCliPathTextField.setText(dotnetCliPaths.get(0));
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
        }

    }
}
