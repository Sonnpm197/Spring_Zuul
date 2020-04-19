package com.son.gateway;

import com.son.gateway.utils.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableZuulProxy
// @EnableZuulServer is used
// when you want to build your own routing service and not use any Zuul prebuilt
// capabilities. An example of this would be if you wanted to use Zuul to
// integrate with a service discovery engine other than Eureka (for example, Consul)
public class LE_ZuulMain {

    // The RestTemplate will use a custom Spring Interceptor class (UserContextInterceptor) to
    // inject the correlation ID into the outbound call as an HTTP header
    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate(){
        RestTemplate template = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }
        return template;
    }

    public static void main(String[] args) {
        SpringApplication.run(LE_ZuulMain.class, args);
    }

}
