package com.taptrack.library.exceptions.types;

/**
 * exceptions
 *
 * @author Juliane Maran
 * @since 14/10/2025
 */
public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
