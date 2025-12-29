package com.example.companybackend.infrastructure.web.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error de Validación", response.getBody().get("error"));
        assertEquals("Invalid argument", response.getBody().get("message"));
    }

    @Test
    void shouldHandleMaxUploadSizeExceededException() {
        MaxUploadSizeExceededException ex = new MaxUploadSizeExceededException(1000);
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleMaxSizeException(ex);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertEquals("Archivo demasiado grande", response.getBody().get("error"));
    }

    @Test
    void shouldHandleIOException() {
        IOException ex = new IOException("Disk error");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleIOException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error de Entrada/Salida", response.getBody().get("error"));
    }

    @Test
    void shouldHandleGeneralException() {
        Exception ex = new Exception("Unknown error");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error Interno", response.getBody().get("error"));
    }
}
