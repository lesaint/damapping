package java.com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Nullable;

/**
 * EnumIntegerToStringMapperImpl - classe générée qui implémente l'interface générée
 * et délègue son implémentation, via une factory statique, à la classe initiale annotée @Mapper.
 *
 * @author lesaint
 */
class EnumIntegerToStringMapperImpl implements EnumIntegerToStringMapper {

    @Override
    public String apply(@Nullable Integer input) {
        return EnumIntegerToStringMapperFactory.instance().apply(input);
    }
}
