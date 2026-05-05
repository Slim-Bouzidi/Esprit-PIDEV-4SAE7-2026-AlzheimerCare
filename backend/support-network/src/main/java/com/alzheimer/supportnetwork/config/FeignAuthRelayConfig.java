package com.alzheimer.supportnetwork.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Relays incoming Authorization header to Feign downstream calls.
 * Keeps inter-service calls compatible with secured resource servers.
 */
@Configuration
public class FeignAuthRelayConfig {

    private static final String AUTHORIZATION = "Authorization";

    @Bean
    public RequestInterceptor bearerTokenRelayInterceptor() {
        return template -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (!(requestAttributes instanceof ServletRequestAttributes servletAttributes)) {
                return;
            }
            String authHeader = servletAttributes.getRequest().getHeader(AUTHORIZATION);
            if (authHeader != null && !authHeader.isBlank()) {
                template.header(AUTHORIZATION, authHeader);
            }
        };
    }
}

