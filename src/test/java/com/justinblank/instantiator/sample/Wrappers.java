package com.justinblank.instantiator.sample;

import java.util.List;

public class Wrappers {

    public static List<String> CLASS_NAMES = List.of(RequiresInt.class.getCanonicalName(), RequiresLong.class.getCanonicalName(), RequiresBoolean.class.getCanonicalName());

    public static class RequiresInt {
        public RequiresInt(Integer i) {
        }
    }

    public static class RequiresLong {
        public RequiresLong(Long l) {
        }
    }


}
