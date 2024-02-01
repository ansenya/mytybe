package ru.senya.storage.confs;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Order(0)
@Component()
public class Log implements Filter {

    Logger log = LoggerFactory.getLogger(Log.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();
        if (!(requestURI.contains("styles") || requestURI.contains("static") || requestURI.contains("scripts"))) {
            String fullURL = httpServletRequest.getRequestURL().toString();
            String queryString = httpServletRequest.getQueryString();

            if (queryString != null) {
                fullURL += "?" + queryString;
            }
            log.info(httpServletRequest.getMethod() + " request received from '" +
                    request.getRemoteAddr() + "' to '" + fullURL + "'");
        }
        chain.doFilter(request, response);
    }

}