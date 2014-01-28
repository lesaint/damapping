package fr.phan.damapping.processor.model.predicate;

import fr.phan.damapping.processor.model.DAInterface;
import fr.phan.damapping.processor.model.DAType;
import fr.phan.damapping.processor.model.DATypeKind;
import fr.phan.damapping.processor.model.factory.DANameFactory;
import fr.phan.damapping.processor.model.factory.DATypeFactory;

import com.google.common.base.Function;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAInterfacePredicatesTest -
 *
 * @author Sébastien Lesaint
 */
public class DAInterfacePredicatesTest {
  @Test
  public void guavaFunction_supports_null() throws Exception {
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(null)).isFalse();
  }

  @Test
  public void guavaFunction_fails_if_DAType_has_no_declared_name() throws Exception {
    DAInterface noDeclaredName = new DAInterface(DAType.builder(DATypeKind.CHAR, DANameFactory.from("char")).build());
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(noDeclaredName)).isFalse();
  }

  @Test
  public void guavaFunction_success_only_if_declaredname_is_guava_function() throws Exception {
    DAInterface daInterface = new DAInterface(DATypeFactory.from(String.class));
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(daInterface)).isFalse();

    DAInterface guavaFunction = new DAInterface(DATypeFactory.declared("com.google.common.base.Function"));
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(guavaFunction)).isTrue();

    DAInterface guavaFunctionFromClass = new DAInterface(DATypeFactory.from(Function.class));
    assertThat(DAInterfacePredicates.isGuavaFunction().apply(guavaFunctionFromClass)).isTrue();
  }
}
