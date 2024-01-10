package com.core.tuichallengeapi.exception;

import com.core.tuichallengeapi.dto.ResponseErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(HttpAcceptException.class)
    public ResponseEntity<ResponseErrorDto> handleExceptionXml(HttpAcceptException exception) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseErrorDto(HttpStatus.NOT_ACCEPTABLE.value(), exception.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseErrorDto> handleExceptionUserNotFoundException(UserNotFoundException message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseErrorDto(HttpStatus.NOT_FOUND.value(), message.getMessage()));
    }

}