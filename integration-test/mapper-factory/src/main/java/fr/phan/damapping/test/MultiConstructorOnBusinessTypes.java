package fr.phan.damapping.test;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.annotation.MapperFactoryMethod;
import fr.phan.damapping.test.sub.B;
import fr.phan.damapping.test.sub.C;

import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * MultiConstructorOnBusinessTypes -
 *
 * @author Sébastien Lesaint
 */
@Mapper
public class MultiConstructorOnBusinessTypes implements Function<C, String> {
  @MapperFactoryMethod
  public MultiConstructorOnBusinessTypes(A a) {
    // implementation does not matter
  }

  @MapperFactoryMethod
  public MultiConstructorOnBusinessTypes(B b) {
    // implementation does not matter
  }

  @Nullable
  @Override
  public String apply(@Nullable C input) {
    return "doesn't matter";
  }
}
