package com.ekino.lesaint.dozerannihilation.processor.impl;

import java.util.Collections;
import java.util.List;
import javax.lang.model.type.TypeKind;

import com.google.common.collect.ImmutableList;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DATypeTest -
 *
 * @author Sébastien Lesaint
 */
public class DATypeTest {
    @Test
    public void isArray() throws Exception {
        assertThat(daType(TypeKind.ARRAY).isArray()).isTrue();
        assertThat(daType(TypeKind.DECLARED).isArray()).isFalse();
    }

    @Test
    public void getImports_no_typeArgs() throws Exception {
        DAType daType = daType("toto");
        assertThat(daType.getImports()).extracting("name").containsOnly("toto");
    }

    @Test
    public void getImports_one_typeArg() throws Exception {
        DAType daType = daType(
                "toto",
                ImmutableList.of(daType("titi"))
        );
        assertThat(daType.getImports()).extracting("name").containsOnly("toto", "titi");
    }

    @Test
    public void getImports_multiple_typeArg() throws Exception {
        DAType daType = daType(
                "toto",
                ImmutableList.of(daType("titi"), daType("tutu"))
        );
        assertThat(daType.getImports()).extracting("name").containsOnly("toto", "titi", "tutu");
    }

    @Test
    public void getImports_one_typeArg_recursive() throws Exception {
        DAType daType = daType(
                "toto",
                ImmutableList.of(
                        daType("titi", ImmutableList.of(daType("tutu")))
                )
        );
        assertThat(daType.getImports()).extracting("name").containsOnly("toto", "titi", "tutu");
    }

    @Test
    public void testGetImports() throws Exception {
        DAType daType = daType("toto");
        assertThat(daType.getImports()).extracting("name").containsOnly("toto");

        daType.typeArgs = ImmutableList.of(
                daType("titi")
        );
        assertThat(daType.getImports()).extracting("name").containsOnly("toto", "titi");
    }

    private static DAType daType(TypeKind kind) {
        DAType res = new DAType();
        res.kind = kind;
        res.typeArgs = Collections.emptyList();
        return res;
    }

    private static DAType daType(String qualifiedName) {
        return daType(qualifiedName, Collections.<DAType>emptyList());
    }

    private static DAType daType(String qualifiedName, List<DAType> typeArgs) {
        DAType res = new DAType();
        res.kind = TypeKind.DECLARED;
        res.qualifiedName = DANameFactory.from(qualifiedName);
        res.typeArgs = typeArgs;
        return res;
    }
}
