package tg.ngstars.auth.exception;

/**
 * @deprecated Use {@link tg.ngstars.common.exception.NotFoundException} instead.
 */
@Deprecated(forRemoval = true)
public class NotFoundException extends tg.ngstars.common.exception.NotFoundException {
    public NotFoundException(String message) { super(message); }
}
