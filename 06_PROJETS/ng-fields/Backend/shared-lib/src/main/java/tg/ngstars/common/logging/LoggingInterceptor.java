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
        Object startTimeObj = request.getAttribute("requestStartTime");
        if (startTimeObj == null) return;
        long duration = System.currentTimeMillis() - (long) startTimeObj;
        int status = response.getStatus();
        if (status >= 500) {
            log.error("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        } else if (status >= 400) {
            log.warn("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        } else {
            log.debug("{} {} → {} ({}ms)", request.getMethod(), request.getRequestURI(), status, duration);
        }

        if (ex != null) {
            log.error("Exception during {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);
        }
    }
}
