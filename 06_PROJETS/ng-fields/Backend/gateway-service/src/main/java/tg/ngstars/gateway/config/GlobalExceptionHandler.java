package tg.ngstars.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
@Order(-1)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        ProblemDetail problem;
        if (ex instanceof ResponseStatusException rse) {
            HttpStatus status = HttpStatus.resolve(rse.getStatusCode().value());
            if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
            problem = ProblemDetail.forStatus(status);
            problem.setTitle(status.getReasonPhrase());
            problem.setDetail(ex.getMessage());
        } else {
            log.error("Unhandled gateway exception", ex);
            problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            problem.setTitle("Internal Server Error");
            problem.setDetail("An unexpected error occurred");
        }
        problem.setType(URI.create("about:blank"));

        exchange.getResponse().setStatusCode(HttpStatus.valueOf(problem.getStatus()));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

        String json = "{\"type\":\"" + escape(problem.getType().toString())
                + "\",\"title\":\"" + escape(problem.getTitle())
                + "\",\"status\":" + problem.getStatus()
                + ",\"detail\":\"" + escape(problem.getDetail()) + "\"}";

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
