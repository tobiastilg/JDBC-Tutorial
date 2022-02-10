package domain;

/**
 * Erbt von RuntimeException - einer unchecked Exception - die nicht geworfen werden muss
 */
public class InvalidValueException extends RuntimeException {
    public InvalidValueException(String message) {
        super(message);
    }
}
