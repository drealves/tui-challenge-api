package com.core.tuichallengeapi.exception;

import com.core.tuichallengeapi.model.dto.ResponseErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * Global exception handler for the REST API, handling custom exceptions.
 */
@RestControllerAdvice
public class CustomExceptionHandler {

    /**
     * Handles HttpAcceptException which occurs when the Accept header is not supported (e.g., XML).
     *
     * @param exception The caught HttpAcceptException.
     * @return A ResponseEntity with a custom error message and a 406 Not Acceptable status.
     */
    @ExceptionHandler(HttpAcceptException.class)
    public ResponseEntity<ResponseErrorDto> handleExceptionXml(HttpAcceptException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseErrorDto(HttpStatus.NOT_ACCEPTABLE.value(), exception.getMessage()));
    }

    /**
     * Handles UserNotFoundException which occurs when a requested user is not found.
     *
     * @param message The caught UserNotFoundException.
     * @return A ResponseEntity with a custom error message and a 404 Not Found status.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseErrorDto> handleExceptionUserNotFoundException(UserNotFoundException message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseErrorDto(HttpStatus.NOT_FOUND.value(), message.getMessage()));
    }

    /**
     * Handles IllegalArgumentException which occurs when a requested user is not found.
     *
     * @param message The caught IllegalArgumentException.
     * @return A ResponseEntity with a custom error message and a 404 Not Found status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseErrorDto> handleExceptionArgumentException(IllegalArgumentException message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseErrorDto(HttpStatus.NOT_FOUND.value(), message.getMessage()));
    }

    /**
     * Handles ForbiddenException which occurs when access to a resource is forbidden.
     *
     * @param message The caught ForbiddenException.
     * @return A ResponseEntity with a custom error message and a 403 Forbidden status.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseErrorDto> handleForbiddenExceptionException(ForbiddenException message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseErrorDto(HttpStatus.FORBIDDEN.value(), message.getMessage()));
    }

}