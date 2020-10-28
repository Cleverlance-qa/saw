package corehelpers.crypting;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.properties.PropertyValueEncryptionUtils;

import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for Jasypt decryption
 */
public class DecryptorImpl implements Decryptor {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String DEFAULT_ALGORITHM = "PBEWithMD5AndTripleDES";
    private static final String ENCRYPT_ALGORITHM_PROPERTY = "encryptAlgorithm";
    private static final String ENCRYPT_PASSWORD_PROPERTY = "encryptPassword";
    private StandardPBEStringEncryptor decryptor;

    /**
     * Pre-load decryptor with password and algorithm
     */
    public DecryptorImpl() {
        decryptor = new StandardPBEStringEncryptor();
        if(System.getProperty(ENCRYPT_ALGORITHM_PROPERTY) == null) {
            decryptor.setAlgorithm(DEFAULT_ALGORITHM);
        } else {
            decryptor.setAlgorithm(System.getProperty(ENCRYPT_ALGORITHM_PROPERTY));
        }
        if(System.getProperty(ENCRYPT_PASSWORD_PROPERTY) != null) {
            decryptor.setPassword(System.getProperty(ENCRYPT_PASSWORD_PROPERTY));
        }
    }

    public String decryptString(String text) {
        if(!PropertyValueEncryptionUtils.isEncryptedValue(text)) {
            return text;
        }
        try {
            return PropertyValueEncryptionUtils.decrypt(text, decryptor);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, "encryptPassword parameter for text decryption can't be empty!", e);
        } catch (EncryptionOperationNotPossibleException e) {
            LOGGER.log(Level.SEVERE, text + " - Can't decrypt given text! Wrong hash or password.", e);
        }
        return text;
    }

    public Properties decryptProperties(Properties prop) {
        final Set<Object> keys = prop.keySet();
        for (Object key : keys) {
            prop.replace(key, prop.getProperty(key.toString()), decryptString(prop.getProperty(key.toString())));
        }
        return prop;
    }
}
