package fr.javatronic.damapping.processor;

import javax.tools.JavaFileObject;

import com.google.testing.compile.JavaFileObjects;
import org.testng.annotations.Test;

/**
 * ConstructorValidationTest -
 *
 * @author Sébastien Lesaint
 */
public class ConstructorValidationTest extends AbstractCompilationTest {
  @Test
  public void compilation_fails_if_constructor_is_private() throws Exception {
    JavaFileObject fileObject = constructorMapper("private");

    assertThat(fileObject)
        .failsToCompile()
        .withErrorContaining("Class does not expose an accessible default constructor")
        .in(fileObject)
        .onLine(4);
  }

  @Test
  public void compilation_succesds_if_constructor_is_public() throws Exception {
    assertThat(constructorMapper("public")).compilesWithoutError();
  }

  @Test
  public void compilation_succesds_if_constructor_is_protected() throws Exception {
    assertThat(constructorMapper("protected")).compilesWithoutError();
  }

  @Test
  public void compilation_succesds_if_constructor_is_default_protected() throws Exception {
    assertThat(constructorMapper("")).compilesWithoutError();
  }

  private static JavaFileObject constructorMapper(String constructorModifier) {
    return JavaFileObjects.forSourceLines(
        "ConstructorMapper",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class ConstructorMapper {",
        "  " + constructorModifier + " ConstructorMapper() {",
        "  }",
        "  public Integer apply(String input) {",
        "    return null;",
        "  }",
        "}"
    );
  }
}
