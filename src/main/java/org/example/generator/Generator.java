package org.example.generator;

import java.lang.reflect.*;
import java.util.*;

public class Generator {
    private final Random random = new Random();
    private final int maxDepth = 3;
    private final int maxCollectionSize = 5;
    private final InterfaceRegistry interfaceRegistry = new InterfaceRegistry();

    public Object generateValueOfType(Class<?> clazz) throws Exception {
        return generateValueOfType(clazz, 0);
    }

    private Object generateValueOfType(Class<?> clazz, int currentDepth) throws Exception {
        if (currentDepth > maxDepth) {
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
            return generateList(currentDepth);
        }
        if (Set.class.isAssignableFrom(clazz)) {
            return generateSet(currentDepth);
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

        return selectedConstructor.newInstance(params);
    }

    private Constructor<?> selectRandomConstructor(Constructor<?>[] constructors) {
        int idx = random.nextInt(constructors.length);
        return constructors[idx];
    }

    private String generateRandomString() {
        String[] words = {"apple", "banana", "cherry", "dog", "elephant", "forest", "guitar", "house", "ice", "jungle"};
        return words[random.nextInt(words.length)] + random.nextInt(100);
    }

    private List<Object> generateList(int depth) throws Exception {
        List<Object> list = new ArrayList<>();
        int size = random.nextInt(maxCollectionSize);

        Class<?>[] possibleTypes = {String.class, Integer.class, Double.class};
        for (int i = 0; i < size; i++) {
            Class<?> randomType = possibleTypes[random.nextInt(possibleTypes.length)];
            list.add(generateValueOfType(randomType, depth + 1));
        }
        return list;
    }

    private Set<Object> generateSet(int depth) throws Exception {
        Set<Object> set = new HashSet<>();
        int size = random.nextInt(maxCollectionSize);

        Class<?>[] possibleTypes = {String.class, Integer.class, Double.class};
        for (int i = 0; i < size; i++) {
            Class<?> randomType = possibleTypes[random.nextInt(possibleTypes.length)];
            set.add(generateValueOfType(randomType, depth + 1));
        }
        return set;
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
