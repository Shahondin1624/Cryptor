package logic;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class Cryptor {
    private final int bufferSize = 1024 * 8;

    public void encryptFile(File file, String password) throws NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File encryptedFile = new File(file.getAbsolutePath() + "_");
        encryptedFile.createNewFile();
        IvParameterSpec iv = generateIv();
        Cipher cipher = init(Cipher.ENCRYPT_MODE, password, iv);
        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileOutputStream fileOutputStream = new FileOutputStream(encryptedFile);
             CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher)) {
            fileOutputStream.write(iv.getIV());
            byte[] buffer = new byte[bufferSize];
            int read;
            while ((read = fileInputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, read);
            }
        }
        file.delete();
        encryptedFile.renameTo(file);
    }

    public void decryptFile(File file, String password) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        File decryptedFile = new File(file.getAbsolutePath() + "_");
        decryptedFile.createNewFile();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            IvParameterSpec iv = new IvParameterSpec(readIV(fileInputStream));
            Cipher cipher = init(Cipher.DECRYPT_MODE, password, iv);
            try (CipherInputStream cipherInputStream = new CipherInputStream(fileInputStream, cipher);
                 FileOutputStream fileOutputStream = new FileOutputStream(decryptedFile)) {
                byte[] buffer = new byte[bufferSize];
                int read;
                while ((read = cipherInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, read);
                }
            }
        }
        file.delete();
        decryptedFile.renameTo(file);
    }

    private IvParameterSpec generateIv() throws NoSuchAlgorithmException {
        byte[] iv = new byte[16];
        SecureRandom.getInstanceStrong().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    private SecretKey deriveFromPassword(String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), "GfxIDN3%lTd".getBytes(StandardCharsets.UTF_8), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    private byte[] readIV(FileInputStream fileInputStream) throws IOException {
        return fileInputStream.readNBytes(16);
    }

    private Cipher init(int initMode, String password, IvParameterSpec iv)
            throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            InvalidKeyException {
        SecretKey key = deriveFromPassword(password);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(initMode, key, iv);
        return cipher;
    }

    private File en_de_cryptFileName(File file, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
        String name = file.getName();
        String encryptedName = name + "_"; //new String(cipher.doFinal(name.getBytes(StandardCharsets.UTF_8)));
        return new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - name.length()) + "/" + encryptedName);
    }
}
