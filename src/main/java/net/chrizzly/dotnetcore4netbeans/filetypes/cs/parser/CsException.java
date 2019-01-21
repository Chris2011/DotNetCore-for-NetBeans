package net.chrizzly.dotnetcore4netbeans.filetypes.cs.parser;

/**
 *
 * @author Chrl
 */
public class CsException extends RuntimeException {
    /**
     * The serial UUID.
     */
    private static final long serialVersionUID = -294368972176956335L;

    /**
     * A handlebars error. Optional.
     */
    private CsError error;

    /**
     * Creates a new {@link HandlebarsException}.
     *
     * @param error The hbs error's. Required.
     */
    public CsException(final CsError error) {
        super(error.message);
        this.error = error;
    }

    /**
     * Creates a new {@link HandlebarsException}.
     *
     * @param cause The error's cause.
     */
    public CsException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new {@link HandlebarsException}.
     *
     * @param error The error's message.
     * @param cause The error's cause.
     */
    public CsException(final CsError error,
            final Throwable cause) {
        super(error.message, cause);
        this.error = error;
    }

    /**
     * Creates a new {@link HandlebarsException}.
     *
     * @param message The error's message.
     * @param cause The error's cause.
     */
    public CsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * A handlebars error.
     *
     * @return A handlebars error. It might be null.
     */
    public CsError getError() {
        return error;
    }
}
