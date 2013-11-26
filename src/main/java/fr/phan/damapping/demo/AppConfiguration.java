package fr.phan.damapping.demo;

import javax.inject.Inject;

import fr.phan.damapping.demo.SpringMapperContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SpringMapperContext.class)
public class AppConfiguration {

    @Inject
    private fr.phan.damapping.demo.StringToIntegerMapperFactory stringToIntegerMapperFactory;

    @Inject
    private fr.phan.damapping.demo.EnumIntegerToStringMapper enumIntegerToStringMapper;
    
    @Inject
    private fr.phan.damapping.demo.IntegerToStringMapper integerToStringMapper;

    @Bean
    public AnyService anyService() {
        return new AnyServiceImpl(stringToIntegerMapperFactory, enumIntegerToStringMapper, integerToStringMapper);
    }
}
