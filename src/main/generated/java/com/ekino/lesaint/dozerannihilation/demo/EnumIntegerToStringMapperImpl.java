package java.com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Resource;

import com.ekino.lesaint.dozerannihilation.demo.EnumIntegerToString;
import org.springframework.stereotype.Component;

/**
 * EnumIntegerToStringMapperImpl -
 *
 * @author lesaint
 */
@Component
public class EnumIntegerToStringMapperImpl implements EnumIntegerToStringMapper {
    @Resource
    private EnumIntegerToString enumIntegerToString;

    @Override
    public String apply(//@javax.annotation.Nullable
                            java.lang.Integer input) {
        return enumIntegerToString.apply(input);
    }
}
