package edu.nanofab.utah.coralapi;

import java.util.Calendar;

import org.opencoral.idl.Activity;
import org.opencoral.idl.Timestamp;

public class ActivityFactory {

	public Activity createDefaultActivity(String item) {
		return createRunActivity("test", item, "test", "test", "test");
	}
	
	public static Activity createRunActivity(String user, String tool, String project, String account, String lab) {
		Calendar currentDate = Calendar.getInstance();
		int y = currentDate.get( Calendar.YEAR );
		int m = currentDate.get( Calendar.MONTH );
		int d = currentDate.get( Calendar.DAY_OF_MONTH );
		int h = currentDate.get( Calendar.HOUR );
		int i = currentDate.get( Calendar.MINUTE );
		int s = currentDate.get( Calendar.SECOND );
		 
		Activity activity = new Activity();
		activity.account = account  ;
		activity.agent = user        ;
		activity.amount = 0.0   ;
		try {
			Class<?> activityClass = activity.getClass();
			activityClass.getField("area").set(activity, "");
		} catch (Exception e) {
			
		}
		activity.bdate = new Timestamp(false, (short)y,
				(short)m,
				(short)d,
				(short)h,
				(short)i,
				(short)s,
				(short)0				
				)      ;
		activity.creationDate = new Timestamp(false, (short)y,
				(short)m,
				(short)d,
				(short)h,
				(short)i,
				(short)s,
				(short)0				
				)      ;
		activity.creator = "coral"      ;
		activity.edate = new Timestamp(true, (short)0,
				(short)0,
				(short)0,
				(short)0,
				(short)0,
				(short)0,
				(short)0				
				)      ;
		activity.id = "not assigned"    ;
		activity.isNull = false ;
		activity.item = tool  ;
		activity.lab = lab        ;
		activity.member = user       ;
		activity.primeAmount = 0.0      ;
		activity.process = "default"    ;
		activity.project = project  ;
		activity.reference = "not assigned"     ;
		activity.stale = false  ;
		activity.type = "Generic Activity"      ;
		return activity;
	}	

}
