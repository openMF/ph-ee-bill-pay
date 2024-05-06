package org.mifos.pheebillpay.service;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("HideUtilityClassConstructor")
public class PrepareHeaderImpl {

    public static Set<String> process(Object instance) throws IllegalAccessException {
        Set<String> resultSet = new HashSet<>();
        Class<?> clazz = instance.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PrepareHeader.class)) {
                PrepareHeader annotation = field.getAnnotation(PrepareHeader.class);
                for (String value : annotation.values()) {
                    resultSet.add(value.toLowerCase());
                }
            }
        }
        return resultSet;
    }
}
