package com.son.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        return null;
    }
}