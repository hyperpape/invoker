package com.justinblank.instantiator;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Resolve class constructors by loading the class and using reflection.
 */
public class ClassPathResolver implements ClassCreationResolver {

    private Optional<Constructor<?>> getNoArgConstructor(Class<?> targetClass) {
        for (var constructor : getPrioritizedConstructors(targetClass)) {
            if (constructor.getParameterCount() == 0) {
                return Optional.of(constructor);
            }
        }
        return Optional.empty();
    }

    private Constructor<?>[] getPrioritizedConstructors(Class<?> c) {
        Constructor<?>[] constructors = c.getDeclaredConstructors();
        Arrays.sort(constructors, Comparator.comparing(Constructor::getParameterCount));
        return constructors;
    }

    @Override
    public Optional<TypeSrc> requiredTypes(String className) {
        try {
            var targetType = new Type(className);
            if (isPrimitive(className)) {
                return Optional.of(new TypeSrc(targetType, SourceKind.Provided, Collections.emptyList()));
            }
            var c = Class.forName(className);
            var typeSrc = getNoArgConstructor(c).map(c1 -> new TypeSrc(targetType, SourceKind.Constructor, List.of()));
            return typeSrc.or(() -> {
                var constructors = getPrioritizedConstructors(c);
                if (constructors.length > 0) {
                    var constructor =  constructors[0];
                    List<Type> arguments = new ArrayList<>();
                    for (Class<?> argumentClass : constructor.getParameterTypes()) {
                        arguments.add(new Type(argumentClass.getCanonicalName()));
                    }
                    return Optional.of(new TypeSrc(targetType, SourceKind.Constructor, arguments));
                }
                return Optional.empty();
            });
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e); // YOLO;
        }
    }

}
