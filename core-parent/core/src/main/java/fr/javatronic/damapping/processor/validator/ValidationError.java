package fr.javatronic.damapping.processor.validator;

/**
 * ValidationError - Check exception representing a validation error
 *
 * @author Sébastien Lesaint
 */
public class ValidationError extends Exception {

  public ValidationError(String message) {
    super(message);
  }

  public ValidationError(String message, Throwable cause) {
    super(message, cause);
  }

  public ValidationError(Throwable cause) {
    super(cause);
  }

  public ValidationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
