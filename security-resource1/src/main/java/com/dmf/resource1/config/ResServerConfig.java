package com.dmf.resource1.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableResourceServer
@Order(99)
@Slf4j
public class ResServerConfig extends ResourceServerConfigurerAdapter {

    /**
     * 登录登录认证的key
     */
    String POST_AUTHORIZATION = "authorization";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {

        resources.resourceId("Android").stateless(false).tokenExtractor(tokenExtractor());
    }

    private TokenExtractor tokenExtractor() {
        TokenExtractor tokenExtractor = new TokenExtractor() {
            @Override
            public Authentication extract(HttpServletRequest request) {
                String reqBody = getBodyData(request);
                if (StringUtils.isEmpty(reqBody)) {
                    return null;
                }
                JSONObject jsonObject = JSONObject.parseObject(reqBody);
                String tokenValue = jsonObject.getString(POST_AUTHORIZATION);
                if (StringUtils.isEmpty(tokenValue)) {
                    return null;
                }
                return new PreAuthenticatedAuthenticationToken(tokenValue, "");
            }
        };
        return tokenExtractor;
    }

    /**
     * 处理请求体
     * @param request
     * @return
     */
    private String getBodyData(HttpServletRequest request) {
        String result = null;
        String contentType = request.getContentType();
        if(contentType.contains("application/json")){
            StringBuffer data = new StringBuffer();
            String line = null;
            try (BufferedReader reader = request.getReader()) {
                while (null != (line = reader.readLine())) {
                    data.append(line);
                }
            } catch (Exception e) {
                log.info("============解析请求体异常==========", e);
            }
            result = data.toString();
        }else if(contentType.contains("multipart/form-data")){
            String auth = request.getParameter(POST_AUTHORIZATION);
            Map<String,String> map = new HashMap<String,String>();
            map.put(POST_AUTHORIZATION,auth);
            result = JSONObject.toJSONString(map);
        }
        if (StringUtils.isEmpty(result)) {
            log.info("============获取token失败==========");
        }
        return result;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();
    }


    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    //与授权服务器使用共同的密钥进行解析
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123321");
        return converter;
    }

}