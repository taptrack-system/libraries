package com.taptrack.library.exceptions.handler;

import com.taptrack.library.exceptions.dto.ErrorResponse;
import com.taptrack.library.exceptions.types.ConflictException;
import com.taptrack.library.exceptions.types.InternalServerErrorException;
import com.taptrack.library.exceptions.types.ResourceNotFoundException;
import com.taptrack.library.exceptions.types.UnprocessableEntityException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Handler global para capturar e padronizar respostas de erro
 * em todos os microsserviços do Tap Track System.
 *
 * @author Juliane Maran
 * @since 14/10/2025
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private ErrorResponse buildErrorResponse(HttpStatus status, String message, WebRequest request) {
    String path = (request instanceof ServletWebRequest swr)
      ? swr.getRequest().getRequestURI()
      : "N/A";

    return ErrorResponse.builder()
      .status(status.value())
      .error(status.getReasonPhrase())
      .message(message)
      .path(path)
      .timestamp(LocalDateTime.now())
      .build();
  }

  /**
   * 400 - Erros de validação (Bean Validation)
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
      .map(err -> err.getField() + ": " + err.getDefaultMessage())
      .collect(Collectors.joining(", "));
    log.warn("Falha na validação de argumento: {}", msg);
    return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, msg, request));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
    String msg = ex.getConstraintViolations().stream()
      .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
      .collect(Collectors.joining(", "));
    log.warn("Violação de restrição: {}", msg);
    return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, msg, request));
  }

  /**
   * 400 - Argumentos inválidos
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
    log.warn("Argumento inválido: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
  }

  /**
   * 404 - Recurso não encontrado
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
    log.warn("Recurso não encontrado: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request));
  }

  /**
   * 405 - Método HTTP não suportado
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                                              WebRequest request) {
    String msg = "Método não permitido: " + ex.getMethod();
    log.warn(msg);
    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
      .body(buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, msg, request));
  }

  /**
   * 408 - Timeout
   */
  @ExceptionHandler(TimeoutException.class)
  public ResponseEntity<ErrorResponse> handleTimeout(TimeoutException ex, WebRequest request) {
    log.error("Timeout: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
      .body(buildErrorResponse(HttpStatus.REQUEST_TIMEOUT, ex.getMessage(), request));
  }

  /**
   * 409 - Conflito
   */
  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, WebRequest request) {
    log.warn("Conflito de dados: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
      .body(buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request));
  }

  /**
   * 422 - Entidade não processável
   */
  @ExceptionHandler(UnprocessableEntityException.class)
  public ResponseEntity<ErrorResponse> handleUnprocessable(UnprocessableEntityException ex, WebRequest request) {
    log.warn("Entidade não processável: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
      .body(buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request));
  }

  /**
   * 500 - Erro interno
   */
  @ExceptionHandler({InternalServerErrorException.class, Exception.class})
  public ResponseEntity<ErrorResponse> handleServerError(Exception ex, WebRequest request) {
    log.error("Erro interno: {}", ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", request));
  }

  /**
   * 503 - Serviço indisponível
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleServiceUnavailable(IllegalStateException ex, WebRequest request) {
    log.error("Serviço indisponível: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
      .body(buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request));
  }

}
