package com.example.Ramashish.booking.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class GlobalExceptionHandler {

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex)
{
            return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
        }
        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateResourceException ex) {
            return build(HttpStatus.CONFLICT, ex.getMessage(), null);
        }
        @ExceptionHandler(AccessDeniedCustomException.class)
        public ResponseEntity<Map<String, Object>>
        handleCustomAccessDenied(AccessDeniedCustomException ex) {
            return build(HttpStatus.FORBIDDEN, ex.getMessage(), null);
        }
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException e)
  {
            return build(HttpStatus.FORBIDDEN, "You do not have permission to perform this action", null);
        }
        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException e) {
            return build(HttpStatus.UNAUTHORIZED, "Invalid username or password", null);
        }
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
            Map<String, String> fieldErrors = new LinkedHashMap<>();
            ex.getBindingResult().getFieldErrors().forEach(err ->
                    fieldErrors.put(err.getField(), err.getDefaultMessage()));
            return build(HttpStatus.BAD_REQUEST, "Validation failed", fieldErrors);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
            return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
        }
        private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message, Object details) {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", Instant.now().toString());
            body.put("status", status.value());
            body.put("error", status.getReasonPhrase());
            body.put("message", message);
            if (details != null) {
                body.put("details", details);
            }
            return ResponseEntity.status(status).body(body);
        }
}
