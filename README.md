# Invoker

Supports constructing instances of arbitrary classes using their constructors and invoking methods on them. 

```java
    // constructs two random strings, then invokes string1.equals(string2);
    var invoker = new Invoker(new ClassPathResolver());
    invoker.invoke(String.class.getCanonicalName(), "equals", List.of(Object.class), DefaultTypes.DEFAULT_TYPES);
```

### Limitations

Arguments will be instantiated randomly, so classes with interesting invariants cannot be reliably instantiated. 

Arguments and objects can currently only be instantiated by directly calling their constructors, not by invoking methods
that return them.  