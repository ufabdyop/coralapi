package edu.utah.nanofab.coralapi.exceptions;

public class InvalidTicketException extends Exception {

  private static final long serialVersionUID = -4066265611609392294L;
  
  public InvalidTicketException() {
    super();
  }


  public InvalidTicketException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidTicketException(String message) {
    super(message);
  }

  public InvalidTicketException(Throwable cause) {
    super(cause);
  }
}
