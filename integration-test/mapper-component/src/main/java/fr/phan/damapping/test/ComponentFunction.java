package fr.phan.damapping.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import fr.phan.damapping.annotation.Mapper;
import org.springframework.stereotype.Component;

/**
 * ComponentFunction -
 *
 * @author Sébastien Lesaint
 */
@Mapper
@Component
public class ComponentFunction implements Function<A, String> {
    @Nullable
    @Override
    public String apply(@Nullable A a) {
        return a.toString();
    }
}
