package autoservice.exception;

/**
 * Исключение, выбрасываемое при ошибках импорта/экспорта.
 */
public class ImportExportException extends AutoserviceException {
    public ImportExportException(String message) {
        super(message);
    }

    public ImportExportException(String message, Throwable cause) {
        super(message, cause);
    }
}

