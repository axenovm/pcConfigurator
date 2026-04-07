package com.example.cursovaya.exception;

import com.example.cursovaya.DTO.response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IncompatibleComponentsException.class)
    public ResponseEntity<ErrorResponse> handleIncompatibleComponents(IncompatibleComponentsException ex,
                                                                      HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST,
                "Incompatible Components", request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN,
                "Access Denied", request.getRequestURI());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(CustomAuthenticationException ex,
                                                            HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.FORBIDDEN,
                "AuthenticationException", request.getRequestURI());
    }

    @ExceptionHandler({ResourceNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex,
                                                        HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND,
                "Resource Not Found", request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST,
                "Validation Error", request.getRequestURI());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleAppValidation(ValidationException ex,
                                                             HttpServletRequest request) {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST,
                "Validation Error", request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex,
                                                             HttpStatus status,
                                                             String error,
                                                             String path) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                error,
                ex.getMessage(),
                path
        );
        return new ResponseEntity<>(response, status);
    }
}
