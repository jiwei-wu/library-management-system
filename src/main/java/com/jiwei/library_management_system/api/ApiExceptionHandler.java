package com.jiwei.library_management_system.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    public record ErrorBody(String message) {}

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ErrorBody> notFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorBody(e.getMessage()));
    }

    @ExceptionHandler({BusinessRuleException.class, DataIntegrityViolationException.class})
    ResponseEntity<ErrorBody> conflict(Exception e) {
        String message = e instanceof BusinessRuleException ? e.getMessage() : "Resource already exists or violates a data constraint";
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorBody(message));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
                       HttpMessageNotReadableException.class})
    ResponseEntity<ErrorBody> invalid(Exception e) {
        String message = e instanceof MethodArgumentNotValidException validation
                ? validation.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .findFirst().orElse("Invalid request")
                : "Invalid request";
        return ResponseEntity.badRequest().body(new ErrorBody(message));
    }
}
