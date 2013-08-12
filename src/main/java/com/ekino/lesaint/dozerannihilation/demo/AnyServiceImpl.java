package com.ekino.lesaint.dozerannihilation.demo;

import java.com.ekino.lesaint.dozerannihilation.demo.EnumIntegerToStringMapper;
import java.com.ekino.lesaint.dozerannihilation.demo.StringToIntegerMapperFactory;
import javax.annotation.Resource;

/**
 * AnyServiceImpl -
 *
 * @author Sébastien Lesaint
 */
//@Component
public class AnyServiceImpl implements AnyService {
    @Resource
    private StringToIntegerMapperFactory stringToIntegerMapperFactory;
    @Resource
    private EnumIntegerToStringMapper enumIntegerToStringMapper;


    @Override
    public void method1() {
        stringToIntegerMapperFactory.integer().apply("1");
    }

    @Override
    public void method2() {
        enumIntegerToStringMapper.apply(1);
    }
}
