package main;

import logic.Cryptor;
import logic.PathHandler;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("You have to provide a password as argument!");
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
                        System.out.println("Not a valid path, exiting en/decryption mode");
                        continue;
                    }
                    List<File> encryptionFiles = PathHandler.getAllFiles(new File(normalizeFilePaths(answer)));
                    encryptionFiles.forEach(file -> {
                        Thread t = new Thread(() -> {
                            Cryptor cryptor = new Cryptor();
                            try {
                                cryptor.encryptFile(file, password);
                            } catch (NoSuchAlgorithmException | IOException | InvalidAlgorithmParameterException | NoSuchPaddingException |
                                    InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                                e.printStackTrace();
                            }
                        });
                        t.setDaemon(false);
                        t.start();
                    });
                    System.out.println("Files are being encrypted");
                }
                case "de" -> {
                    System.out.println("Please provide a filepath/directory filepath");
                    answer = scanner.nextLine();
                    if (!isPath(answer)) {
                        System.out.println("Not a valid path, exiting en/decryption mode");
                        continue;
                    }
                    List<File> decryptionFiles = PathHandler.getAllFiles(new File(normalizeFilePaths(answer)));
                    decryptionFiles.forEach(file -> {
                        Thread t = new Thread(() -> {
                            Cryptor cryptor = new Cryptor();
                            try {
                                cryptor.decryptFile(file, password);
                            } catch (NoSuchAlgorithmException | IOException | InvalidAlgorithmParameterException | NoSuchPaddingException |
                                    InvalidKeySpecException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                                e.printStackTrace();
                            }
                        });
                        t.setDaemon(false);
                        t.start();
                    });
                    System.out.println("Files are being decrypted");
                }
                default -> {
                    abort = true;
                    scanner.close();
                }
            }
        }
    }

    //unused
    private static String normalizeFilePaths(String path) {
        return path;
    }

    private static boolean isPath(String path) {
        try {
            File file = new File(path);
            return file.exists() && file.canRead() && file.canWrite();
        } catch (RuntimeException e) {
            System.out.println("Not a valid path, exiting en/decryption mode");
            return false;
        }
    }
}
