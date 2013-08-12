package com.ekino.lesaint.dozerannihilation.demo;

import javax.annotation.Resource;

/**
 * AnyServiceImpl -
 *
 * @author Sébastien Lesaint
 */
public class AnyServiceImpl implements AnyService {
    @Resource
    private StringToIntegerMapperFactory stringToIntegerMapperFactory;
    @Resource
    private EnumIntegerToStringMapper enumIntegerToStringMapper;
    @Resource
    private IntegerToStringMapper integerToStringMapper;


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
