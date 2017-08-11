package net.chrizzly.csharp4netbeans.filetypes.cs.parser;

/**
 *
 * @author Chrl
 */
public class CsError  {

  /**
   * The error's line number.
   */
  public final int line;

  /**
   * The error's column number.
   */
  public final int column;

  /**
   * The error's problem.
   */
  public final String reason;

  /**
   * The error's evidence.
   */
  public final String evidence;

  /**
   * The full error's message.
   */
  public final String message;

  /**
   * Creates a new {@link HandlebarsError}.
   *
   * @param line The error's line number.
   * @param column The error's column number.
   * @param reason The error's reason. Required.
   * @param evidence The error's evidence. Required.
   * @param message The error's message. Required.
   */
  public CsError(final int line,
      final int column, final String reason, final String evidence,
      final String message) {
    this.line = line;
    this.column = column;
    this.reason = reason;
    this.evidence = evidence;
    this.message = message;
  }

  @Override
  public String toString() {
    return message;
  }
}