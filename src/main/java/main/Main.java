package main;

import logic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            logger.error("You have to provide a password as argument!");
            return;
        }
        String password = args[0];
        boolean abort = false;
        Scanner scanner = new Scanner(System.in);
        while (!abort) {
            System.out.println("Do you want to en-/decrypt or exit? (en/de/ex)");
            String answer = scanner.nextLine();
            switch (answer.toLowerCase(Locale.ROOT)) {
                case "en" -> {
                    System.out.println("Please provide a filepath/directory filepath");
                    answer = scanner.nextLine();
                    if (!isPath(answer)) {
                        logger.error("Not a valid path, exiting encryption mode");
                        continue;
                    }
                    logger.info("Files are being encrypted");
                    Files.walkFileTree(Path.of(normalizeFilePaths(answer)), new CustomFileVisitor(new Cryptor(), CipherMode.ENCRYPT, password));
                    abort = true;
                }
                case "de" -> {
                    System.out.println("Please provide a filepath/directory filepath");
                    answer = scanner.nextLine();
                    if (!isPath(answer)) {
                        logger.error("Not a valid path, exiting decryption mode");
                        continue;
                    }
                    logger.info("Files are being decrypted");
                    Files.walkFileTree(Path.of(normalizeFilePaths(answer)), new CustomFileVisitor(new Cryptor(), CipherMode.DECRYPT, password));
                    abort = true;
                }
                default -> abort = true;
            }
        }
        scanner.close();
//        waitUntilAllThreadsAreDone(threads);
    }

    //unused
    private static String normalizeFilePaths(String path) {
        switch (File.separator) {
            case "\\" -> {
                return path.replaceAll("/", "\\");
            }
            case "/" -> {
                return path.replaceAll("\\\\", "/");
            }
            default -> {
                return path;
            }
        }
    }

    private static boolean isPath(String path) {
        try {
            File file = new File(path);
            return file.exists() && file.canRead() && file.canWrite();
        } catch (RuntimeException e) {
            logger.error("When testing path {} was thrown: {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }
}
