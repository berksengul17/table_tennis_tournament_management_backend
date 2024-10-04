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

        if (httpRequest.getRequestURI().contains("/get-participants") ||
            httpRequest.getRequestURI().contains("/download-age-categories") ||
            httpRequest.getRequestURI().contains("/download-groups") ||
            httpRequest.getRequestURI().contains("/download-group-table-time") ||
            httpRequest.getRequestURI().contains("/download-all-group-table-time") ||
            httpRequest.getRequestURI().contains("/download-bracket") ||
            httpRequest.getRequestURI().contains("/download-participants")) {
            chain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        // Process the request and response
        chain.doFilter(wrappedRequest, wrappedResponse);

        // Capture request body
        String requestBody = new String(wrappedRequest.getContentAsByteArray());

        // Capture response body
        String responseBody = new String(wrappedResponse.getContentAsByteArray());

        // Copy the response body to the real response
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
