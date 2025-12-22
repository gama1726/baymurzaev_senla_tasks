package autoservice.exception;

/**
 * Базовое исключение для автосервиса.
 */
public class AutoserviceException extends Exception {
    public AutoserviceException(String message) {
        super(message);
    }

    public AutoserviceException(String message, Throwable cause) {
        super(message, cause);
    }
}



