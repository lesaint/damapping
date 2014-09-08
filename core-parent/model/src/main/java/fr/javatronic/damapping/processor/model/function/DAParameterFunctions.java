package fr.javatronic.damapping.processor.model.function;

import fr.javatronic.damapping.processor.model.DAParameter;
import fr.javatronic.damapping.util.Function;

import javax.annotation.Nullable;

/**
 * DAParameterFunctions -
 *
 * @author Sébastien Lesaint
 */
public final class DAParameterFunctions {
  private DAParameterFunctions() {
    // prevents instantiation
  }

  public static Function<DAParameter, String> toName() {
    return DAParameterToName.INSTANCE;
  }

  private static enum DAParameterToName implements Function<DAParameter, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(DAParameter daParameter) {
      if (daParameter == null) {
        return null;
      }
      return daParameter.getName().getName();
    }
  }

  public static Function<DAParameter, String> toNamePrefixedWithThis() {
    return DAParameterToNamePrefixedWithThis.INSTANCE;
  }

  private static enum DAParameterToNamePrefixedWithThis implements Function<DAParameter, String> {
    INSTANCE;

    @Nullable
    @Override
    public String apply(@Nullable DAParameter daParameter) {
      if (daParameter == null) {
        return null;
      }
      return "this." + daParameter.getName().getName();
    }
  }
}
