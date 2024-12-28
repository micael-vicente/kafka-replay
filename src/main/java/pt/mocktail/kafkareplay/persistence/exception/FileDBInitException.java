package pt.mocktail.kafkareplay.persistence.exception;

public class FileDBInitException extends RuntimeException {

    public FileDBInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
