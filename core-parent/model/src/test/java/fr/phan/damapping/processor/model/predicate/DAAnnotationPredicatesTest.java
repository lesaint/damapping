package fr.phan.damapping.processor.model.predicate;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.processor.model.DAAnnotation;
import fr.phan.damapping.processor.model.factory.DATypeFactory;

import org.testng.annotations.Test;

import static fr.phan.damapping.processor.model.predicate.DAAnnotationPredicates.isMapper;
import static fr.phan.damapping.processor.model.predicate.DAAnnotationPredicates.isSpringComponent;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * DAAnnotationPredicatesTest -
 *
 * @author Sébastien Lesaint
 */
public class DAAnnotationPredicatesTest {

  @Test
  public void testIsMapper() throws Exception {
    assertThat(isMapper().apply(null)).isFalse();
    assertThat(isMapper().apply(new DAAnnotation(DATypeFactory.declared("com.toto.Mapper")))).isFalse();
    assertThat(isMapper().apply(new DAAnnotation(DATypeFactory.declared("org.acme.zoom.Foo")))).isFalse();
    assertThat(isMapper().apply(new DAAnnotation(DATypeFactory.declared("Bar")))).isFalse();
    assertThat(isMapper().apply(new DAAnnotation(DATypeFactory.declared(Mapper.class.getName())))).isTrue();
  }

  @Test
  public void testIsSpringComponent() throws Exception {
    assertThat(isSpringComponent().apply(null)).isFalse();
    assertThat(isSpringComponent().apply(new DAAnnotation(DATypeFactory.declared("com.toto.Component")))).isFalse();
    assertThat(isSpringComponent().apply(new DAAnnotation(DATypeFactory.declared("org.acme.zoom.Foo")))).isFalse();
    assertThat(isSpringComponent().apply(new DAAnnotation(DATypeFactory.declared("Bar")))).isFalse();
    assertThat(isSpringComponent().apply(new DAAnnotation(DATypeFactory.declared("org.springframework.stereotype.Component")))).isTrue();
  }
}
