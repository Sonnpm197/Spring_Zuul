package com.son.gateway.utils;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// **After going through TrackingFilter
// The UserContextFilter class is a custom HTTP ServletFilter. It maps a correlation
// ID to the UserContext class. The UserContext class is stored values in
// thread-local storage for use later in the call
@Component
@Slf4j
public class UserContextFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        UserContext userContext = UserContextHolder.getContext();
        userContext.setCorrelationId(httpServletRequest.getHeader(UserContext.CORRELATION_ID));
        userContext.setUserId(httpServletRequest.getHeader(UserContext.USER_ID));
        userContext.setAuthToken(httpServletRequest.getHeader(UserContext.AUTH_TOKEN));
        userContext.setOrgId(httpServletRequest.getHeader(UserContext.ORG_ID));

        log.debug("Special Routes Service Incoming Correlation id: {}",
                userContext.getCorrelationId());

        filterChain.doFilter(httpServletRequest, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
