package com.ekino.lesaint.dozerannihilation.demo;

import javax.inject.Inject;

/**
 * AnyServiceImpl -
 *
 * @author Sébastien Lesaint
 */
public class AnyServiceImpl implements AnyService {

    private StringToIntegerMapperFactory stringToIntegerMapperFactory;
    private EnumIntegerToStringMapper enumIntegerToStringMapper;
    private IntegerToStringMapper integerToStringMapper;

    @Inject
    public AnyServiceImpl(StringToIntegerMapperFactory stringToIntegerMapperFactory,
                          EnumIntegerToStringMapper enumIntegerToStringMapper,
                          IntegerToStringMapper integerToStringMapper) {

        this.stringToIntegerMapperFactory = stringToIntegerMapperFactory;
        this.enumIntegerToStringMapper = enumIntegerToStringMapper;
        this.integerToStringMapper = integerToStringMapper;
    }


    @Override
    public Integer method1() {
        return stringToIntegerMapperFactory.integer().apply("1");
    }

    @Override
    public String method2() {
        return enumIntegerToStringMapper.apply(1);
    }

    @Override
    public String method3() {
        return integerToStringMapper.apply(1);
    }
}
