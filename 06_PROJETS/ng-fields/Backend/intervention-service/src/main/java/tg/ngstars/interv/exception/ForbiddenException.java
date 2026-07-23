package tg.ngstars.interv.exception;

/**
 * @deprecated Use {@link tg.ngstars.common.exception.ForbiddenException} instead.
 */
@Deprecated(forRemoval = true)
public class ForbiddenException extends tg.ngstars.common.exception.ForbiddenException {
    public ForbiddenException(String message) { super(message); }
}
