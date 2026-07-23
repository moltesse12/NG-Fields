package tg.ngstars.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base exception handler providing standard RFC 7807 ProblemDetail responses.
 * <p>
 * Services should extend this class and annotate with {@code @RestControllerAdvice}
 * to inherit common handlers. Add service-specific handlers as needed.
 * <p>
 * Exception hierarchy:
 * <ul>
 *   <li>{@link NotFoundException} → 404 Not Found: resource not found by ID or key</li>
 *   <li>{@link ConflictException} → 409 Conflict: duplicate resource, state conflict</li>
 *   <li>{@link ForbiddenException} → 403 Forbidden: business-level access denied</li>
 *   <li>{@link BusinessException} → 400 Bad Request: business rule violation with error code</li>
 *   <li>{@link MediaServiceException} → 502 Bad Gateway: downstream media service failure</li>
 * </ul>
 */
public abstract class BaseExceptionHandler {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    protected ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                fe -> fe.getField(),
                fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalide",
                (a, b) -> a + "; " + b));
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Bad Request");
        detail.setType(URI.create("about:blank"));
        detail.setProperty("errors", errors);
        return detail;
    }

    protected ProblemDetail handleNotFound(NotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Not Found");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleConflict(ConflictException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Conflict");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleForbidden(AccessDeniedException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setTitle("Forbidden");
        detail.setDetail("Access denied");
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleForbidden(ForbiddenException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setTitle("Forbidden");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleBusiness(BusinessException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Business Error");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        detail.setProperty("code", ex.getCode());
        return detail;
    }

    protected ProblemDetail handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Conflict");
        detail.setDetail("Ce document a ete modifie par un autre utilisateur. Veuillez recharger la page.");
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Conflict");
        detail.setDetail("Violation de contrainte de donnees. L'operation ne peut pas etre effectuee.");
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Bad Request");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        detail.setTitle("Conflict");
        detail.setDetail(ex.getMessage());
        detail.setType(URI.create("about:blank"));
        return detail;
    }

    protected ProblemDetail handleException(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        detail.setTitle("Internal Server Error");
        detail.setDetail("Une erreur inattendue s'est produite");
        detail.setType(URI.create("about:blank"));
        return detail;
    }
}
