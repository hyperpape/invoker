package com.justinblank.instantiator;

import com.justinblank.instantiator.sample.ClassA;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValueSearcherTest {

    @Test
    public void testCanResolveValueSearcher() {
        var searcher = new ValueSearcher(new ClassPathResolver());
        var tree = searcher.resolve(new Type(ClassA.class.getCanonicalName()), DefaultTypes.DEFAULT_TYPES);
        assertTrue(tree.isPresent());
        assertTrue(tree.get().isResolved());
    }
}
