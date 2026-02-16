package autoservice.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import autoservice.exception.AutoserviceException;
import autoservice.exception.EntityNotFoundException;
import autoservice.exception.ImportExportException;
import autoservice.exception.ValidationException;
import autoservice.web.dto.ErrorResponseDto;

/**
 * Глобальная обработка исключений REST API.
 * Ответы в формате JSON/XML через ErrorResponseDto.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFound(EntityNotFoundException ex) {
        logger.warn("Entity not found: {}", ex.getMessage());
        ErrorResponseDto body = new ErrorResponseDto(ex.getMessage(), "NOT_FOUND", HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(ValidationException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        ErrorResponseDto body = new ErrorResponseDto(ex.getMessage(), "VALIDATION_ERROR", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ImportExportException.class)
    public ResponseEntity<ErrorResponseDto> handleImportExport(ImportExportException ex) {
        logger.warn("Import/Export error: {}", ex.getMessage());
        ErrorResponseDto body = new ErrorResponseDto(ex.getMessage(), "IMPORT_EXPORT_ERROR", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(AutoserviceException.class)
    public ResponseEntity<ErrorResponseDto> handleAutoservice(AutoserviceException ex) {
        logger.warn("Autoservice error: {}", ex.getMessage());
        ErrorResponseDto body = new ErrorResponseDto(ex.getMessage(), "AUTOSERVICE_ERROR", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Bad request: {}", ex.getMessage());
        ErrorResponseDto body = new ErrorResponseDto(ex.getMessage(), "BAD_REQUEST", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(Exception ex) {
        logger.error("Unexpected error", ex);
        ErrorResponseDto body = new ErrorResponseDto(
            ex.getMessage() != null ? ex.getMessage() : "Внутренняя ошибка сервера",
            "INTERNAL_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
