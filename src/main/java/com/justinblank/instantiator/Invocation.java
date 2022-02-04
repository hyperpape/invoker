package com.justinblank.instantiator;

import java.util.List;

public class Invocation {

    public final Object instance;
    public final List<Object> arguments;
    public final Object result;
    public final Exception exception;

    @Override
    public String toString() {
        return "Invocation{" +
                "instance=" + instance +
                ", arguments=" + arguments +
                ", result=" + result +
                ", exception=" + exception +
                '}';
    }

    private Invocation(Object o, List<Object> args, Object result, Exception e) {
        this.instance = o;
        this.arguments = args;
        this.result = result;
        this.exception = e;
    }

    public static Invocation success(Object o, List<Object> args, Object result) {
        return new Invocation(o, args, result, null);
    }

    public static Invocation exception(Object o, List<Object> args, Exception e) {
        return new Invocation(o, args, null, e);
    }
}
