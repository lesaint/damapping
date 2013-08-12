package java.com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Resource;

import com.ekino.lesaint.dozerannihilation.demo.EnumIntegerToString;
import org.springframework.stereotype.Component;

/**
 * EnumIntegerToStringMapperImpl - Classe générée qui implémente l'interface générée et délègue à la classe annotée
 *
 * @author lesaint
 */
@Component
public class EnumIntegerToStringMapperImpl implements EnumIntegerToStringMapper {
    // on se fait injecter via spring la classe source car l'annotation @Mapper étend @Component
    @Resource
    private EnumIntegerToString enumIntegerToString;

    @Override
    public String apply(//@javax.annotation.Nullable
                            java.lang.Integer input) {
        return enumIntegerToString.apply(input);
    }
}
