package edu.utah.nanofab.coralapi;

import java.util.Date;

import org.opencoral.constants.Constants;
import org.opencoral.idl.Activity;

import edu.utah.nanofab.coralapi.helper.TimestampConverter;
import edu.utah.nanofab.coralapi.resource.Account;
import edu.utah.nanofab.coralapi.resource.Member;
import edu.utah.nanofab.coralapi.resource.Project;
import edu.utah.nanofab.coralapi.resource.Reservation;

public class ActivityFactory {
	
	public static Activity createRunActivity(String agent, String member,
			String tool, String project, String account, String lab,
			Date bdate, Date edate) {

		Activity activity = new Activity();
		activity.agent = agent;
		activity.member = member;
		activity.item = tool;
		activity.project = project;
		activity.account = account;
		activity.lab = lab;
		activity.bdate = TimestampConverter.dateToTimestamp(bdate);
		activity.edate = TimestampConverter.dateToTimestamp(edate);
		activity.amount = 0.0;
		activity.creationDate = TimestampConverter.dateToTimestamp(new Date());
		activity.creator = agent;
		activity.id = "not assigned";
		activity.isNull = false;
		activity.primeAmount = 0.0;
		activity.process = "default";
		activity.reference = "not assigned";
		activity.stale = false;
		activity.type = "Generic Activity";

		try {
			Class<?> activityClass = activity.getClass();
			activityClass.getField("area").set(activity, "");
		} catch (Exception e) {

		}

		return activity;
	}
	
	public static Activity createLowerReservationActivityFilter(String member,
			String tool, Date date) {

		Activity activity = new Activity();
		activity.account = "*";
		activity.agent = "*";
		activity.area = "*";
		activity.amount = 0.0;
		activity.bdate = TimestampConverter.dateToTimestamp(null);
		activity.creationDate = TimestampConverter.dateToTimestamp(null);
		activity.creator = "*";
		activity.edate = TimestampConverter.dateToTimestamp(date);
		activity.id = "*";
		activity.isNull = false;
		activity.item = tool;
		activity.lab = "*";
		activity.member = member;
		activity.project = "*";
		activity.primeAmount = 0.0;
		activity.process = "*";
		activity.reference = "*";
		activity.stale = false;
		activity.type = "reservation";

		try {
			Class<?> activityClass = activity.getClass();
			activityClass.getField("area").set(activity, "");
		} catch (Exception e) {

		}

		return activity;
	}
	
	public static Activity createUpperReservationActivityFilter(String member,
			String tool, Date date) {
		Activity activity = new Activity();
		activity.account = "*";
		activity.agent = "*";
		activity.area = "*";
		activity.amount = Constants.MAX_ACTIVITY_AMOUNT;
		activity.bdate = TimestampConverter.dateToTimestamp(date);
		activity.creationDate = TimestampConverter.dateToTimestamp(null);
		activity.creator = "*";
		activity.edate = TimestampConverter.dateToTimestamp(null);
		activity.id = "*";
		activity.isNull = false;
		activity.item = tool;
		activity.lab = "*";
		activity.member = member;
		activity.project = "*";
		activity.primeAmount = Constants.MAX_ACTIVITY_AMOUNT;
		activity.process = "*";
		activity.reference = "*";
		activity.stale = false;
		activity.type = "reservation";

		try {
			Class<?> activityClass = activity.getClass();
			activityClass.getField("area").set(activity, "");
		} catch (Exception e) {

		}

		return activity;
	}
	
	

	/**
	 * 
	 * @param a
	 * @return
	 */
	public static Reservation convertActivityToReservation(Activity a) {
		Member m = new Member();
		m.setName(a.member);
		
		Project p = new Project();
		p.setName(a.project);
		
		Account acct = new Account();
		acct.setName(a.account);
		
		String item = a.item;
		String lab = a.lab;
		Date bdate = TimestampConverter.timestampToDate(a.bdate);
		Date edate = TimestampConverter.timestampToDate(a.edate);
		
		Reservation r = new Reservation(m, p, acct, bdate, edate, item, lab);
		return r;
	}

}
