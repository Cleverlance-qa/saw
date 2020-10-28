package corehelpers.crypting;

import java.util.Properties;

/**
 * Interface for password and data decryption.
 * Data can be decrypted in two ways:
 * If they are saved in properties or datasets and in ENV(...) format. In this case data are decrypted automatically
 * If they are decrypted via decryptText method
 */
public interface Decryptor {

    /**
     * Decrytpt encrypted text with Jasypt
     * @param text - given encrypted text
     * @return - Decrypted text
     * In case of non-crypted text, return text from input
     * In case of fail, return back crypted text from input
     */
    public String decryptString(String text);

    /**
     * Decrypt encryptet properties with Jasypt
     * @param prop - given properties
     * @return - properties with encrypted entries
     */
    public Properties decryptProperties(Properties prop);

}
