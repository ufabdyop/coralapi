package edu.utah.nanofab.helper;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * The Utils class contains many static helper functions that can be used throughout the
 * Coral API project.
 */
public final class Utils {
	
	/**
	 * Checks if a given email is a valid email address.
	 * 
	 * @param email The email address.
	 *
	 * @return True is the email is valid. False otherwise.
	 */
	public static boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}
}
