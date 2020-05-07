package vn.locdt.excel.utils;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class ReflectionUtils {
    public static Map<String, Field> getMapOfFieldNameAndField(final Class<?> clazz) {
        final Map<String, Field> mapOfFieldNameAndField = new HashMap<>();

        for (final Field field : getAllFieldsList(clazz)) {
            if (!Modifier.isStatic(field.getModifiers())) {
                mapOfFieldNameAndField.put(field.getName(), field);
            }
        }

        return mapOfFieldNameAndField;
    }

    /**
     * Gets all fields of the given class and its parents (if any).
     *
     * @param cls the {@link Class} to query
     * @return an array of Fields (possibly empty).
     * @throws IllegalArgumentException if the class is {@code null}
     * @since 3.2
     */
    private static List<Field> getAllFieldsList(final Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("The class must not be null");
        }

        final List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = cls;
        while (currentClass != null) {
            final Field[] declaredFields = currentClass.getDeclaredFields();
            Collections.addAll(allFields, declaredFields);
            currentClass = currentClass.getSuperclass();
        }
        return allFields;
    }

    public static <T> T newInstanceFromClass(final Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void invokeSetMethod(final Object obj, final String field, final Object value) {
        final Class<?> cls = obj.getClass();
        final Method[] mths = cls.getMethods();
        final String setterMth = ReflectionUtils.getSetterName(field);
        for (final Method method : mths) {
            if (method.getName().equals(setterMth) && method.getParameterTypes().length == 1) {
                try {
                    method.invoke(obj, value);
                    break;
                } catch (final Throwable e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
    }

    public static String getSetterName(final String field) {
        return "set" + StringUtils.capitalize(field);
    }
}
