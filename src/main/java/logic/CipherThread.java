package logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CipherThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(CipherThread.class);
    private final Cryptor cipher;
    private final CipherMode cipherMode;
    private final String password;
    private final Path file;
    private long start;

    public CipherThread(Cryptor cipher, CipherMode cipherMode, String password, Path file) {
        this.cipher = cipher;
        this.cipherMode = cipherMode;
        this.password = password;
        this.file = file;
    }

    @Override
    public void run() {
        try {
            if (cipherMode == CipherMode.ENCRYPT) {
                logger.info("Starting to encrypt {}", file.toString());
                startMeasuringTime();
                cipher.encryptFile(file.toFile(), password);
                logger.info("Finished encrypting {} took {}", file, logDuration(getExecutionTime()));
            } else {
                logger.info("Starting to decrypt {}", file.toString());
                startMeasuringTime();
                cipher.decryptFile(file.toFile(), password);
                logger.info("Finished decrypting {} took {}", file, logDuration(getExecutionTime()));
            }
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | InvalidKeyException | IOException e) {
            if (cipherMode == CipherMode.ENCRYPT) {
                logger.error("Encryption of {} failed: {}", file, e.getMessage());
            } else {
                logger.error("Decryption of {} failed: {}", file, e.getMessage());
            }
        }
    }

    private void startMeasuringTime() {
        start = System.currentTimeMillis();
    }

    private long getExecutionTime() {
        return System.currentTimeMillis() - start;
    }

    private String logDuration(long duration) {
        int seconds = (int) (duration / 1000) % 60;
        int minutes = (int) ((duration / (1000 * 60)) % 60);
        int hours = (int) ((duration / (1000 * 60 * 60)) % 24);
        int ms = (int) (duration - seconds * 1000 - minutes * 1000 * 60 - hours * 1000 * 3600);
        return String.format("%02d:%02d:%02d:%03d", hours, minutes, seconds, ms);
    }
}
