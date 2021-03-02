package com.justinblank.instantiator;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;

public class DefaultTypes {

    private static final Random random = new Random();

    public static final List<TypeSrc> DEFAULT_TYPES = List.of(
            TypeSrc.fromSupplier(Type.INT, random::nextInt),
            TypeSrc.fromSupplier(Type.PRIM_INT, random::nextInt),
            TypeSrc.fromSupplier(Type.LONG, random::nextLong),
            TypeSrc.fromSupplier(Type.PRIM_LONG, random::nextLong),
            TypeSrc.fromSupplier(Type.DOUBLE, random::nextDouble),
            TypeSrc.fromSupplier(Type.PRIM_DOUBLE, random::nextDouble),
            TypeSrc.fromSupplier(Type.FLOAT, random::nextFloat),
            TypeSrc.fromSupplier(Type.PRIM_FLOAT, random::nextFloat),

            TypeSrc.fromSupplier(Type.BOOLEAN, random::nextBoolean),
            TypeSrc.fromSupplier(Type.PRIM_BOOLEAN, random::nextBoolean),

            TypeSrc.fromSupplier(Type.SHORT, () -> random.nextInt(Short.MAX_VALUE * 2) - Short.MAX_VALUE),
            TypeSrc.fromSupplier(Type.PRIM_SHORT, () -> random.nextInt(Short.MAX_VALUE * 2) - Short.MAX_VALUE),

            // TODO: lazy lazy lazy
            TypeSrc.fromSupplier(Type.CHAR, () -> (char) random.nextInt(127)),
            TypeSrc.fromSupplier(Type.PRIM_CHAR, () -> (char) random.nextInt(127)),

            TypeSrc.fromSupplier(Type.STRING, () -> RandomStringUtils.random(10))
    );
}
