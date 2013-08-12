package java.com.ekino.lesaint.dozerannihilation.demo;

import com.ekino.lesaint.dozerannihilation.demo.StringToInteger;

/**
 * StringToIntegerMapperFactory -
 *
 * @author Sébastien Lesaint
 */
class StringToIntegerMapperFactoryImpl implements StringToIntegerMapperFactory {

    @Override
    public StringToIntegerMapper bigDecimal() {
        return new StringToIntegerMapperImpl(StringToInteger.integer());
    }

    @Override
    public StringToIntegerMapper integer() {
        return new StringToIntegerMapperImpl(StringToInteger.integer());
    }

    @Override
    public StringToIntegerMapper instance(boolean bigDecimal) {
        return new StringToIntegerMapperImpl(StringToInteger.instance(bigDecimal));
    }
}
