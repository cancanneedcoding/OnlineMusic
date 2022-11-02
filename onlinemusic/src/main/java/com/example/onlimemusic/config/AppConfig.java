package com.example.onlimemusic.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    /*
    *   用于告诉方法，产生一个Bean对象，然后这个Bean对象交给spring管理。产生这个Bean对象的方法spring只会调用一次
    *   随后这个spring将会将这个Bean对象放入自己的容器当中
    *
    * */
    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //拦截器 登录之后才能访问其他的页面
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LoginInterceptor loginInterceptor=new LoginInterceptor();
        registry.addInterceptor(loginInterceptor).
                addPathPatterns("/**")
                .excludePathPatterns("/js/**.js")
                .excludePathPatterns("/images/**")
                .excludePathPatterns("/css/**.css")
                .excludePathPatterns("/fronts/**")
                .excludePathPatterns("/player/**")
                .excludePathPatterns("/login.html")
                .excludePathPatterns("/user/login");
    }
}
