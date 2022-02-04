package com.justinblank.instantiator;

import java.lang.reflect.Constructor;
import java.util.*;

public class Invoker {

    private final ClassCreationResolver resolver;
    private final ValueSearcher searcher;

    public Invoker(ClassCreationResolver resolver) {
        this.resolver = resolver;
        searcher = new ValueSearcher(resolver);
    }

    /**
     * Instantiate an instance of the specified class and invoke the named method if possible.
     * @param className the fully qualified class name
     * @param methodName the name of the method
     * @param arguments the classes of the methods arguments
     * @param provided a provided set of types that can be constructed
     * @return an optional Invocation, or empty if it was not possible to invoke the method
     * @throws ReflectiveOperationException if the method could not be invoked
     */
    public Optional<Invocation> invoke(String className, String methodName, List<Class<?>> arguments, List<TypeSrc> provided) throws ReflectiveOperationException {
        if (!canResolveMethod(searcher, className, arguments, provided)) {
            return Optional.empty();
        }

        var c = Class.forName(className);
        var method = c.getMethod(methodName, arguments.toArray(new Class[0]));
        var recipient = instantiate(className, provided);
        if (recipient.isEmpty()) {
            return Optional.empty();
        }
        var argumentsObjects = new ArrayList<>();
        for (var argClass : arguments) {
            var argumentObject = searcher.resolve(new Type(argClass.getCanonicalName()), provided);
            if (argumentObject.isEmpty()) {
                return Optional.empty();
            }
            var instantiatedArgument = instantiate(argumentObject.get().type.typeString, provided);
            if (instantiatedArgument.isEmpty()) {
                return Optional.empty();
            }
            argumentsObjects.add(instantiatedArgument.get());
        }
        try {
            var obj = method.invoke(recipient.get(), argumentsObjects.toArray(new Object[0]));
            return Optional.of(Invocation.success(recipient.get(), argumentsObjects, obj));
        }
        // TODO: If our method actually threw a ReflectiveOperationException, we can't detect it. With more thought
        // about proper use of reflection, we might remove this check.
        catch (ReflectiveOperationException e) {
            throw e;
        }
        catch (Exception e) {
            return Optional.of(Invocation.exception(recipient.get(), argumentsObjects, e));
        }
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

    /**
     * Instantiate an instance of the given object, using any available constructors
     * @param className the fully qualified class name of the desired type
     * @param provided the list of ways to instantiate particular types provided
     * @return an instance of the object if it can be instantiated, otherwise empty
     */
    public Optional<Object> instantiate(String className, List<TypeSrc> provided) {
        var searcher = new ValueSearcher(resolver);
        var typeTree = searcher.resolve(new Type(className), provided);
        return typeTree.flatMap(t -> {
            try {
                if (t.isResolved()) {
                    if (t.typeSrc.sourceKind() == SourceKind.Constructor) {
                        return instantiateViaConstructor(t, provided);
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

    private Optional<Object> instantiateViaConstructor(TypeTree t, List<TypeSrc> provided) throws Exception {
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
