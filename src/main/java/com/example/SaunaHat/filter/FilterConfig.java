package com.example.SaunaHat.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    //管理者権限フィルター
    public FilterRegistrationBean ManagementFilter() {

        FilterRegistrationBean bean = new FilterRegistrationBean(new ManagementFilter());

        //フィルターを適用するURLを指定
        bean.addUrlPatterns("/userManage");
        bean.addUrlPatterns("/editUser/*");
        bean.addUrlPatterns("/newEntry");

        return bean;
    }

}

