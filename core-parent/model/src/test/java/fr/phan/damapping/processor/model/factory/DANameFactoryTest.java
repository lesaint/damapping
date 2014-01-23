package fr.phan.damapping.processor.model.factory;

import fr.phan.damapping.processor.model.DAName;
import fr.phan.damapping.processor.model.DATypeKind;
import org.testng.annotations.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DANameFactoryTest -
 *
 * @author: Sébastien Lesaint
 */
public class DANameFactoryTest {

    private static final String TOTO = "toto";

    @Test
    public void from_string() throws Exception {
        assertThat(DANameFactory.from(TOTO).getName()).isEqualTo(TOTO);
    }

    @Test
    public void fromPrimitiveKind() throws Exception {
        for (DATypeKind typeKind : DATypeKind.values()) {
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
