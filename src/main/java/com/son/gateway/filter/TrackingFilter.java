package com.son.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

// When a call is made to the service through the Zuul gateway, the
// TrackingFilter will inject a correlation ID into the incoming HTTP header
// for any calls coming into Zuul.
@Component
@Slf4j
public class TrackingFilter extends ZuulFilter {
    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;

    @Autowired
    private FilterUtils filterUtils;

    @Value("${signingKey}")
    private String signingKey;

    // The filterType() method is used to tell Zuul whether the filter
    // is a pre-, route, or post filter.
    @Override
    public String filterType() {
        return FilterUtils.PRE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    // The shouldFilter() method returns a Boolean indicating whether or
    // not the filter should be active
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }

    public Object run() {
        if (filterUtils.getCorrelationId() != null) {
            log.debug("tmx-correlation-id found in tracking filter: {}. ",
                    filterUtils.getCorrelationId());
        } else {
            filterUtils.setCorrelationId(generateCorrelationId());
            log.debug("tmx-correlation-id generated in tracking filter: {}.",
                    filterUtils.getCorrelationId());
        }

        RequestContext ctx = RequestContext.getCurrentContext();
        log.debug("Processing incoming request for {}.", ctx.getRequest().getRequestURI());
        jwtSpecialProp();
        return null;
    }

    private void jwtSpecialProp() {
        String jwt = filterUtils.getAuthToken().replace("Bearer ", "");
        try {
            Claims claims =
                    Jwts.parser()
                    .setSigningKey(signingKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(jwt)
                    .getBody();

            log.info("Special property from OAuth2 server: {}", claims.get("specialProperty"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}