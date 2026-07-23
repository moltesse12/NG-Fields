package tg.ngstars.interv.config;

import java.io.IOException;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tg.ngstars.common.exception.BaseExceptionHandler;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.ForbiddenException;
import tg.ngstars.common.exception.MediaServiceException;
import tg.ngstars.common.exception.NotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex) {
        return super.handleNotFound(ex);
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException ex) {
        return super.handleConflict(ex);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ProblemDetail handleForbidden(ForbiddenException ex) {
        return super.handleForbidden(ex);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            org.springframework.web.bind.MethodArgumentNotValidException ex) {
        return super.handleValidation(ex);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(
            org.springframework.security.access.AccessDeniedException ex) {
        return super.handleForbidden(ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return super.handleIllegalArgument(ex);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        return super.handleIllegalState(ex);
    }

    @ExceptionHandler(MediaServiceException.class)
    public ProblemDetail handleMediaService(MediaServiceException ex) {
        log.error("Media service error", ex);
        var problem = ProblemDetail.forStatus(org.springframework.http.HttpStatus.BAD_GATEWAY);
        problem.setTitle("Bad Gateway");
        problem.setDetail("Media service unavailable");
        problem.setType(java.net.URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(IOException.class)
    public ProblemDetail handleIOException(IOException ex) {
        log.error("IO error", ex);
        var problem = ProblemDetail.forStatus(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("Erreur d'entree/sortie: " + ex.getMessage());
        problem.setType(java.net.URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLock(
            org.springframework.orm.ObjectOptimisticLockingFailureException ex) {
        return super.handleOptimisticLock(ex);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(
            org.springframework.dao.DataIntegrityViolationException ex) {
        return super.handleDataIntegrity(ex);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        return super.handleException(ex);
    }
}
