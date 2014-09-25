package fr.javatronic.damapping.processor;

import javax.tools.JavaFileObject;

import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.testng.annotations.Test;
import org.truth0.Truth;

/**
 * DefaultPackageDedicatedClassTest - Tests generating classes from a dedicated class in the default package.
 *
 * @author Sébastien Lesaint
 */
public class DefaultPackageDedicatedClassTest {
  @Test
  public void compiling_mapper_in_default_package_is_successfull() throws Exception {
    assertThat("MostSimple",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "",
        "@Mapper",
        "public class MostSimple {",
        "  public String map(Integer input) {",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    ).compilesWithoutError();
  }

  @Test
  public void compiling_mapperFactory_in_default_package_is_successfull() throws Exception {
    assertThat("MostSimpleFactory",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "import fr.javatronic.damapping.annotation.MapperFactory;",
        "",
        "@Mapper",
        "public class MostSimpleFactory {",
        "  @MapperFactory",
        "  public MostSimpleFactory(boolean a) {",
        "    // implementation does not matter",
        "  }",
        "",
        "  public String apply(Integer input) {",
        "    return null; // implementation does not matter",
        "  }",
        "}"
    ).compilesWithoutError();
  }

  private CompileTester assertThat(String fullyQualifiedName, String... sourceLines) {
    return assertThat(JavaFileObjects.forSourceLines(fullyQualifiedName, sourceLines));
  }

  private CompileTester assertThat(JavaFileObject fileObject) {
    return Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                .that(fileObject)
                .processedWith(new DAAnnotationProcessor());
  }
}
