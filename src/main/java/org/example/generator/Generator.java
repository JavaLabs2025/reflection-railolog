package org.example.generator;

import java.lang.reflect.*;
import java.util.*;

public class Generator {

    private static final int MAX_COLLECTION_SIZE = 10;
    private static final int MAX_DEPTH = 10;

    private final Random random = new Random();
    private final InterfaceRegistry interfaceRegistry = new InterfaceRegistry();

    public Object generateValueOfType(Class<?> clazz) throws Exception {
        return generateValueOfType(clazz, 0);
    }

    private Object generateValueOfType(Class<?> clazz, int currentDepth) throws Exception {
        return generateValueOfType(clazz, currentDepth, null);
    }

    private Object generateValueOfType(Class<?> clazz, int currentDepth, Field field) throws Exception {
        if (currentDepth > MAX_DEPTH) {
            return null;
        }

        if (clazz == int.class || clazz == Integer.class) {
            return random.nextInt(1000);
        }
        if (clazz == double.class || clazz == Double.class) {
            return random.nextDouble() * 100;
        }
        if (clazz == float.class || clazz == Float.class) {
            return random.nextFloat() * 100;
        }
        if (clazz == long.class || clazz == Long.class) {
            return random.nextLong();
        }
        if (clazz == boolean.class || clazz == Boolean.class) {
            return random.nextBoolean();
        }
        if (clazz == byte.class || clazz == Byte.class) {
            return (byte) random.nextInt(256);
        }
        if (clazz == short.class || clazz == Short.class) {
            return (short) random.nextInt(32768);
        }
        if (clazz == char.class || clazz == Character.class) {
            return (char) (random.nextInt(26) + 'a');
        }

        if (clazz == String.class) {
            return generateRandomString();
        }

        if (List.class.isAssignableFrom(clazz)) {
            return field == null ? new ArrayList<>() : generateList(field, currentDepth);
        }
        if (Set.class.isAssignableFrom(clazz)) {
            return field == null ? new HashSet<>() : generateSet(field, currentDepth);
        }
        if (Map.class.isAssignableFrom(clazz)) {
            return field == null ? new HashMap<>() : generateMap(field, currentDepth);
        }

        if (clazz.isInterface()) {
            return generateInterfaceImplementation(clazz, currentDepth);
        }

        return generateObject(clazz, currentDepth);
    }

    private Object generateObject(Class<?> clazz, int currentDepth) throws Exception {
        if (!clazz.isAnnotationPresent(Generatable.class)) {
            return null;
        }


        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            return null;
        }

        Constructor<?> selectedConstructor = selectRandomConstructor(constructors);
        selectedConstructor.setAccessible(true);

        Class<?>[] paramTypes = selectedConstructor.getParameterTypes();
        Object[] params = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = generateValueOfType(paramTypes[i], currentDepth + 1);
        }

        Object instance = selectedConstructor.newInstance(params);
        populateFields(instance, clazz, currentDepth);
        return instance;
    }

    public void populateFields(Object instance, Class<?> clazz, int currentDepth) {
        for (Field field : getAllFields(clazz)) {
            if (Modifier.isFinal(field.getModifiers())) {
                continue;
            }

            try {
                field.setAccessible(true);
                Object fieldValue = generateValueOfType(field.getType(), currentDepth + 1, field);
                field.set(instance, fieldValue);
            } catch (Exception ignored) {
            }
        }
    }

    private List<Object> generateList(Field field, int currentDepth) throws Exception {
        List<Object> list = new ArrayList<>();
        Class<?> elementClass = resolveSingleTypeArgument(field, 0);
        int size = random.nextInt(MAX_COLLECTION_SIZE);

        for (int i = 0; i < size; i++) {
            list.add(elementClass != null ? generateValueOfType(elementClass, currentDepth + 1) : null);
        }
        return list;
    }

    private Set<Object> generateSet(Field field, int currentDepth) throws Exception {
        Set<Object> set = new HashSet<>();
        Class<?> elementClass = resolveSingleTypeArgument(field, 0);
        int size = random.nextInt(MAX_COLLECTION_SIZE);

        for (int i = 0; i < size; i++) {
            set.add(elementClass != null ? generateValueOfType(elementClass, currentDepth + 1) : null);
        }
        return set;
    }

    private Map<Object, Object> generateMap(Field field, int currentDepth) throws Exception {
        Map<Object, Object> map = new HashMap<>();
        Class<?> keyClass = resolveSingleTypeArgument(field, 0);
        Class<?> valueClass = resolveSingleTypeArgument(field, 1);
        int size = random.nextInt(MAX_COLLECTION_SIZE);

        for (int i = 0; i < size; i++) {
            Object key = keyClass != null ? generateValueOfType(keyClass, currentDepth + 1) : null;
            Object value = valueClass != null ? generateValueOfType(valueClass, currentDepth + 1) : null;
            map.put(key, value);
        }
        return map;
    }

    public static Class<?> resolveSingleTypeArgument(Field field, int idx) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType p) {
            Type[] args = p.getActualTypeArguments();
            if (idx >= 0 && idx < args.length && args[idx] instanceof Class<?> clazz) {
                return clazz;
            }
        }
        return null;
    }

    public static List<Field> getAllFields(Class<?> type) {
        List<Field> result = new ArrayList<>();
        Class<?> cur = type;
        while (cur != null && cur != Object.class) {
            Collections.addAll(result, cur.getDeclaredFields());
            cur = cur.getSuperclass();
        }
        return result;
    }

    private Constructor<?> selectRandomConstructor(Constructor<?>[] constructors) {
        int idx = random.nextInt(constructors.length);
        return constructors[idx];
    }

    private String generateRandomString() {
        String[] words = {"apple", "banana", "cherry", "dog", "elephant", "forest", "guitar", "house", "ice", "jungle"};
        return words[random.nextInt(words.length)] + random.nextInt(100);
    }

    private Object generateInterfaceImplementation(Class<?> interfaceClass, int depth) throws Exception {
        List<Class<?>> implementations = interfaceRegistry.getImplementations(interfaceClass);

        if (implementations.isEmpty()) {
            throw new IllegalArgumentException("No @Generatable implementations found for interface: " + interfaceClass.getName());
        }

        Class<?> selectedImpl = implementations.get(random.nextInt(implementations.size()));
        return generateValueOfType(selectedImpl, depth);
    }
}
