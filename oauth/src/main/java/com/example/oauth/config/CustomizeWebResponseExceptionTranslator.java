package com.example.oauth.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * @author lizhichao
 * @description
 * @date 2021/10/15 17:43
 */

public class CustomizeWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception>  {
    private static Logger log = LoggerFactory.getLogger(CustomizeWebResponseExceptionTranslator.class);

    @Override
    public ResponseEntity<OAuth2Exception>  translate(Exception e) throws Exception {
        OAuth2Exception oAuth2Exception = new OAuth2Exception(e.getMessage());

        log.info("message:{}",e.getMessage());
        ResponseEntity<OAuth2Exception> response = new ResponseEntity<OAuth2Exception>(oAuth2Exception,HttpStatus.OK);
        return response;
    }
}
