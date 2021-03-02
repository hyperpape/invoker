package com.justinblank.instantiator;

import java.util.*;
import java.util.stream.Collectors;

class ValueSearcher {

    private final ClassCreationResolver resolver;

    private final Map<Type, TypeTree> knownTypes = new HashMap<>();
    private final Stack<Type> pending = new Stack<>();

    ValueSearcher(ClassCreationResolver resolver) {
        this.resolver = resolver;
    }

    Optional<TypeTree> resolve(Type t, List<TypeSrc> provided) {
        for (TypeSrc typeSrc : provided) {
            TypeTree typeTree = new TypeTree(typeSrc.type(), typeSrc, null);
            typeTree.markResolved();
            knownTypes.put(typeSrc.type(), typeTree);
        }

        int exploration = 0;
        enqueue(t);
        // Do an initial exploration phase...
        while (exploration < 1000 && !pending.isEmpty()) {
            exploration++;
            Type typeToResolve = pending.pop();
            var typeSrc = resolver.requiredTypes(typeToResolve.typeString);
            typeSrc.ifPresent((ts) -> handleNewTypeSrc(t, ts));
        }
        var typeTree = knownTypes.get(t);
        if (typeTree != null && typeTree.isResolved()) {
            return Optional.of(typeTree);
        }
        return Optional.empty();
    }

    private void handleNewTypeSrc(Type t, TypeSrc typeSrc) {
        if (typeSrc.sourceKind() == SourceKind.Constructor) {
            if (!knownTypes.containsKey(t)) {
                var tree = storeTreeInKnownTypes(t, typeSrc);
                if (tree.isResolved()) {
                    processWaiters(tree);
                }
            }
        }
    }

    private void processWaiters(TypeTree tree) {
        for (var waiter : tree.waiters) {
            if (canResolve(waiter)) {
                waiter.markResolved();
            }
        }
        tree.clearWaiters();
    }

    private TypeTree storeTreeInKnownTypes(Type t, TypeSrc typeSrc) {
        List<TypeTree> children = typeSrc.requiredTypes().stream().map(childType -> {
            var childTree = knownTypes.get(childType);
            if (childTree != null) {
                return childTree;
            } else {
                childTree = new TypeTree(childType);
                enqueue(childType);
                knownTypes.put(childType, childTree);
                childTree.addWaiter(knownTypes.get(t));
                return childTree;
            }
        }).collect(Collectors.toList());
        var tree = new TypeTree(t, typeSrc, children);
        if (canResolve(tree)) {
            tree.markResolved();
        }

        knownTypes.put(t, tree);
        return tree;
    }

    private boolean canResolve(TypeTree tree) {
        return tree.children.stream().allMatch(TypeTree::isResolved);
    }

    private void enqueue(Type t) {
        if (!knownTypes.containsKey(t)) {
            pending.add(t);
        }
    }
}
