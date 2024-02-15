package com.senior.dreamteam.configs;

import com.senior.dreamteam.authentication.JwtTokenUtil;
import com.senior.dreamteam.exception.DemoGraphqlException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
class RequestHeaderInterceptor implements WebGraphQlInterceptor {
    final JwtTokenUtil jwtTokenUtil;

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        String value = request.getHeaders().getFirst("Authorization");
        if (value == null || value.isEmpty()) {
            request.configureExecutionInput((executionInput, builder) ->
                    builder.graphQLContext(Collections.singletonMap("token", "")).build());
            return chain.next(request);
        }
        if (value.startsWith("Bearer ")) {
            value = value.substring(7);
        }
        String finalValue = value;
        try {
            if (!jwtTokenUtil.isAccessToken(finalValue)) {
                throw new DemoGraphqlException("Unauthorized");
            }
            request.configureExecutionInput((executionInput, builder) ->
                    builder.graphQLContext(Collections.singletonMap("token", finalValue)).build());
            return chain.next(request);
        } catch (Exception e) {
            log.error("Error while parsing WebGraphQlRequest header ");
            throw new RuntimeException(e);
        }

    }
}