package net.chrizzly.csharp4netbeans.ui.options;

import java.awt.EventQueue;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.chrizzly.csharp4netbeans.options.DotnetCliOptions;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@NbBundle.Messages({
    "AdvancedOption_DisplayName_DotnetCli=.NET Core CLI",
    "AdvancedOption_Keywords_DotnetCli=Microsoft, .NET Core, .NET Core CLI",
})
@OptionsPanelController.TopLevelRegistration(
    categoryName = ".NET Core",
    iconBase = "net/chrizzly/csharp4netbeans/microsoft-net-core.gif",
    keywords = "AdvancedOption_Keywords_DotnetCli", // TODO: Maybe needed for the search
    keywordsCategory = ".NET Core" // TODO: Maybe needed for the search and keymap
)
public final class DotnetCliOptionsPanelController extends OptionsPanelController implements ChangeListener {
    public static final String OPTIONS_PATH = ".NET Core"; // NOI18N
    
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private DotnetCliOptionsPanel dotnetCliOptionsPanel;
    private volatile boolean changed = false;
    private boolean firstOpening = true;

    @Override
    public void update() {
        assert EventQueue.isDispatchThread();
        if (firstOpening || !isChanged()) { // if panel is not modified by the user and he switches back to this panel, set to default
            firstOpening = false;
            getDotnetCliOptionsPanel().setDotnetCli(getDotnetCliOptions().getDotnetCli());
        }

        changed = false;
    }

    @Override
    public void applyChanges() {
        EventQueue.invokeLater(() -> {
            getDotnetCliOptions().setDotnetCli(getDotnetCliOptionsPanel().getDotnetCli());

            changed = false;
        });
    }

    @Override
    public void cancel() {
        if (isChanged()) {
            getDotnetCliOptionsPanel().setDotnetCli(getDotnetCliOptions().getDotnetCli());
        }
    }

    @Override
    public boolean isValid() {
        assert EventQueue.isDispatchThread();
        return getDotnetCliOptionsPanel().valid();
    }

    @Override
    public boolean isChanged() {
        String saved = getDotnetCliOptions().getDotnetCli();
        String current = getDotnetCliOptionsPanel().getDotnetCli().trim();

        return saved == null ? !current.isEmpty() : !saved.equals(current);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("net.chrizzly.csharp4netbeans.ui.options.DotnetCliOptionsPanel"); // NOI18N
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getDotnetCliOptionsPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (!changed) {
            changed = true;
            propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        propertyChangeSupport.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    private DotnetCliOptions getDotnetCliOptions() {
        return DotnetCliOptions.getInstance();
    }

    private DotnetCliOptionsPanel getDotnetCliOptionsPanel() {
        assert EventQueue.isDispatchThread();

        if (dotnetCliOptionsPanel == null) {
            dotnetCliOptionsPanel = new DotnetCliOptionsPanel();
            dotnetCliOptionsPanel.addChangeListener(this);
        }

        return dotnetCliOptionsPanel;
    }
}
