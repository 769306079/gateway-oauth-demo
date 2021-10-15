//package com.example.gateway;
//
//import com.example.gateway.config.AuthorizationManager;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.stereotype.Component;
//
///**
// * @author lizhichao
// * @description 资源服务配置  JWT 版
// * @date 2021/10/15 13:30
// */
//@Component
//@EnableWebFluxSecurity
//public class ResourceServerConfigJWT {
//
//    @Autowired
//    private AuthorizationManager authorizationManager;
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http.oauth2ResourceServer().jwt()
//                .jwtAuthenticationConverter(jwtAuthenticationConverter());
//
//        http.authorizeExchange()
//                .anyExchange().access(authorizationManager)
//                .and().csrf().disable();
//
//        return http.build();
//    }
//
//    /**
//     * @linkhttps://blog.csdn.net/qq_24230139/article/details/105091273
//     * ServerHttpSecurity没有将jwt中authorities的负载部分当做Authentication
//     * 需要把jwt的Claim中的authorities加入
//     * 方案：重新定义ReactiveAuthenticationManager权限管理器，默认转换器JwtGrantedAuthoritiesConverter
//     * @return
//     */
//    @Bean
//    public ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
//        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
//    }
//
//}
