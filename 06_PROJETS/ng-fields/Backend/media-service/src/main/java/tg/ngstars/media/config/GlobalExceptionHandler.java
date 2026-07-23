package tg.ngstars.media.config;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tg.ngstars.media.exception.FileAccessException;
import tg.ngstars.media.exception.StorageLimitReachedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide",
                (a, b) -> a + "; " + b
            ));
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Bad Request");
        problem.setProperty("errors", errors);
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(StorageLimitReachedException.class)
    public ProblemDetail handleStorageLimit(StorageLimitReachedException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.INSUFFICIENT_STORAGE);
        problem.setTitle("Storage Limit Reached");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(FileAccessException.class)
    public ProblemDetail handleFileAccess(FileAccessException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("File Access Denied");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleBadRequest(IllegalArgumentException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        var problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problem.setTitle("Forbidden");
        problem.setDetail("Access denied");
        problem.setType(URI.create("about:blank"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        log.error("Unhandled exception", ex);
        var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Internal Server Error");
        problem.setDetail("An unexpected error occurred");
        problem.setType(URI.create("about:blank"));
        return problem;
    }
}
