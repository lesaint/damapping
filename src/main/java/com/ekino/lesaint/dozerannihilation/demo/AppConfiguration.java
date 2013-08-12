package com.ekino.lesaint.dozerannihilation.demo;

import java.com.ekino.lesaint.dozerannihilation.demo.SpringMapperContext;

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
