package fr.phan.damapping.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.subpackage.OutOfPackage;

/**
 * WildcardGenerics -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class WildcardGenerics implements Function<Optional<? extends OutOfPackage>, String> {
    @Nullable
    @Override
    public String apply(@Nullable Optional<? extends OutOfPackage> optional) {
        return "resultDoesNotMatter";
    }
}
