package com.example.SaunaHat.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    //管理者権限フィルター
    public FilterRegistrationBean ManagementFilter() {

        FilterRegistrationBean bean = new FilterRegistrationBean(new ManagementFilter());

        //フィルターを適用するURLを指定（ユーザ管理系）
        bean.addUrlPatterns("/userManage/*");
        bean.addUrlPatterns("/editUser/*");
        bean.addUrlPatterns("/accountStop/*");
        bean.addUrlPatterns("/update/*");
        bean.addUrlPatterns("/newEntry/*");

        //フィルターの適用順序を指定（2番目）
        bean.setOrder(2);

        return bean;
    }

    @Bean
    //ログインフィルター
    public FilterRegistrationBean LoginFilter() {

        FilterRegistrationBean bean = new FilterRegistrationBean(new LoginFilter());

        //フィルターを適用するURLを指定（ログイン画面以外）
        bean.addUrlPatterns("/home/*");
        bean.addUrlPatterns("/new/*");
        bean.addUrlPatterns("/delete/*");
        bean.addUrlPatterns("/comment/*");
        bean.addUrlPatterns("/deleteComment/*");
        bean.addUrlPatterns("/userManage/*");
        bean.addUrlPatterns("/editUser/*");
        bean.addUrlPatterns("/accountStop/*");
        bean.addUrlPatterns("/update/*");
        bean.addUrlPatterns("/newEntry/*");



        //フィルターの適用順序を指定（1番目）
        bean.setOrder(1);

        return bean;

    }
}

