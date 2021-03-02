package com.justinblank.instantiator;

import com.justinblank.instantiator.sample.Circular1;
import com.justinblank.instantiator.sample.RequiresBoolean;
import com.justinblank.instantiator.sample.RequiresPrimitiveBoolean;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvokerTest {

    @Test
    public void testInstantiateString() {
        var invoker = new Invoker(new ClassPathResolver());
        assertThat(invoker.instantiate(String.class.getCanonicalName(), DefaultTypes.DEFAULT_TYPES)).isNotEmpty();
    }

    @Test
    public void testInstantiationBasic() {
        var invoker = new Invoker(new ClassPathResolver());
        assertThat(invoker.instantiate(ClassPathResolver.class.getCanonicalName(), List.of())).isNotEmpty();
    }

    @Test
    public void testCircularClassesTerminate() {
        var invoker = new Invoker(new ClassPathResolver());
        assertThat(invoker.instantiate(Circular1.class.getCanonicalName(), List.of()).isEmpty());
    }

    @Test
    public void testInstantiationFromString() {
        var invoker = new Invoker(new ClassPathResolver());
        var created = invoker.instantiate("com.justinblank.instantiator.sample.ClassA", DefaultTypes.DEFAULT_TYPES);
        assertThat(created).isNotEmpty();
    }

    @Test
    public void testInstantiationFromBoolean() {
        var invoker = new Invoker(new ClassPathResolver());
        var created = invoker.instantiate(RequiresBoolean.class.getCanonicalName(), DefaultTypes.DEFAULT_TYPES);
        assertThat(created).isNotEmpty();
    }

    @Test
    public void testInstantiationFromPrimitiveBoolean() {
        var invoker = new Invoker(new ClassPathResolver());
        var created = invoker.instantiate(RequiresPrimitiveBoolean.class.getCanonicalName(), DefaultTypes.DEFAULT_TYPES);
        assertThat(created).isNotEmpty();
    }

    @Test
    public void testInvoker() throws Exception {
        var invoker = new Invoker(new ClassPathResolver());
        assertTrue(invoker.invoke(String.class.getCanonicalName(), "equals", List.of(Object.class), DefaultTypes.DEFAULT_TYPES));
    }
}