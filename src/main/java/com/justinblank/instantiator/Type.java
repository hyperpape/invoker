package com.justinblank.instantiator;

import java.util.Objects;
import java.util.Set;

class Type {

    public static final Type STRING = new Type("java.lang.String");
    public static final Type INT = new Type("java.lang.Integer");
    public static final Type PRIM_INT = new Type("int");
    public static final Type LONG = new Type("java.lang.Long");
    public static final Type PRIM_LONG = new Type("long");
    public static final Type BOOLEAN = new Type("java.lang.Boolean");
    public static final Type PRIM_BOOLEAN = new Type("boolean");
    public static final Type DOUBLE = new Type("java.lang.Double");
    public static final Type PRIM_DOUBLE = new Type("double");
    public static final Type FLOAT = new Type("java.lang.Float");
    public static final Type PRIM_FLOAT = new Type("float");
    public static final Type SHORT = new Type("java.lang.Short");
    public static final Type PRIM_SHORT = new Type("short");
    public static final Type CHAR = new Type("java.lang.Character");
    public static final Type PRIM_CHAR = new Type("char");

    public static final Set TRIVIAL_TYPES = Set.of(STRING, INT, PRIM_INT, BOOLEAN, PRIM_BOOLEAN, DOUBLE, PRIM_DOUBLE, FLOAT, PRIM_FLOAT, LONG, PRIM_LONG, SHORT, PRIM_SHORT, CHAR, PRIM_CHAR);

    final String typeString;

    Type(String typeString) {
        this.typeString = typeString;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return Objects.equals(typeString, type.typeString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeString);
    }

    @Override
    public String toString() {
        return "Type(" + typeString + ")";
    }
}
