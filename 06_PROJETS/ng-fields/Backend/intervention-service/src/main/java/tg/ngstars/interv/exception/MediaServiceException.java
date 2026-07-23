package tg.ngstars.interv.exception;

/**
 * @deprecated Use {@link tg.ngstars.common.exception.MediaServiceException} instead.
 */
@Deprecated(forRemoval = true)
public class MediaServiceException extends tg.ngstars.common.exception.MediaServiceException {
    public MediaServiceException(String message) { super(message); }
    public MediaServiceException(String message, Throwable cause) { super(message, cause); }
}
