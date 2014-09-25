package fr.javatronic.damapping.processor;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import javax.tools.JavaFileObject;

import com.google.testing.compile.CompileTester;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourceSubjectFactory;
import org.testng.annotations.Test;
import org.truth0.Truth;

/**
 * MapperMethodPresentTest -
 *
 * @author Sébastien Lesaint
 */
public class MapperMethodPresentTest {
  @Test
  public void compiling_empty_annotated_class_fails_because_there_is_no_mapper_method() throws Exception {
    JavaFileObject fileObject = JavaFileObjects.forSourceLines(
        "Empty",
        "import fr.javatronic.damapping.annotation.Mapper;",
        "@Mapper",
        "public class Empty {}"
    );
    assertThat(fileObject)
         .failsToCompile()
         .withErrorContaining("Mapper must have at one and only one method qualifying as mapper method")
         .in(fileObject)
         .onLine(3);
  }

  private CompileTester assertThat(String fullyQualifiedName, String... sourceLines) {
    return assertThat(JavaFileObjects.forSourceLines(fullyQualifiedName, sourceLines));
  }

  private CompileTester assertThat(String fileName) throws URISyntaxException, MalformedURLException {
    return assertThat(JavaFileObjects.forResource(getClass().getResource(fileName).toURI().toURL()));
  }

  private CompileTester assertThat(JavaFileObject fileObject) {
    return Truth.ASSERT.about(JavaSourceSubjectFactory.javaSource())
                                       .that(fileObject)
                                       .processedWith(new DAAnnotationProcessor());
  }
}
