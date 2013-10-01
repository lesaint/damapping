package com.ekino.lesaint.dozerannihilation.test;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import com.ekino.lesaint.dozerannihilation.annotation.Mapper;

@Mapper
public enum GenericsEverywhere implements Function<Optional<Integer[]>, Collection<Predicate<Map<Long, File>>>> {
    INSTANCE;

    @Override
    public Collection<Predicate<Map<Long, File>>> apply(@Nullable Optional<Integer[]> input) {
        return Collections.emptyList();
    }
}
