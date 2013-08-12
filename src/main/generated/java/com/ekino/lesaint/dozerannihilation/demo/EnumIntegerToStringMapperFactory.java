package java.com.ekino.lesaint.dozerannihilation.demo;

import com.ekino.lesaint.dozerannihilation.demo.EnumIntegerToString;
import com.ekino.lesaint.dozerannihilation.demo.StringToInteger;
import org.springframework.stereotype.Component;

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
