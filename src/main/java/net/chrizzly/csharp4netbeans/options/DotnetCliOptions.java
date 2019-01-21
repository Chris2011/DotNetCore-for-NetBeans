package net.chrizzly.csharp4netbeans.options;

import java.util.List;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import net.chrizzly.csharp4netbeans.executables.DotnetCliExecutable;
import org.netbeans.api.annotations.common.CheckForNull;
import org.openide.util.NbPreferences;

public class DotnetCliOptions {
    public static final String DOTNET_CLI_PATH = "dotnet-cli.path"; // NOI18N

    // Do not change arbitrary - consult with layer's folder OptionsExport
    // Path to Preferences node for storing these preferences
    private static final String PREFERENCES_PATH = "nodejs"; // NOI18N

    private static final DotnetCliOptions INSTANCE = new DotnetCliOptions();

    private final Preferences preferences;
    private volatile boolean dotnetCliSearched = false;

    public static DotnetCliOptions getInstance() {
        return INSTANCE;
    }

    public void addPreferenceChangeListener(PreferenceChangeListener listener) {
        preferences.addPreferenceChangeListener(listener);
    }

    public void removePreferenceChangeListener(PreferenceChangeListener listener) {
        preferences.removePreferenceChangeListener(listener);
    }

    private DotnetCliOptions() {
        preferences = NbPreferences.forModule(DotnetCliOptions.class).node(PREFERENCES_PATH);
    }

    @CheckForNull
    public String getDotnetCli() {
        String path = preferences.get(DOTNET_CLI_PATH, null);

        if (path == null && !dotnetCliSearched) {
            dotnetCliSearched = true;
            List<String> files = FileUtils.findFileOnUsersPath(DotnetCliExecutable.DOTNET_CLI_NAME);

            if (!files.isEmpty()) {
                path = files.get(0);
                setDotnetCli(path);
            }
        }

        return path;
    }

    public void setDotnetCli(String dotnetCli) {
        preferences.put(DOTNET_CLI_PATH, dotnetCli);
    }
}
