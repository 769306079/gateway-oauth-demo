package com.example.gateway.config;

import com.example.gateway.handler.CustomAccessDeniedHandler;
import com.example.gateway.handler.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;

/**
 * @author lizhichao
 * @description 资源服务配置
 * @date 2021/10/15 13:30
 */
@Component
@EnableWebFluxSecurity
public class ResourceServerConfig {

    @Autowired
    private AuthorizationManager authorizationManager;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        //资源服务配置服务  可以选择 jwt  和 opaqueToken
        http.oauth2ResourceServer().opaqueToken();

        // 自定义处理 授权验证未通过
        http.oauth2ResourceServer().authenticationEntryPoint(customAuthenticationEntryPoint);
        //授权配置
        http.authorizeExchange()
                //这里可以配置白名单
                .pathMatchers("/taobao/**").permitAll()
                .anyExchange().access(authorizationManager)
                .and()
                .exceptionHandling()
                // 处理未授权
                .accessDeniedHandler(customAccessDeniedHandler)
                //处理未认证
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .and()
                .csrf().disable();

        return http.build();
    }

}
