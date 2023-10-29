package ru.senya.mytybe.security.cfg;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Order(0)
@Component()
public class LoggingFilter implements Filter {

    Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        log.info(httpServletRequest.getMethod() + " request received from '" + request.getRemoteAddr() + "' to '" + httpServletRequest.getRequestURI() + "'");

        chain.doFilter(request, response);
    }

}