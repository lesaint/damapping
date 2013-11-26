package fr.phan.damapping.test;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.annotation.MapperFactoryMethod;
import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.math.BigDecimal;

/**
 * ConstructorWithParameter -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class ConstructorWithParameter implements Function<BigDecimal, String> {
    private final String salt;

    @MapperFactoryMethod
    public ConstructorWithParameter(String salt) {
        this.salt = salt;
    }

    @Nullable
    @Override
    public String apply(@Nullable BigDecimal bigDecimal) {
        return bigDecimal + "-" + salt;
    }
}
