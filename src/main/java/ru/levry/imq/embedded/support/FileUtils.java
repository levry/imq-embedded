package ru.levry.imq.embedded.support;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author levry
 */
@UtilityClass
public class FileUtils {

    public static boolean deleteRecursively(Path root) throws IOException {
        if (root == null) {
            return false;
        }
        if (!root.toFile().exists()) {
            return false;
        }

        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
        return true;
    }

    public static void copyRecursively(Path src, Path dest) throws IOException {
        BasicFileAttributes srcAttr = Files.readAttributes(src, BasicFileAttributes.class);

        if (srcAttr.isDirectory()) {
            Files.walkFileTree(src, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Files.createDirectories(dest.resolve(src.relativize(dir)));
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, dest.resolve(src.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else if (srcAttr.isRegularFile()) {
            Files.copy(src, dest);
        }
        else {
            throw new IllegalArgumentException("Source File must denote a directory or file");
        }
    }

}
