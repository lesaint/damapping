package fr.phan.damapping.test;

import javax.annotation.Nullable;
import com.google.common.base.Function;

import fr.phan.damapping.annotation.Mapper;
import fr.phan.damapping.test.subpackage.OutOfPackage;

@Mapper
public class HandleCustomTypes implements Function<OutOfPackage, InPackage> {

    @Override
    public InPackage apply(@Nullable OutOfPackage input) {
        return new InPackage();
    }
}
