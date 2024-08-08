package com.berk.table_tennis_tournament_management_backend.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;

@Component
public class LogFilter implements Filter {

    private final LogRepository logRepository;

    public LogFilter(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        StringWriter requestWriter = new StringWriter();
        StringWriter responseWriter = new StringWriter();

        // Capture request body
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        chain.doFilter(wrappedRequest, response);
        String requestBody = new String(wrappedRequest.getContentAsByteArray());

        // Capture response body
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);
        chain.doFilter(request, wrappedResponse);
        String responseBody = new String(wrappedResponse.getContentAsByteArray());
        wrappedResponse.copyBodyToResponse();

        // Log the request and response details
        Log log = new Log();
        log.setMethod(httpRequest.getMethod());
        log.setUri(httpRequest.getRequestURI());
        log.setRequestBody(requestBody);
        log.setResponseBody(responseBody);
        log.setResponseStatus(httpResponse.getStatus());
        log.setTimestamp(LocalDateTime.now());

        logRepository.save(log);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}