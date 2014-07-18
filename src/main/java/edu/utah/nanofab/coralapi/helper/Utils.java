package edu.utah.nanofab.coralapi.helper;

import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.opencoral.idl.Activity;

import edu.utah.nanofab.coralapi.ActivityFactory;

/**
 * The Utils class contains many static helper functions that can be used throughout the
 * Coral API project.
 */
public final class Utils {
	
	/**
	 * Creates an array of Activity instances representing a lower bound and upper bound for a
	 * reservation search filter.
	 * 
	 * @param member The member to limit the search too.
	 * @param tool The tool the reservation is made for.
	 * @param bdate The beginning date of the reservation.
	 * @param edate The ending date of the reservation.
	 * @return An array of Activity instances where the first element is the lower bound and the second
	 * element is an upper bound.
	 */
	public static Activity[] createReservationSearchFilter(String member,
			String tool, Date bdate, Date edate) {
		Activity b = ActivityFactory.createLowerReservationActivityFilter(member, tool, bdate);
		Activity e = ActivityFactory.createUpperReservationActivityFilter(member, tool, edate);
	    return new Activity[]{b, e};
	}
	
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
