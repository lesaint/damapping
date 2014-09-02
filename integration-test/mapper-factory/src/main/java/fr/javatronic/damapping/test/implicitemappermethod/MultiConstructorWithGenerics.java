package fr.javatronic.damapping.test.implicitemappermethod;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.annotation.MapperFactory;
import fr.javatronic.damapping.test.implicitemappermethod.sub.B;
import fr.javatronic.damapping.test.implicitemappermethod.sub.C;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import com.google.common.base.Optional;

/**
 * MultiConstructorWithGenerics -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class MultiConstructorWithGenerics {
  @MapperFactory
  public MultiConstructorWithGenerics(Set<A> as) {
    // implementation does not matter
  }

  @MapperFactory
  public MultiConstructorWithGenerics(List<B> bs) {
    // implementation does not matter
  }

  @Nullable
  public Optional<String> apply(@Nullable Map<C, Set<BigDecimal>> input) {
    return Optional.of("doesn't matter");
  }
}
