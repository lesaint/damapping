package java.com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Nullable;

import org.springframework.stereotype.Component;

/**
 * EnumIntegerToStringMapperImpl - Classe générée qui implémente l'interface générée
 * et délègue à la classe annotée via une factory statique
 *
 * @author lesaint
 */
@Component
class EnumIntegerToStringMapperImpl implements EnumIntegerToStringMapper {

    @Override
    public String apply(@Nullable Integer input) {
        return EnumIntegerToStringMapperFactory.instance().apply(input);
    }
}
