package net.chrizzly.csharp4netbeans.validator;

import net.chrizzly.csharp4netbeans.options.DotnetCliOptions;

public class DotnetCliOptionsValidator {
    private final ValidationResult result = new ValidationResult();

    public DotnetCliOptionsValidator validateDotnetCli() {
        return DotnetCliOptionsValidator.this.validateDotnetCli(DotnetCliOptions.getInstance().getDotnetCli());
    }

    public DotnetCliOptionsValidator validateDotnetCli(String dotnetCli) {
        ValidationUtils.validateDotnetCli(result, dotnetCli);

        return this;
    }

    public ValidationResult getResult() {
        return result;
    }
}