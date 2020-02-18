/*
 * $Id: RcGenerator.java 5142 2010-05-12 22:13:47Z pavel.muller $
 * 
 * Copyright (c) 2010 AspectWorks, spol. s r.o.
 */
package corehelpers.injections;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Random generator of valid Czech 'Rodne cislo' numbers.
 * Produces numbers in formats 123456/1234 and 123456/123 for numbers older than year 1954.
 *
 * @see RcType
 *
 * @author Pavel Muller
 * @version $Revision: 5142 $
 */
public class RcGenerator {

	private static Random random = new Random(System.currentTimeMillis());

	/**
	 * Type of RC number to generate.
	 */
	public enum RcType {
		/**
		 * RC according to official specification. Default option.
		 * 9-digit number modulo 11 and digit 10 is CRC.
		 * If CRC is 10 then CRC is 0. There is about 1000 of RCs of this type issued these days.
		 * <p>This type also supports new possibility to add 70 to month for women and 20 for men
		 * if there is no free number for a given date.
		 */
		OFFICIAL,
		/**
		 * RC that passes most of the validators.
		 * Whole 10-digit number modulo 11 is 0.
		 * It's not exactly according to the spec.
		 */
		COMMON,
		/**
		 * RC with the corner case.
		 * 9-digit number modulo 11 is 10 so the CRC number is 0.
		 * Use this type to check your validation algorithm.
		 * This option will work only for RCs issued after 1.1.1954.
		 */
		NO_MOD_11
	}

	/**
	 * Person gender (male or female).
	 */
	public enum Gender {
		MALE,
		FEMALE
	}

	/**
	 * Generates official RC for random date starting from 1900 and random gender.
	 * @return valid RC
	 */
	public static String generateRc() {
		return generateRc(RcType.OFFICIAL);
	}

	/**
	 * Generates RC of a given type for random date starting from 1900 and random gender.
	 * @param type RC type to generate
	 * @return valid RC
	 */
	public static String generateRc(RcType type) {
		final Calendar birthDate = generateDate();
		final Gender gender = generateGender();
		return generateRc(birthDate, gender, type);
	}

	/**
	 * Generates official RC for a given gender and random date starting from 1900.
	 * @param gender person gender to use for RC
	 * @return valid RC
	 */
	public static String generateRc(Gender gender) {
		return generateRc(gender, RcType.OFFICIAL);
	}

	/**
	 * Generates RC of a given type, for a given gender and random date starting from 1900.
	 * @param gender person gender to use for RC
	 * @param type RC type to generate
	 * @return valid RC
	 */
	public static String generateRc(Gender gender, RcType type) {
		final Calendar birthDate = generateDate();
		return generateRc(birthDate, gender, type);
	}

	/**
	 * Generates official RC for a person in an age range.
	 * Gender is random.
	 * @param minAge person's minimum current age (inclusive)
	 * @param maxAge person's maximum current age (inclusive)
	 * @return valid RC
	 */
	public static String generateRcForAge(int minAge, int maxAge) {
		return generateRcForAge(minAge, maxAge, RcType.OFFICIAL);
	}

	/**
	 * Generates official RC for a person in an age range and with a given gender.
	 * @param minAge person's minimum current age (inclusive)
	 * @param maxAge person's maximum current age (inclusive)
	 * @param gender person gender to use for RC
	 * @return valid RC
	 */
	public static String generateRcForAge(int minAge, int maxAge, Gender gender) {
		return generateRcForAge(minAge, maxAge, gender, RcType.OFFICIAL);
	}

	/**
	 * Generates RC of a given type for a person in an age range.
	 * Gender is random.
	 * @param minAge person's minimum current age (inclusive)
	 * @param maxAge person's maximum current age (inclusive)
	 * @param type RC type to generate
	 * @return valid RC
	 */
	public static String generateRcForAge(int minAge, int maxAge, RcType type) {
		final Gender gender = generateGender();
		return generateRcForAge(minAge, maxAge, gender, type);
	}

	/**
	 * Generates RC of a given type for a person in an age range and with a given gender.
	 * @param minAge person's minimum current age (inclusive)
	 * @param maxAge person's maximum current age (inclusive)
	 * @param gender person gender to use for RC
	 * @param type RC type to generate
	 * @return valid RC
	 */
	public static String generateRcForAge(int minAge, int maxAge, Gender gender, RcType type) {
		final Calendar birthDate = generateDateForAge(minAge, maxAge);
		return generateRc(birthDate, gender, type);
	}

	/**
	 * Generates valid official RC for a given birth date and gender.
	 * @param birthDate date of birth
	 * @param gender person's gender to use for RC
	 * @return valid RC
	 */
	public static String generateRc(Calendar birthDate, Gender gender) {
		return generateRc(birthDate, gender, RcType.OFFICIAL);
	}

