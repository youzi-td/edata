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

    private static final String GET = "get";
    private static final String SET = "set";


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
            methodMap = getMethodMap(clazz, GET);
            GETTER_CACHE.put(className, methodMap);
        }

        return methodMap.get(fieldName);
    }

    public static Method getSetter(Class<?> clazz, String fieldName) {
        String className = clazz.getName();
        Map<String, Method> methodMap = SETTER_CACHE.get(className);
        if (methodMap == null) {
            methodMap = getMethodMap(clazz, SET);
            SETTER_CACHE.put(className, methodMap);
        }

        return methodMap.get(fieldName);
    }

    private static Map<String, Method> getMethodMap(Class<?> clazz, String type) {
        Map<String, Field> fieldMap = getClassFields(clazz);
        Map<String, Method> methodMap = new HashMap<>(fieldMap.size());

        for (Method method : clazz.getMethods()) {
            String methodName = method.getName();
            if (methodName.startsWith(type) && methodName.length() > 3) {
                if (GET.equals(type) && method.getParameterCount() != 0) {
                    continue;
                }
                if (SET.equals(type) && method.getParameterCount() != 1) {
                    continue;
                }
                String fieldName = methodName.substring(3);
                fieldName = fieldMap.containsKey(fieldName) ? fieldName : fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
                if (fieldMap.containsKey(fieldName) ) {
                    methodMap.put(fieldName, method);
                }
            }
        }
        return methodMap;
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        return getClassFields(clazz).get(fieldName);
    }

    public static Object getValue(Object obj, String field) {
        Method getter = getGetter(obj.getClass(), field);
        if (getter != null) {
            try {
                return getter.invoke(obj);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
