package java.com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Resource;

import com.ekino.lesaint.dozerannihilation.demo.EnumIntegerToString;
import org.springframework.stereotype.Component;

/**
 * EnumIntegerToStringMapperImpl - Classe générée qui implémente l'interface générée
 * et délègue à la classe annotée via une factory statique
 *
 * @author lesaint
 */
@Component
public class EnumIntegerToStringMapperImpl implements EnumIntegerToStringMapper {

    @Override
    public String apply(//@javax.annotation.Nullable
                            java.lang.Integer input) {
        return EnumIntegerToStringMapperFactory.instance().apply(input);
    }
}
