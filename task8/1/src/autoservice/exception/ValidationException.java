package autoservice.exception;

/**
 * Исключение, выбрасываемое при ошибках валидации данных.
 */
public class ValidationException extends AutoserviceException {
    public ValidationException(String message) {
        super(message);
    }
}



