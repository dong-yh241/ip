package cipher;

/**
 * Represents an application-specific exception used by Cipher.
 * <p>
 * This exception is thrown when user input is invalid or when an operation cannot be completed.
 */
public class CipherException extends Exception {

    /**
     * Constructs a {@code CipherException} with the specified message.
     *
     * @param message Error message describing the cause of the exception.
     */
    public CipherException(String message) {
        super(message);
    }
}
