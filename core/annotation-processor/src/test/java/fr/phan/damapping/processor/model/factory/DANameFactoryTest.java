package fr.phan.damapping.processor.model.factory;

import fr.phan.damapping.processor.model.DAName;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * DANameFactoryTest -
 *
 * @author: Sébastien Lesaint
 */
public class DANameFactoryTest {

    public static final String TOTO = "toto";

    @Test
    public void from_string() throws Exception {
        assertThat(DANameFactory.from(TOTO).getName()).isEqualTo(TOTO);
    }

    @Test
    public void from_Name() throws Exception {
        Name mock = Mockito.mock(Name.class);
        when(mock.toString()).thenReturn(TOTO);
        assertThat(DANameFactory.from(mock).getName()).isEqualTo(TOTO);
    }

    @Test
    public void fromPrimitiveKind() throws Exception {
        for (TypeKind typeKind : TypeKind.values()) {
            if (typeKind.isPrimitive()) {
                DAName daName = DANameFactory.fromPrimitiveKind(typeKind);
                assertThat(daName.getName()).isEqualTo(typeKind.name().toLowerCase(Locale.US));
            }
        }
    }

    @Test
    public void simpleFromQualified() throws Exception {
        assertThat(DANameFactory.simpleFromQualified(DANameFactory.from(TOTO)).getName()).isEqualTo(TOTO);
        assertThat(DANameFactory.simpleFromQualified(DANameFactory.from("a." + TOTO)).getName()).isEqualTo(TOTO);
        assertThat(DANameFactory.simpleFromQualified(DANameFactory.from("d.b.a." + TOTO)).getName()).isEqualTo(TOTO);
    }

    @Test
    public void wildcard() throws Exception {
        assertThat(DANameFactory.wildcard().getName()).isEqualTo("?");
    }

    @Test
    public void voidName() throws Exception {
        assertThat(DANameFactory.voidDAName().getName()).isEqualTo("void");
    }
}
