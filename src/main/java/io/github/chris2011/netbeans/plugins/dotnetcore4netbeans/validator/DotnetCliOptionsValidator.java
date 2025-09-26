package io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.validator;

import io.github.chris2011.netbeans.plugins.dotnetcore4netbeans.options.DotnetCliOptions;

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