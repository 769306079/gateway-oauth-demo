package com.example.gateway.config;

import com.example.gateway.constants.AuthConstants;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author lizhichao
 * @description
 * @date 2021/10/15 11:21
 */

@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {


    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> mono , AuthorizationContext authorizationContext) {
        ServerHttpRequest request = authorizationContext.getExchange().getRequest();


        // 1.跨域的预请求直接放行
        if (request.getMethod() == HttpMethod.OPTIONS){
            return Mono.just(new AuthorizationDecision(true));
        }

        // 2.token 为空直接拒绝
        String token = request.getHeaders().getFirst(AuthConstants.JWT_TOKEN_HEADER);
        if (token == null || token.length() == 0){
            return Mono.just(new AuthorizationDecision(false));
        }

        // 3.这里可以做权限拦截，比如根据不同的权限，来拦截路由，我这里就没做啦！
        return  mono.filter(Authentication::isAuthenticated)
                .map(e->new AuthorizationDecision(true))
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
