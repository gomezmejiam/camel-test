package org.ingeniods.integration.exception;

public class IngenioBaseException extends RuntimeException {

  private static final long serialVersionUID = -4915364075260280922L;

  public IngenioBaseException(Exception ex) {
    super(ex);
  }

  public IngenioBaseException(String message) {
    super(message);
  }

}
