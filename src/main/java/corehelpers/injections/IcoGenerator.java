package corehelpers.injections;

import java.util.Random;

/**
 * Random generator of valid Czech 'IČO' numbers.
 * Produces numbers in 8-digit format from range 00000000 - 99999999.
 */

public class IcoGenerator {

    /**
     * Generates random valid Czech 'IČO' number from range 00000000 - 99999999.
     * @return valid 'IČO' number
     */
    public static String generateIco(){
        Random random = new Random();
        final int RANDOM_NUMBER = random.ints(0,(9999999+1)).findFirst().getAsInt();
        String generatedIcoBase = String.valueOf(RANDOM_NUMBER);
        final int RANDOM_NUMBER_LENGTH = generatedIcoBase.length();

        if (RANDOM_NUMBER_LENGTH < 7) {
            final int DIGITS_TO_ADD = 7 - RANDOM_NUMBER_LENGTH;
            final String repeatedDigit = "0";
            final String addedDigits = new String(new char[DIGITS_TO_ADD]).replace("\0", repeatedDigit);
            generatedIcoBase = addedDigits + RANDOM_NUMBER;
        }

        int valueForModulo11Validation = 0;

        for (int i = 0; i < 7; i++) {
            valueForModulo11Validation += Integer.parseInt(String.valueOf(generatedIcoBase.charAt(i))) * (8 - i);
        }

        final int MODULO_11 = valueForModulo11Validation % 11;
        final String validationDigit;
        if (MODULO_11 == 0) {
            validationDigit = "1";
        } else if (MODULO_11 == 1) {
            validationDigit = "0";
        } else {
            validationDigit = String.valueOf(11 - MODULO_11);
        }

        return generatedIcoBase + validationDigit;

    }

}

