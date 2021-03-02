package com.justinblank.instantiator;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 *
 */
public class Invoker {

    private final ClassCreationResolver resolver;

    public Invoker(ClassCreationResolver resolver) {
        this.resolver = resolver;
    }

    public boolean invoke(String className, String methodName, List<Class<?>> arguments, List<TypeSrc> provided) throws Exception {
        var searcher = new ValueSearcher(resolver);
        if (!canResolveMethod(searcher, className, arguments, provided)) {
            return false;
        }

        var c = Class.forName(className);
        var method = c.getMethod(methodName, arguments.toArray(new Class[0]));
        var recipient = instantiate(className, provided);
        if (recipient.isEmpty()) {
            return false;
        }
        var argumentsObjects = new ArrayList<>();
        for (var argClass : arguments) {
            var argumentObject = searcher.resolve(new Type(argClass.getCanonicalName()), provided);
            if (argumentObject.isEmpty()) {
                return false;
            }
            argumentsObjects.add(argumentObject.get());
        }
        method.invoke(recipient.get(), argumentsObjects.toArray(new Object[0]));
        return true;
    }

    private boolean canResolveMethod(ValueSearcher searcher, String className, List<Class<?>> arguments, List<TypeSrc> provided) {
        return searcher.resolve(new Type(className), provided).map(typeTree -> {
            if (!typeTree.isResolved()) {
                return false;
            }
            for (var argumentClass : arguments) {
                var argumentTree = searcher.resolve(new Type(argumentClass.getCanonicalName()), provided);
                if (argumentTree.isEmpty() || !argumentTree.get().isResolved()) {
                    return false;
                }
            }
            return true;
        }).orElse(Boolean.FALSE);
    }

    public Optional<Object> instantiate(String className, List<TypeSrc> provided) {
        var searcher = new ValueSearcher(resolver);
        var typeTree = searcher.resolve(new Type(className), provided);
        return typeTree.flatMap(t -> {
            try {
                if (t.isResolved()) {
                    if (t.typeSrc.sourceKind() == SourceKind.Constructor) {
                        return Optional.of(instantiateViaConstructor(t, provided));
                    }
                    else if (t.typeSrc.sourceKind() == SourceKind.Generatable) {
                        return Optional.of(t.typeSrc.supplier().get());
                    }
                    else {
                        return Optional.empty();
                    }
                }
                else {
                    return Optional.empty();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
        });
    }

    private Object instantiateViaConstructor(TypeTree t, List<TypeSrc> provided) throws Exception {
        var arguments = new Object[t.children.size()];
        for (var i = 0; i < t.children.size(); i++) {
            var next = instantiate(t.children.get(i).type.typeString, provided);
            if (next.isEmpty()) {
                return Optional.empty();
            }
            arguments[i] = next.get();
        }
        return Optional.of(getConstructor(t.typeSrc).newInstance(arguments));
    }

    private Constructor<?> getConstructor(TypeSrc typeSrc) throws Exception {
        var c = Class.forName(typeSrc.type().typeString);
        var types = typeSrc.requiredTypes();
        var arguments = new Class[types.size()];
        for (var i = 0; i < types.size(); i++) {
            var t = types.get(i);
            if (resolver.isPrimitive(t.typeString)) {
                arguments[i] = getClassForPrimitive(t.typeString);
            }
            else {
                arguments[i] = Class.forName(types.get(i).typeString);
            }
        }
        return c.getDeclaredConstructor(arguments);
    }

    private Class<?> getClassForPrimitive(String typeString) {
        switch (typeString) {
            case "boolean":
                return boolean.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "double":
                return double.class;
            case "float":
                return float.class;
            case "short":
                return short.class;
            case "char":
                return char.class;
            default:
                return null;
        }
    }
}
