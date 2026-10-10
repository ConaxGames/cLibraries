package com.conaxgames.libraries.util;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassUtils {

    private ClassUtils() {
    }

    public static Collection<Class<?>> getClassesInPackage(Plugin plugin, String packageName) {
        CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
        URL resource = codeSource.getLocation();
        String relPath = packageName.replace('.', '/');
        String jarPath = resource.getPath().replace("%20", " ").replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        ClassLoader loader = plugin.getClass().getClassLoader();
        Collection<Class<?>> classes = new ArrayList<>();

        try (JarFile jarFile = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement().getName();
                if (!entryName.endsWith(".class") || !entryName.startsWith(relPath) || entryName.length() <= relPath.length() + 1) {
                    continue;
                }
                // Trailing suffix only — replace(".class","") also mangles ".classes" packages.
                String className = entryName.substring(0, entryName.length() - 6).replace('/', '.').replace('\\', '.');
                try {
                    // Load without initializing so missing soft-dep linkage fails at construction, not scan.
                    classes.add(Class.forName(className, false, loader));
                } catch (ClassNotFoundException ignored) {
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }

        return Collections.unmodifiableCollection(classes);
    }

}
