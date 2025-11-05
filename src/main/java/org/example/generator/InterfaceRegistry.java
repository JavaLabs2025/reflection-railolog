package org.example.generator;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
                } else if (resource.getProtocol().equals("jar")) {
                    scanJarFile(resource.getFile(), packagePath);
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

    private void scanJarFile(String jarPath, String packagePath) {
        try {
            String jarFilePath = jarPath.substring(jarPath.indexOf(":") + 1, jarPath.indexOf("!"));
            try (JarFile jarFile = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                        String className = entryName.replace('/', '.').substring(0, entryName.length() - 6);
                        try {
                            Class<?> clazz = Class.forName(className);
                            registerClass(clazz);
                        } catch (ClassNotFoundException ignored) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error scanning JAR file: " + e.getMessage());
        }
    }
}