package com.ruochu.edata.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : RanPengCheng
 * @date : 2020/3/18 15:58
 */
public class ReflectUtil {

    private static final Map<String, Map<String, Field>> CLASS_FIELDS_CACHE = new HashMap<>();
    private static final Map<String, Map<String, Method>> GETTER_CACHE = new HashMap<>();
    private static final Map<String, Map<String, Method>> SETTER_CACHE = new HashMap<>();



    private ReflectUtil() {
    }

    /**
     * 是不是java基础类
     *
     * @param field
     * @return
     */
    public static boolean isJavaClass(Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType.isArray()) {
            return false;
        }

        return fieldType.isPrimitive() || fieldType.getPackage() == null
                || "java.lang".equals(fieldType.getPackage().getName())
                || "java.math".equals(fieldType.getPackage().getName())
                || "java.sql".equals(fieldType.getPackage().getName())
                || "java.util".equals(fieldType.getPackage().getName());
    }

    /**
     * 获取class的 包括父类的
     *
     * @param clazz
     * @return
     */
    public static Map<String, Field> getClassFields(Class<?> clazz) {

        String className = clazz.getName();
        Map<String, Field> fieldMap = CLASS_FIELDS_CACHE.get(className);
        if (EmptyChecker.isEmpty(fieldMap)) {
            fieldMap = new HashMap<>();
            CLASS_FIELDS_CACHE.put(className, fieldMap);
            do {
                for (Field field : clazz.getDeclaredFields()) {
                    fieldMap.put(field.getName(), field);
                }
                clazz = clazz.getSuperclass();
            } while (clazz != Object.class && clazz != null);
        }

        return fieldMap;
    }
    /**
     * 判断是不是集合的实现类
     *
     * @param clazz
     * @return
     */
    public static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }


    public static Method getGetter(Class<?> clazz, String fieldName) {
        String className = clazz.getName();
        Map<String, Method> methodMap = GETTER_CACHE.get(className);
        if (methodMap == null) {
            methodMap = new HashMap<>();
            GETTER_CACHE.put(className, methodMap);
        }
        Method getter = methodMap.get(fieldName);
        if (getter == null) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equalsIgnoreCase("get".concat(fieldName)) && method.getParameterCount() == 0) {
                    getter = method;
                }
            }
        }

        return getter;
    }


    public static Method getSetter(Class<?> clazz, String fieldName) {
        String className = clazz.getName();
        Map<String, Method> methodMap = SETTER_CACHE.get(className);
        if (methodMap == null) {
            methodMap = new HashMap<>();
            SETTER_CACHE.put(className, methodMap);
        }

        Method setter = methodMap.get(fieldName);
        if (setter == null) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equalsIgnoreCase("set".concat(fieldName)) && method.getParameterCount() == 1) {
                    setter = method;
                }
            }
        }
        return setter;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        return getClassFields(clazz).get(fieldName);
    }
}
