package yuki.exception;

/**
 * Represents an invalid command or other user-input error in Yuki.
 */
public class YukiException extends RuntimeException {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message explanation of the invalid input
     */
    public YukiException(String message) {
        super(message);
    }
}
