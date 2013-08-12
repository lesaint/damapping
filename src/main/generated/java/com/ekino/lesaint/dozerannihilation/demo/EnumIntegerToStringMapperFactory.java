package java.com.ekino.lesaint.dozerannihilation.demo;

import com.ekino.lesaint.dozerannihilation.demo.EnumIntegerToString;

/**
 * StringToIntegerMapperFactory -
 *
 * @author Sébastien Lesaint
 */
class EnumIntegerToStringMapperFactory {

    public static EnumIntegerToString instance() {
        return EnumIntegerToString.INSTANCE;
    }
}