	/**
	 * Generates valid RC of a given type for a given birth date and gender.
	 * @param birthDate date of birth
	 * @param gender person's gender to use for RC
	 * @param type RC type to generate
	 * @return valid RC
	 */
	public static String generateRc(Calendar birthDate, Gender gender, RcType type) {
		final String yearPart = new SimpleDateFormat("yy", Locale.UK).format(birthDate.getTime());
		int month = birthDate.get(Calendar.MONTH) + 1;
		month += getGenderAddition(birthDate, gender, type);

		final String monthPart = String.format("%02d", month);
		final String dayPart = new SimpleDateFormat("dd", Locale.UK).format(birthDate.getTime());
		final String suffix = generateSuffix();

		final StringBuilder buf = new StringBuilder();
		buf.append(yearPart).append(monthPart).append(dayPart).append(suffix);

		final int checkNumber = Integer.parseInt(buf.toString());
		int crcNumber = checkNumber % 11;
		int suffixNumber = Integer.parseInt(suffix);
		if (crcNumber == 10) {
			crcNumber = 0;
			// bad case for common validation algorithms - shift number by one
			if (type == RcType.COMMON) {
				if (suffixNumber == 0) {
					suffixNumber++;
				} else {
					suffixNumber--;
					crcNumber = 9;
				}
			}
		} else if (type == RcType.NO_MOD_11 && birthDate.get(Calendar.YEAR) >= 1954) {
			// we need modulo 11 to be 10
			if (suffixNumber < 990) {
				suffixNumber += (10 - crcNumber);
			} else {
				suffixNumber -= (crcNumber + 1);
			}
			crcNumber = 0;
		}

		final StringBuilder rc = new StringBuilder();
		rc.append(yearPart).append(monthPart).append(dayPart);
		rc.append('/');
		rc.append(String.format("%03d", suffixNumber));
		if (birthDate.get(Calendar.YEAR) >= 1954) {
			rc.append(crcNumber);
		}

		return rc.toString();
	}

	private static int getGenderAddition(Calendar birthDate, Gender gender, RcType type) {
		// from year 2004 there is a possibility to add 70 for women and 20 for men
		// if there is no free RC for the birth date
		// use this possibility for one case of 10
		if (type == RcType.OFFICIAL && birthDate.get(Calendar.YEAR) >= 2004 && random.nextInt(10) == 0) {
			if (gender == Gender.FEMALE) {
				return 70;
			}
			return 20;
		} else if (gender == Gender.FEMALE) {
			return 50;
		}

		return 0;
	}

	private static Calendar generateDate() {
		final Calendar minDate = Calendar.getInstance();
		minDate.set(Calendar.YEAR, 1900);
		minDate.set(Calendar.DAY_OF_YEAR, 1);
		minDate.set(Calendar.HOUR_OF_DAY, 0);
		minDate.set(Calendar.MINUTE, 0);
		final Calendar maxDate = Calendar.getInstance();
		return generateDate(minDate, maxDate);
	}

	private static Calendar generateDateForAge(int minAge, int maxAge) {
		final Calendar maxDate = Calendar.getInstance();
		maxDate.add(Calendar.YEAR, -maxAge-1);
		final Calendar minDate = Calendar.getInstance();
		minDate.add(Calendar.YEAR, -minAge);
		return generateDate(minDate, maxDate);
	}

	private static Calendar generateDate(Calendar minDate, Calendar maxDate) {
		final long val1 = minDate.getTimeInMillis();
		final long val2 = maxDate.getTimeInMillis();
		final long randomDate = (long) (random.nextDouble() * (val2 - val1)) + val1;
		final Calendar result = Calendar.getInstance();
		result.setTimeInMillis(randomDate);
		return result;
	}

	private static String generateSuffix() {
		return String.format("%03d", random.nextInt(1000));
	}

	private static Gender generateGender() {
		final boolean isMale = random.nextBoolean();
		if (isMale) {
			return Gender.MALE;
		}
		return Gender.FEMALE;
	}

	/**
	 * Get date from RC
	 * @param rc - RC to parse
	 * @return - parsed date from RC
	 */
	public String getDateFromRC(String rc) {
		final int R_LIMIT = 54;
		final int day = Integer.parseInt(rc.substring(4,6));;
		int month = Integer.parseInt(rc.substring(2,4));
		int year = Integer.parseInt(rc.substring(0, 2));
		if (rc.length() == 9 || year >= R_LIMIT) {
			year += 1900;  //stary format cisla platny do 1.1.1954 mel jen 9 cislic
		} else {
			year += 2000;
		}

        /*Zeny maji u mesice +50
          Od roku 2004 (zákonem č. 53/2004 Sb.) je zavedena možnost v případě, že
          jsou v nějaký day vyčerpána všechna platná čtyřčíslí, použít alternativní
          rodné číslo, u kterého mají muži k číslu měsíce přičteno 20 a ženy 70.
        */
		if (month > 50) {
			month -= 50;
		}
		if (month > 20) {
			month -= 20;
		}
		return checkNumber(day) + checkNumber(month) + Integer.toString(year);
	}

	private String checkNumber(int numberToCheck) {
		if (numberToCheck < 10) {
			return "0" + numberToCheck;
		}
		return Integer.toString(numberToCheck);
	}
}
