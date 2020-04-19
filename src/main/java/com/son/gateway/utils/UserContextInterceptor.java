package com.son.gateway.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

// UserContextInterceptor ensures that all outbound REST
// calls have the correlation ID from the UserContext in them
// (call from A -> B then A & B will have the same correlation ID)
public class UserContextInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        HttpHeaders headers = request.getHeaders();
        UserContext userContext = UserContextHolder.getContext();
        headers.add(UserContext.CORRELATION_ID, userContext.getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN, userContext.getAuthToken());

        return execution.execute(request, body);
    }
}