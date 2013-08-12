package java.com.ekino.lesaint.dozerannihilation.demo;

/**
 * StringToIntegerMapperFactory -
 *
 * @author Sébastien Lesaint
 */
public interface StringToIntegerMapperFactory {
    StringToIntegerMapper bigDecimal();

    StringToIntegerMapper integer();

    StringToIntegerMapper instance(boolean bigDecimal);
}
