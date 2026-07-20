package tg.ngstars.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("requestStartTime", System.currentTimeMillis());
        log.debug("→ {} {} from {}", request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (long) request.getAttribute("requestStartTime");
        long duration = System.currentTimeMillis() - startTime;
        int status = response.getStatus();
        String level = status >= 500 ? "ERROR" : status >= 400 ? "WARN" : "INFO";
        
        if ("INFO".equals(level)) {
            log.info("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        } else if ("WARN".equals(level)) {
            log.warn("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        } else {
            log.error("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        }

        if (ex != null) {
            log.error("Exception during {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        }
    }
}
