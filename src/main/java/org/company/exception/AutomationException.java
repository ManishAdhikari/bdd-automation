package org.company.exception;

public class AutomationException extends RuntimeException {

  public AutomationException() {}

  public AutomationException(String message) {
    super(message);
  }

  public AutomationException(Throwable cause) {
    super(cause);
  }

  public AutomationException(String message, Throwable cause) {
    super(message, cause);
  }
}
