package com.ekino.lesaint.dozerannihilation.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SpringMapperContext.class)
public class AppConfiguration {

    @Bean
    public AnyService anyService() {
        return new AnyServiceImpl();
    }
}
