package java.com.ekino.lesaint.dozerannihilation.demo;

import com.ekino.lesaint.dozerannihilation.demo.StringToInteger;

/**
 * StringToIntegerMapperImpl -
 *
 * @author lesaint
 */
public class StringToIntegerMapperImpl implements StringToIntegerMapper {
    private final StringToInteger stringToInteger;

    protected StringToIntegerMapperImpl(StringToInteger stringToInteger) {
        this.stringToInteger = stringToInteger;
    }

    @Override
    public Integer apply(//@javax.annotation.Nullable
                        java.lang.String input) {
        return stringToInteger.apply(input);
    }
}
