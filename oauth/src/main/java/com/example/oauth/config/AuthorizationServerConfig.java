package com.example.oauth.config;

import org.apache.catalina.UserDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lizhichao
 * @description 授权服务配置
 * @date 2021/10/13 17:03
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    @Qualifier("UserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private DataSource dataSource;

    /**
     * 客户端信息访问配置
     * 配置clientDetails授权方式，
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // 1.这里用于测试，客户端信息从内存中获取
//        clients.inMemory()
//                .withClient("client") // client_id
//                .secret("$2a$10$xox4JztwJISO9eWlj85.YO5ugx3RjVQYXXQ.TyACcjseiyyPeb8Q6")// client_secret
//                .authorizedGrantTypes("password","authorization_code","implicit", "refresh_token")// 该client允许的授权类型
//                .scopes("app")      // 允许的授权范围
//                .autoApprove(true) //登录后绕过批准询问
//                .redirectUris("https://www.baidu.com");


        // 2.客户端信息从数据库获取
         clients.jdbc(dataSource);

        // 3.这是自己实现ClientDetailsService
        // clients.withClientDetails(clientDetailsService);

    }


    /**
     * 访问端点配置
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {

        endpoints//.authenticationManager(authenticationManager)  // 认证管理器
                .tokenStore(new InMemoryTokenStore())           // token存储
                .userDetailsService(userDetailsService)       //用户信息获取方式，密码模式必须要
//                .tokenEnhancer(jwtAccessTokenConverter())   //令牌增强,可以设置生成不同类型的token,如jwt
                .tokenGranter(tokenGranter(endpoints))         //配置grant_type模式，用户可以进行扩展,如果不配置则默认使用密码模式、简化模式、验证码模式以及刷新token模式，如果配置了只使用配置中，默认配置失效
                .reuseRefreshTokens(false)
                .exceptionTranslator(new CustomizeWebResponseExceptionTranslator());

    }

    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        List<TokenGranter> list = new ArrayList<>();
        //这里配置密码模式、刷新token模式、授权码模式、简化模式
        list.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        list.add(new RefreshTokenGranter(endpoints.getTokenServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        list.add(new AuthorizationCodeTokenGranter(endpoints.getTokenServices(),endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(), endpoints.getOAuth2RequestFactory()));
        list.add(new ImplicitTokenGranter(endpoints.getTokenServices(),endpoints.getClientDetailsService(),endpoints.getOAuth2RequestFactory()));
        return new CompositeTokenGranter(list);
    }

    /**
     * 授权服务安全配置
     * 配置：安全检查流程,用来配置令牌端点（Token Endpoint）的安全与权限访问
     *  默认过滤器：BasicAuthenticationFilter
     *  1、oauth_client_details表中clientSecret字段加密【ClientDetails属性secret】
     *  2、CheckEndpoint类的接口 oauth/check_token 无需经过过滤器过滤，默认值：denyAll()
     * 对以下的几个端点进行权限配置：
     * /oauth/authorize：授权端点
     * /oauth/token：令牌端点
     * /oauth/confirm_access：用户确认授权提交端点
     * /oauth/error：授权服务错误信息端点
     * /oauth/check_token：用于资源服务访问的令牌解析端点
     * /oauth/token_key：提供公有密匙的端点，如果使用JWT令牌的话
     *
     * @param security
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients()  //允许表单提交
                .passwordEncoder(new BCryptPasswordEncoder()) //密码加密方式
                .tokenKeyAccess("permitAll()")                // 令牌访问权限
                .checkTokenAccess("isAuthenticated()");      //token校验访问权限

    }



    /**
     * 使用非对称加密算法对token签名
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(keyPair());
        return converter;
    }

    /**
     * JWT内容增强
     * @return
     */
    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory factory = new KeyStoreKeyFactory(
                new ClassPathResource("test.jks"), "123456".toCharArray());
        KeyPair keyPair = factory.getKeyPair(
                "test", "123456".toCharArray());
        return keyPair;
    }
}
