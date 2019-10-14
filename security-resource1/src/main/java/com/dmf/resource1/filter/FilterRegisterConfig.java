package com.dmf.resource1.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * 类名: FilterRegisterConfig
 * 类描述: 过滤器配置类
 * 创建人: dingmeifei
 * 创建日期: 2018/9/29 10:10
 */
@Configuration
public class FilterRegisterConfig {


    @Bean
    public FilterRegistrationBean paramRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(paramFilter());
        registration.setName("paramFilter");
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }


    @Bean
    public ParamFilter paramFilter() {
        return new ParamFilter();
    }

}
