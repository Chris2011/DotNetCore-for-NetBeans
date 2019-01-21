package net.chrizzly.csharp4netbeans.validator;

import org.openide.util.NbBundle;

public final class ValidationUtils {

    public static final String DOTNET_CLI_PATH = "dotnet-cli.path"; // NOI18N

    private ValidationUtils() {
    }

    @NbBundle.Messages("ValidationUtils.dotnetcli.name=.NET Core CLI")
    public static void validateDotnetCli(ValidationResult result, String dotnetCli) {
        String warning = ExternalExecutableValidator.validateCommand(dotnetCli, Bundle.ValidationUtils_dotnetcli_name());

        if (warning != null) {
            result.addWarning(new ValidationResult.Message(DOTNET_CLI_PATH, warning));
        }
    }
}