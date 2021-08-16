package logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.StringJoiner;

public class CustomFileVisitor extends SimpleFileVisitor<Path> {
    private final Logger logger = LoggerFactory.getLogger(CustomFileVisitor.class);
    private final Cryptor cipher;
    private final CipherMode cipherMode;
    private final String password;

    public CustomFileVisitor(Cryptor cipher, CipherMode cipherMode, String password) {
        this.cipher = cipher;
        this.cipherMode = cipherMode;
        this.password = password;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (Files.isReadable(dir) && Files.isDirectory(dir)) {
            return FileVisitResult.CONTINUE;
        } else return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        executeInNewThread(file);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        StringJoiner joiner = new StringJoiner("\n");
        for (StackTraceElement stackTraceElement : exc.getStackTrace()) {
            joiner.add(stackTraceElement.toString());
        }
        logger.warn("File-Access failed: {}\n{}", exc.getMessage(), joiner);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        return FileVisitResult.CONTINUE;
    }

    private void executeInNewThread(Path file) {
        CipherThread t = new CipherThread(cipher, cipherMode, password, file);
        t.start();
    }
}
