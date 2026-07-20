package tg.ngstars.media.exception;

public class StorageLimitReachedException extends RuntimeException {
    public StorageLimitReachedException(String message) { super(message); }
}
