package fr.javatronic.damapping.test.guava;

import java.util.List;
import javax.annotation.Nullable;
import com.google.common.base.Function;

/**
 * ConstructorWithGenericTypeAsParameter -
 *
 * @author Sébastien Lesaint
 */
public class ConstructorWithGenericTypeAsParameter implements Function<Integer, InPackage> {
  private final List<InPackage> packages;

  public ConstructorWithGenericTypeAsParameter(List<InPackage> packages) {
    this.packages = packages;
  }

  @Nullable
  @Override
  public InPackage apply(@Nullable Integer input) {
    // implementation does not matter
    return null;
  }
}
