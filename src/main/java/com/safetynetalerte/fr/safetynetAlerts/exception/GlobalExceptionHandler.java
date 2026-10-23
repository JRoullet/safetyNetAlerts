package com.safetynetalerte.fr.safetynetAlerts.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
// Mutualise le log des exceptions
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String,String>> handleNotFound(NotFoundException e){
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", e.getMessage()));
    }
}
