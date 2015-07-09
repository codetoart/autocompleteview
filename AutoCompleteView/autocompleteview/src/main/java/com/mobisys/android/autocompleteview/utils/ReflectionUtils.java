package com.mobisys.android.autocompleteview.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by priyank on 6/24/15.
 */
public class ReflectionUtils {
    public static List<Method> getFieldsUpTo(@NonNull Class<?> startClass,
                                                @Nullable Class<?> exclusiveParent) {

        List<Method> currentClassMethods = Arrays.asList(startClass.getDeclaredMethods());
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null &&
                (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Method> parentClassFields = (List<Method>) getFieldsUpTo(parentClass, exclusiveParent);
            currentClassMethods.addAll(parentClassFields);
        }

        return currentClassMethods;
    }
}
