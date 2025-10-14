package com.taptrack.library.exceptions.types;

/**
 * exceptions
 *
 * @author Juliane Maran
 * @since 14/10/2025
 */
public class InternalServerErrorException extends RuntimeException {
  public InternalServerErrorException(String message) {
    super(message);
  }
}
