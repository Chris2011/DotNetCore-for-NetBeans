package net.chrizzly.dotnetcore4netbeans.options;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.annotations.common.CheckForNull;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.Parameters;
import org.openide.util.Utilities;

// XXX copied from PHP
public final class FileUtils {

    private static final Logger LOGGER = Logger.getLogger(FileUtils.class.getName());

    private static final boolean IS_WINDOWS = Utilities.isWindows();

    public static final File TMP_DIR = new File(System.getProperty("java.io.tmpdir")); // NOI18N

    private FileUtils() {
    }

    /**
     * Find all the files (absolute path) with the given "filename" on user's
     * PATH.
     * <p>
     * This method is suitable for *nix as well as windows.
     *
     * @param filename the name of a file to find.
     * @return list of absolute paths of found files.
     * @see #findFileOnUsersPath(String[])
     */
    public static List<String> findFileOnUsersPath(String filename) {
        Parameters.notNull("filename", filename); // NOI18N
        return findFileOnUsersPath(new String[]{filename});
    }

    /**
     * Find all the files (absolute path) with the given "filename" on user's
     * PATH.
     * <p>
     * This method is suitable for *nix as well as windows.
     *
     * @param filenames the name of a file to find, more names can be provided.
     * @return list of absolute paths of found files (order preserved according
     * to input names).
     * @see #findFileOnUsersPath(String)
     */
    public static List<String> findFileOnUsersPath(String... filenames) {
        Parameters.notNull("filenames", filenames); // NOI18N

        String path = System.getenv("PATH"); // NOI18N
        LOGGER.log(Level.FINE, "PATH: [{0}]", path);
        if (path == null) {
            return Collections.<String>emptyList();
        }
        // on linux there are usually duplicities in PATH
        Set<String> dirs = new LinkedHashSet<String>(Arrays.asList(path.split(File.pathSeparator)));
        LOGGER.log(Level.FINE, "PATH dirs: {0}", dirs);
        List<String> found = new ArrayList<String>(dirs.size() * filenames.length);
        for (String filename : filenames) {
            Parameters.notNull("filename", filename); // NOI18N
            for (String dir : dirs) {
                File file = new File(dir, filename);
                if (file.isFile()) {
                    String absolutePath = FileUtil.normalizeFile(file).getAbsolutePath();
                    LOGGER.log(Level.FINE, "File ''{0}'' found", absolutePath);
                    // not optimal but should be ok
                    if (!found.contains(absolutePath)) {
                        LOGGER.log(Level.FINE, "File ''{0}'' added to found files", absolutePath);
                        found.add(absolutePath);
                    }
                }
            }
        }
        LOGGER.log(Level.FINE, "Found files: {0}", found);
        return found;
    }

    /**
     * Validate a file path and return {@code null} if it is valid, otherwise an
     * error.
     * <p>
     * This method simply calls {@link #validateFile(String, String, boolean)}
     * with "File" (localized) as a {@code source}.
     *
     * @param filePath a file path to validate
     * @param writable {@code true} if the file must be writable, {@code false}
     * otherwise
     * @return {@code null} if it is valid, otherwise an error
     * @see #validateFile(String, String, boolean)
     */
    @NbBundle.Messages("FileUtils.validateFile.file=File")
    @CheckForNull
    public static String validateFile(String filePath, boolean writable) {
        return validateFile(Bundle.FileUtils_validateFile_file(), filePath, writable);
    }

    /**
     * Validate a file path and return {@code null} if it is valid, otherwise an
     * error.
     * <p>
     * A valid file means that the <tt>filePath</tt> represents a valid,
     * readable file with absolute file path.
     *
     * @param source source used in error message (e.g. "Script", "Config file")
     * @param filePath a file path to validate
     * @param writable {@code true} if the file must be writable, {@code false}
     * otherwise
     * @return {@code null} if it is valid, otherwise an error
     */
    @NbBundle.Messages({
        "# {0} - source",
        "FileUtils.validateFile.missing={0} must be selected.",
        "# {0} - source",
        "FileUtils.validateFile.notAbsolute={0} must be an absolute path.",
        "# {0} - source",
        "FileUtils.validateFile.notFile={0} must be a valid file.",
        "# {0} - source",
        "FileUtils.validateFile.notReadable={0} is not readable.",
        "# {0} - source",
        "FileUtils.validateFile.notWritable={0} is not writable."
    })
    @CheckForNull
    public static String validateFile(String source, String filePath, boolean writable) {
        if (!StringUtils.hasText(filePath)) {
            return Bundle.FileUtils_validateFile_missing(source);
        }

        File file = new File(filePath);
        if (!file.isAbsolute()) {
            return Bundle.FileUtils_validateFile_notAbsolute(source);
        } else if (!file.isFile()) {
            return Bundle.FileUtils_validateFile_notFile(source);
        } else if (!file.canRead()) {
            return Bundle.FileUtils_validateFile_notReadable(source);
        } else if (writable && !file.canWrite()) {
            return Bundle.FileUtils_validateFile_notWritable(source);
        }
        return null;
    }

    /**
     * Get the OS-dependent script extension.
     * <ul>Currently it returns (for dotted version):
     * <li><tt>.bat</tt> on Windows
     * <li><tt>.sh</tt> anywhere else
     * </ul>
     *
     * @param withDot return "." as well, e.g. <tt>.sh</tt>
     * @return the OS-dependent script extension
     */
    public static String getScriptExtension(boolean withDot, boolean cmdInsteadBatOnWin) {
        StringBuilder sb = new StringBuilder(4);
        if (withDot) {
            sb.append("."); // NOI18N
        }
        if (IS_WINDOWS) {
            sb.append(cmdInsteadBatOnWin ? "cmd" : "bat"); // NOI18N
        } else {
            sb.append("sh"); // NOI18N
        }
        return sb.toString();
    }
}
