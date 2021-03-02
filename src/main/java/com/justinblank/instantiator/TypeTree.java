package com.justinblank.instantiator;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class TypeTree {

    final Type type;
    TypeSrc typeSrc;
    List<TypeTree> children;
    boolean resolved = false;
    final Set<TypeTree> waiters = new HashSet<>();

    TypeTree(Type t) {
        Objects.requireNonNull(t);
        this.type = t;
    }

    TypeTree(Type t, TypeSrc typeSrc, List<TypeTree> children) {
        Objects.requireNonNull(t);
        this.type = t;
        this.typeSrc = typeSrc;
        this.children = children;
    }

    int size() {
        int size = 1;
        for (var child : children) {
            size += child.size();
        }
        return size;
    }

    void setTypeSrc(TypeSrc typeSrc) {
        this.typeSrc = typeSrc;
    }

    void addWaiter(TypeTree t) {
        waiters.add(t);
    }

    void clearWaiters() {
        waiters.clear();
    }

    boolean isResolved() {
        return resolved;
    }

    void markResolved() {
        this.resolved = true;
    }

    public boolean equals(Object other) {
        return this == other || (other instanceof TypeTree && ((TypeTree) other).type.equals(this.type));
    }

}
