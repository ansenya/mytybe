package ru.senya.spot.configs;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(0)
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    private static final String[] EXCLUDED_PATHS = {"styles", "static", "scripts"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        if (!shouldExclude(requestURI)) {
            logRequest(httpRequest);
        }

        chain.doFilter(request, response);
    }

    private boolean shouldExclude(String requestURI) {
        for (String path : EXCLUDED_PATHS) {
            if (requestURI.contains(path)) {
                return true;
            }
        }
        return false;
    }

    private void logRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String remoteAddr = request.getRemoteAddr();
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        if (queryString != null) {
            requestURL += "?" + queryString;
        }

        logger.info("{} request received from '{}' to '{}'", method, remoteAddr, requestURL);
    }
}
