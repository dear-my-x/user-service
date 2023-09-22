package com.onetwo.userservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        if (servletRequest instanceof HttpServletRequest request && servletResponse instanceof HttpServletResponse response) {
            HttpServletRequest requestToCache = new ContentCachingRequestWrapper(request);
            HttpServletResponse responseToCache = new ContentCachingResponseWrapper(response);

            chain.doFilter(requestToCache, responseToCache);

            String queryString = requestToCache.getQueryString();

            log.info("Request : {} uri=[{}] request-ip=[{}] header=[{}] content-type=[{}] request body: {}",
                    requestToCache.getMethod(),
                    requestToCache.getRemoteAddr(),
                    queryString == null ? requestToCache.getRequestURI() : requestToCache.getRequestURI() + queryString,
                    getHeaders(requestToCache),
                    requestToCache.getContentType(),
                    getRequestBody((ContentCachingRequestWrapper) requestToCache)
            );
            log.info("Response : header: {}", getResponseHeaders(responseToCache));
            log.info("response body: {}", getResponseBody(responseToCache));
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }

    private Map<String, Object> getResponseHeaders(HttpServletResponse response) {
        Map<String, Object> headerMap = new HashMap<>();

        Collection<String> headerArray = response.getHeaderNames();

        headerArray.forEach(headerName -> headerMap.put(headerName, response.getHeader(headerName)));

        return headerMap;
    }

    private Map<String, Object> getHeaders(HttpServletRequest request) {
        Map<String, Object> headerMap = new HashMap<>();

        Enumeration<String> headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                try {
                    return new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                } catch (UnsupportedEncodingException e) {
                    return " - ";
                }
            }
        }
        return " - ";
    }

    private String getResponseBody(final HttpServletResponse response) throws IOException {
        String payload = null;
        ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        if (wrapper != null) {
            wrapper.setCharacterEncoding("UTF-8");
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                payload = new String(buf, 0, buf.length, wrapper.getCharacterEncoding());
                wrapper.copyBodyToResponse();
            }
        }
        return null == payload ? " - " : payload;
    }
}
