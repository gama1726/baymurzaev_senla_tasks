package autoservice.exception;

/**
 * Исключение, выбрасываемое когда сущность не найдена.
 */
public class EntityNotFoundException extends AutoserviceException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}



