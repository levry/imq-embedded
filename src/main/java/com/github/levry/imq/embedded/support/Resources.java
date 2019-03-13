package com.github.levry.imq.embedded.support;

import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.apache.commons.io.FileUtils.*;

/**
 * Internal utilities for resources
 *
 * @author levry
 */
@UtilityClass
public class Resources {

    public static void copyResourcesRecursively(URL source, File destination) throws IOException {

        URLConnection urlConnection = source.openConnection();
        if(urlConnection instanceof JarURLConnection) {
            copyJarResourcesRecursively((JarURLConnection) urlConnection, destination);
            return;
        }

        copyDirectory(new File(source.getPath()), destination);
    }

    private static void copyJarResourcesRecursively(JarURLConnection source, File destination) throws IOException {
        JarFile jarFile = source.getJarFile();
        for (JarEntry entry : Collections.list(jarFile.entries())) {
            String entryName = entry.getName();
            String sourceName = source.getEntryName();
            if (entryName.startsWith(sourceName)) {
                String fileName = entryName.substring(sourceName.length());
                File targetFile = new File(destination, fileName);
                if (!entry.isDirectory()) {
                    copyInputStreamToFile(jarFile.getInputStream(entry), targetFile);
                } else {
                    ensureDirectoryExists(targetFile);
                }
            }
        }
    }

    private static boolean ensureDirectoryExists(File f) {
        return f.exists() || f.mkdir();
    }

    public static String createTempDir(String prefix) {
        try {
            Path homeDir = Files.createTempDirectory(prefix);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> deleteQuietly(homeDir.toFile())));
            return homeDir.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create temp directory", e);
        }
    }

}
