package fr.phan.damapping.test;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.annotation.MapperFactoryMethod;
import fr.phan.damapping.test.sub.B;
import fr.phan.damapping.test.sub.C;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * MultiConstructorWithGenerics -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class MultiConstructorWithGenerics implements Function<Map<C, Set<BigDecimal>>, Optional<String>> {
  @MapperFactoryMethod
  public MultiConstructorWithGenerics(Set<A> as) {
    // implementation does not matter
  }

  @MapperFactoryMethod
  public MultiConstructorWithGenerics(List<B> bs) {
    // implementation does not matter
  }

  @Nullable
  @Override
  public Optional<String> apply(@Nullable Map<C, Set<BigDecimal>> input) {
    return Optional.of("doesn't matter");
  }
}
