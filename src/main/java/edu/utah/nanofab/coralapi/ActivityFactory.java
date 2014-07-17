package edu.utah.nanofab.coralapi;

import java.util.Date;

import org.opencoral.idl.Activity;

import edu.utah.nanofab.coralapi.helper.TimestampConverter;
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

	/**
	 * 
	 * @param a
	 * @return
	 */
	public static Reservation convertActivityToReservation(Activity a) {
		return null;
	}

}
