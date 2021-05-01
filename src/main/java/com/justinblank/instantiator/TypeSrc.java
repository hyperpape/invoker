package com.justinblank.instantiator;

import java.util.List;
import java.util.function.Supplier;

public class TypeSrc {

    private final Type type;
    private final SourceKind sourceKind;
    private final List<Type> requiredTypes;
    private final Object o;
    private final Supplier<Object> s;

    TypeSrc(Type type, SourceKind sourceKind, List<Type> requiredTypes) {
        this(type, sourceKind, requiredTypes, null, null);
    }

    private TypeSrc(Type type, SourceKind sourceKind, List<Type> requiredTypes, Object object, Supplier<Object> supplier) {
        this.type = type;
        this.sourceKind = sourceKind;
        this.requiredTypes = requiredTypes;
        this.o = object;
        this.s = supplier;
    }

    public static TypeSrc fromObject(Type type, Object o) {
        return new TypeSrc(type, SourceKind.Provided, List.of(), o, null);
    }

    public static TypeSrc fromSupplier(Type type, Supplier<Object> s) {
        return new TypeSrc(type, SourceKind.Generatable, List.of(), null, s);
    }

    Type type() {
        return type;
    }

    SourceKind sourceKind() {
        return sourceKind;
    }

    List<Type> requiredTypes() {
        return requiredTypes;
    }

    Supplier<?> supplier() {
        return s;
    }
}
