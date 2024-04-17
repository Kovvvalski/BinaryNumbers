package by.kovalski.binarynumbers.exception;

public class OutOfRangeException extends RuntimeException{
  public OutOfRangeException() {
    super();
  }

  public OutOfRangeException(String message) {
    super(message);
  }

  public OutOfRangeException(String message, Throwable cause) {
    super(message, cause);
  }

  public OutOfRangeException(Throwable cause) {
    super(cause);
  }
}
