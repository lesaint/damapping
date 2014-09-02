package fr.javatronic.damapping.test.guava;

import fr.javatronic.damapping.annotation.Mapper;
import fr.javatronic.damapping.annotation.MapperFactory;
import fr.javatronic.damapping.test.guava.sub.B;
import fr.javatronic.damapping.test.guava.sub.C;

import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * MultiConstructorOnBusinessTypes -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class MultiConstructorOnBusinessTypes implements Function<C, String> {
  @MapperFactory
  public MultiConstructorOnBusinessTypes(A a) {
    // implementation does not matter
  }

  @MapperFactory
  public MultiConstructorOnBusinessTypes(B b) {
    // implementation does not matter
  }

  @Nullable
  @Override
  public String apply(@Nullable C input) {
    return "doesn't matter";
  }
}
