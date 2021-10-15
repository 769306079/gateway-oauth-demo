package com.example.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        //资源服务配置服务  可以选择 jwt  和 opaqueToken
        http.oauth2ResourceServer().opaqueToken();

        //授权配置
        http.authorizeExchange()
                //这里可以配置白名单
                .pathMatchers("/taobao/**").permitAll()
                .anyExchange().access(authorizationManager)
                .and()
                .csrf().disable();

        return http.build();
    }

}
