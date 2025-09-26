package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.projecttypes.universal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 * First wizard panel for selecting .NET project template.
 */
public class TemplateSelectionWizardPanel implements WizardDescriptor.Panel<WizardDescriptor> {

    private TemplateSelectionVisualPanel component;
    private WizardDescriptor wizard;

    @Override
    public Component getComponent() {
        if (component == null) {
            component = new TemplateSelectionVisualPanel(this);
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public boolean isValid() {
        return component != null && component.getSelectedTemplate() != null;
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
            wiz.putProperty("projectTemplate", component.getSelectedTemplate());
        }
    }

    void fireChangeEvent() {
        // This will be called by the visual panel when selection changes
        // We need to notify the wizard that validation state changed
        if (changeListener != null) {
            changeListener.stateChanged(new javax.swing.event.ChangeEvent(this));
        }
    }

    private javax.swing.event.ChangeListener changeListener;

    /**
     * Visual panel for template selection.
     */
    static class TemplateSelectionVisualPanel extends JPanel {

        private final TemplateSelectionWizardPanel wizardPanel;
        private final DotNetTemplateService templateService;
        private JTree categoryTree;
        private JList<DotNetTemplate> templateList;
        private JLabel descriptionLabel;
        private Map<String, List<DotNetTemplate>> templatesByCategory;
        private javax.swing.event.ChangeListener changeListener;

        public TemplateSelectionVisualPanel(TemplateSelectionWizardPanel wizardPanel) {
            this.wizardPanel = wizardPanel;
            this.templateService = new DotNetTemplateService();
            initComponents();
            loadTemplates();
        }

        private void initComponents() {
            setLayout(new BorderLayout());

            // Create category tree
            categoryTree = new JTree();
            categoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            categoryTree.addTreeSelectionListener(this::categorySelectionChanged);

            // Create template list
            templateList = new JList<>(new DefaultListModel<>());
            templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            templateList.setCellRenderer(new TemplateListCellRenderer());
            templateList.addListSelectionListener(this::templateSelectionChanged);

            // Create description label
            descriptionLabel = new JLabel("<html><body style='width: 300px'>Select a project template to see its description.</body></html>");
            descriptionLabel.setVerticalAlignment(JLabel.TOP);

            // Layout
            JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            mainSplit.setLeftComponent(new JScrollPane(categoryTree));

            JPanel rightPanel = new JPanel(new BorderLayout());
            rightPanel.add(new JScrollPane(templateList), BorderLayout.CENTER);
            rightPanel.add(new JScrollPane(descriptionLabel), BorderLayout.SOUTH);

            mainSplit.setRightComponent(rightPanel);
            mainSplit.setDividerLocation(200);

            add(mainSplit, BorderLayout.CENTER);

            // Add loading indicator
            add(new JLabel("Loading templates..."), BorderLayout.NORTH);
        }

        private void loadTemplates() {
            // Load templates asynchronously with error handling
            templateService.getAvailableTemplatesAsync()
                    .thenAcceptAsync(templates -> {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            try {
                                templatesByCategory = templateService.getTemplatesByCategory();
                                updateCategoryTree();

                                // Safely remove loading label
                                if (getComponentCount() > 0 && getComponent(0) instanceof JLabel) {
                                    remove(0);
                                }

                                revalidate();
                                repaint();
                            } catch (Exception e) {
                                // Handle UI update errors gracefully
                                System.err.println("Error updating template UI: " + e.getMessage());
                            }
                        });
                    })
                    .exceptionally(throwable -> {
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            // Show error message instead of loading
                            if (getComponentCount() > 0 && getComponent(0) instanceof JLabel) {
                                ((JLabel) getComponent(0)).setText("Error loading templates. Please check .NET CLI configuration.");
                            }
                        });
                        return null;
                    });
        }

        private void updateCategoryTree() {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(".NET Projects");

            for (String category : templatesByCategory.keySet()) {
                List<DotNetTemplate> templates = templatesByCategory.get(category);
                if (!templates.isEmpty()) {
                    DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category);
                    root.add(categoryNode);
                }
            }

            categoryTree.setModel(new DefaultTreeModel(root));
            categoryTree.expandRow(0); // Expand root

            // Select first category by default
            if (root.getChildCount() > 0) {
                categoryTree.setSelectionRow(1);
            }
        }

        private void categorySelectionChanged(TreeSelectionEvent e) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();
            if (node == null || node.isRoot()) {
                return;
            }

            String category = node.toString();
            List<DotNetTemplate> templates = templatesByCategory.get(category);

            DefaultListModel<DotNetTemplate> listModel = (DefaultListModel<DotNetTemplate>) templateList.getModel();
            listModel.clear();

            if (templates != null) {
                for (DotNetTemplate template : templates) {
                    listModel.addElement(template);
                }
            }

            // Clear selection and description
            templateList.clearSelection();
            descriptionLabel.setText("<html><body style='width: 300px'>Select a template from the list above.</body></html>");

            if (changeListener != null) {
                changeListener.stateChanged(new javax.swing.event.ChangeEvent(this));
            }
        }

        private void templateSelectionChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            DotNetTemplate selected = templateList.getSelectedValue();
            if (selected != null) {
                String description = String.format(
                    "<html><body style='width: 300px'>" +
                    "<b>%s</b><br/><br/>" +
                    "<b>Short Name:</b> %s<br/>" +
                    "<b>Languages:</b> %s<br/>" +
                    "<b>Tags:</b> %s<br/><br/>" +
                    "%s" +
                    "</body></html>",
                    selected.getDisplayName(),
                    selected.getShortName(),
                    String.join(", ", selected.getLanguages()),
                    String.join(", ", selected.getTags()),
                    selected.getDescription()
                );
                descriptionLabel.setText(description);
            } else {
                descriptionLabel.setText("<html><body style='width: 300px'>Select a template from the list above.</body></html>");
            }

            if (changeListener != null) {
                changeListener.stateChanged(new javax.swing.event.ChangeEvent(this));
            }
        }

        public DotNetTemplate getSelectedTemplate() {
            return templateList.getSelectedValue();
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
            // Pre-select template if specified
            DotNetTemplate template = (DotNetTemplate) wizard.getProperty("projectTemplate");
            if (template != null) {
                // Find and select the template
                for (int i = 0; i < templateList.getModel().getSize(); i++) {
                    if (templateList.getModel().getElementAt(i).equals(template)) {
                        templateList.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }

        @Override
        public String getName() {
            return "Select Project Template";
        }

        /**
         * Custom cell renderer for template list.
         */
        private static class TemplateListCellRenderer extends DefaultListCellRenderer {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof DotNetTemplate) {
                    DotNetTemplate template = (DotNetTemplate) value;
                    setText(template.getDisplayName());
                    setToolTipText(template.getDescription());
                }

                return this;
            }
        }
    }
}