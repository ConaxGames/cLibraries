package com.conaxgames.libraries.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ClassUtils {

    private static final Logger LOGGER = Logger.getLogger(ClassUtils.class.getName());

    private ClassUtils() {
    }

    public static Set<Class<?>> getClassesInPackage(JavaPlugin plugin, String packageName, boolean isTopLevel) throws IOException {
        return ClassPath.from(plugin.getClass().getProtectionDomain().getClassLoader())
                .getAllClasses()
                .stream()
                .filter(clazz -> clazz.getPackageName().equalsIgnoreCase(packageName))
                .filter(clazz -> !isTopLevel || clazz.isTopLevel())
                .map(ClassPath.ClassInfo::load)
                .collect(Collectors.toSet());
    }

    /**
     * Gets all the classes in a the provided package.
     *
     * @param plugin      The plugin who owns the package
     * @param packageName The package to scan classes in.
     * @return The classes in the package packageName.
     */
    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        Collection<Class<?>> classes = new ArrayList<>();
        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > relPath.length() + 1) {
                    String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
                    try {
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException e) {
                        LOGGER.log(Level.WARNING, "Failed to load class: " + className, e);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }

        return ImmutableSet.copyOf(classes);
    }

}