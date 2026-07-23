package tg.ngstars.interv.exception;

/**
 * @deprecated Use {@link tg.ngstars.common.exception.ConflictException} instead.
 */
@Deprecated(forRemoval = true)
public class ConflictException extends tg.ngstars.common.exception.ConflictException {
    public ConflictException(String message) { super(message); }
}
