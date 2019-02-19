package net.chrizzly.dotnetcore4netbeans.projecttypes.dotnetcore.consoleapp;

import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.FinishablePanel;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Panel just asking for basic info.
 */
public class ConsoleAppWizardPanel implements FinishablePanel<WizardDescriptor> {
    private WizardDescriptor wizardDescriptor;
    private ConsoleAppPanelVisual consoleAppPanelVisual;

    public ConsoleAppWizardPanel() {
    }

    @Override
    public ConsoleAppPanelVisual getComponent() {
        if (consoleAppPanelVisual == null) {
            consoleAppPanelVisual = new ConsoleAppPanelVisual(this);
            consoleAppPanelVisual.setName(NbBundle.getMessage(ConsoleAppWizardPanel.class, "LBL_CreateProjectStep"));
        }
        return consoleAppPanelVisual;
    }

    @Override
    public HelpCtx getHelp() {
        return new HelpCtx("net.chrizzly.dotnetcore4netbeans.projecttypes.dotnetcore.consoleapp.ConsoleAppWizardPanel");
    }
    
    @Override
    public boolean isValid() {
        getComponent();
        
        return consoleAppPanelVisual.valid(wizardDescriptor);
    }

    private final Set<ChangeListener> listeners = new HashSet<>(1); // or can use ChangeSupport in NB 6.0

    protected final void fireChangeEvent() {
        Set<ChangeListener> ls;
        synchronized (listeners) {
            ls = new HashSet<>(listeners);
        }
        ChangeEvent ev = new ChangeEvent(this);
        ls.forEach((l) -> {
            l.stateChanged(ev);
        });
    }

    @Override
    public boolean isFinishPanel() {
        return true;
    }

    @Override
    public void readSettings(WizardDescriptor settings) {
        wizardDescriptor = settings;
        consoleAppPanelVisual.read(wizardDescriptor);
    }

    @Override
    public void storeSettings(WizardDescriptor settings) {
        consoleAppPanelVisual.store(settings);
    }
    
    @Override
    public void addChangeListener(ChangeListener listener) {
        getComponent().addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        getComponent().removeChangeListener(listener);
    }
}