package java.com.ekino.lesaint.dozerannihilation.demo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringMapperContext {

    @Bean
    public EnumIntegerToStringMapper enumIntegerToStringMapper() {
        return new EnumIntegerToStringMapperImpl();
    }

    @Bean
    public IntegerToStringMapper integerToStringMapper() {
        return new IntegerToStringMapperImpl();
    }

    @Bean
    public StringToIntegerMapperFactory stringToIntegerMapperFactory() {
        return new StringToIntegerMapperFactoryImpl();
    }
}
