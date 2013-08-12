package com.ekino.lesaint.dozerannihilation.demo;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SpringMapperContext.class)
public class AppConfiguration {

    @Inject
    private StringToIntegerMapperFactory stringToIntegerMapperFactory;

    @Inject
    private EnumIntegerToStringMapper enumIntegerToStringMapper;
    
    @Inject
    private IntegerToStringMapper integerToStringMapper;

    @Bean
    public AnyService anyService() {
        return new AnyServiceImpl(stringToIntegerMapperFactory, enumIntegerToStringMapper, integerToStringMapper);
    }
}
