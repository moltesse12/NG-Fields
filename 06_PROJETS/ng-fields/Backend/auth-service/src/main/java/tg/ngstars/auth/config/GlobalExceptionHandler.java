package tg.ngstars.auth.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tg.ngstars.common.exception.BaseExceptionHandler;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public org.springframework.http.ProblemDetail handleNotFound(NotFoundException ex) {
        return super.handleNotFound(ex);
    }

    @ExceptionHandler(ConflictException.class)
    public org.springframework.http.ProblemDetail handleConflict(ConflictException ex) {
        return super.handleConflict(ex);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public org.springframework.http.ProblemDetail handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        return super.handleValidation(ex);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public org.springframework.http.ProblemDetail handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        return super.handleForbidden(ex);
    }

    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public org.springframework.http.ProblemDetail handleOptimisticLock(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
        return super.handleOptimisticLock(ex);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public org.springframework.http.ProblemDetail handleDataIntegrity(
            org.springframework.dao.DataIntegrityViolationException ex) {
        return super.handleDataIntegrity(ex);
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ProblemDetail handleException(Exception ex) {
        return super.handleException(ex);
    }
}
