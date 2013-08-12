package java.com.ekino.lesaint.dozerannihilation.demo;

import com.ekino.lesaint.dozerannihilation.demo.IntegerToString;

class IntegerToStringMapperFactory {

    public static IntegerToString instance() {
        return new IntegerToString();
    }
}
