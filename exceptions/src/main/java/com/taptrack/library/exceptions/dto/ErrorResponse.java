package com.taptrack.library.exceptions.dto;

import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Representa o payload de erro padronizado do Tap Track System
 *
 * @author Juliane Maran
 * @since 14/10/2025
 */
@Builder
public record ErrorResponse(
  int status,
  String error,
  String message,
  String path,
  LocalDateTime timestamp
) {
}
