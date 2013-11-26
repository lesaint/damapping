package fr.phan.damapping.demo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Contexte Spring généré en fonction de toutes les classes annotées @Mapper et @MapperFactory.
 * Il faut imaginer que cette tâche de génération est délégué à un module à part, assurant ainsi
 * un découplage entre l'éventuel framework de DI client et ce framework.
 *
 * Comme illustré ci-dessous, les seuls types exposés sont:
 *
 *  - pour une classe annotée @Mapper, son interface générée
 *  - pour une classe annotée @MapperFactory, son interface de factory générée,
 *  qui retourne l'interface de mapper générée
 */
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
