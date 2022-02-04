package com.justinblank.instantiator;

import java.util.Optional;

/**
 * Returns available methods to construct a given class.
 */
public interface ClassCreationResolver {

    Optional<TypeSrc> requiredTypes(String className);

    default boolean isPrimitive(String className) {
        switch (className) {
            case "boolean":
            case "int":
            case "long":
            case "float":
            case "double":
            case "short":
            case "char":
                return true;
            default:
                return false;
        }
    }
}
