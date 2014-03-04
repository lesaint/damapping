package fr.phan.damapping.intellij.plugin.integration.psiparsing;

/**
 * MapperEnumPsiParsingAndGenerateSourceTest -
 *
 * @author Sébastien Lesaint
 */
public class MapperEnumPsiParsingAndGenerateSourceTest extends AbstractPsiParsingAndGenerateSourceTest {

  public MapperEnumPsiParsingAndGenerateSourceTest() {
    super("mapper-enum");
  }

  /**
   * Tests Psi parsing, DASourceClass validation and generated class source generation for @Mapper class : EnumInstancedGuavaFunction.
   */
  public void testEnumInstancedGuavaFunction() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

  /**
   * Tests Psi parsing, DASourceClass validation and generated class source generation for @Mapper class : GenericsEverywhere.
   */
  public void testGenericsEverywhere() throws Exception {
    doPsiParsingAndGenerateSourceTest();
  }

}
