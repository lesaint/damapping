package java.com.ekino.lesaint.dozerannihilation.demo;


class IntegerToStringMapperImpl implements IntegerToStringMapper {

    @Override
    public String apply(Integer input) {
        return IntegerToStringMapperFactory.instance().apply(input);
    }
}
