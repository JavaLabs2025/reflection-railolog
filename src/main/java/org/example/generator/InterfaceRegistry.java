package org.example.generator;

import java.io.File;
import java.net.URL;
import java.util.*;

public class InterfaceRegistry {
    private static final String PACKAGE_NAME = "org.example.classes";

    private final Map<Class<?>, List<Class<?>>> interfaceToImplementations = new HashMap<>();
    private boolean initialized = false;

    public List<Class<?>> getImplementations(Class<?> interfaceClass) {
        if (!initialized) {
            scanForGeneratableClasses();
        }
        return interfaceToImplementations.getOrDefault(interfaceClass, Collections.emptyList());
    }

    private void registerClass(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Generatable.class)) {
            return;
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClass : interfaces) {
            interfaceToImplementations
                    .computeIfAbsent(interfaceClass, k -> new ArrayList<>())
                    .add(clazz);
        }
    }

    private void scanForGeneratableClasses() {
        try {
            String packagePath = PACKAGE_NAME.replace('.', '/');

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(packagePath);

            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    scanDirectory(new File(resource.getFile()), PACKAGE_NAME);
                }
            }

            initialized = true;
        } catch (Exception e) {
            System.err.println("Error scanning for @Generatable classes: " + e.getMessage());
        }
    }

    private void scanDirectory(File directory, String packageName) {
        if (!directory.exists()) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    registerClass(clazz);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
    }
}