package tg.ngstars.media.config;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tg.ngstars.common.exception.BaseExceptionHandler;
import tg.ngstars.media.exception.FileAccessException;
import tg.ngstars.media.exception.StorageLimitReachedException;

@RestControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        return super.handleValidation(ex);
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
        return super.handleIllegalArgument(ex);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        return super.handleException(ex);
    }
}
