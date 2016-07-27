package edu.utah.nanofab.coralapi.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.opencoral.idl.Timestamp;

public class TimestampConverter {
 
  public static final boolean IGNORE_NULLS = true;

  public static Timestamp dateToTimestamp(Date bdate2) {
    
    // If the provided date is null then just create a null Timestamp with arbitrary
    // parameters.
    if (bdate2 == null) {
      Timestamp tstamp = new Timestamp();
      tstamp.isNull = true;
      
      return tstamp;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(bdate2);
    Timestamp tstamp = new Timestamp();
    tstamp.year = (short) cal.get(Calendar.YEAR);
    tstamp.month = (short) (cal.get(Calendar.MONTH) + 1);
    tstamp.day = (short) cal.get(Calendar.DAY_OF_MONTH);
    tstamp.hour = (short) cal.get(Calendar.HOUR_OF_DAY);
    tstamp.minute = (short) cal.get(Calendar.MINUTE);
    tstamp.second = (short) cal.get(Calendar.SECOND);
    return tstamp;
  }

  public static Date timestampToDate(Timestamp tstamp) {
	if (!IGNORE_NULLS) {
		if (tstamp.isNull) { return null; }
	}
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, tstamp.year);
    cal.set(Calendar.MONTH, tstamp.month - 1);
    cal.set(Calendar.DAY_OF_MONTH, tstamp.day);
    cal.set(Calendar.HOUR_OF_DAY, tstamp.hour);
    cal.set(Calendar.MINUTE, tstamp.minute);
    cal.set(Calendar.SECOND, tstamp.second);
    return cal.getTime();
  }
  
  public static String dateToAdapterString(Date d) {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return format.format(d);
  }
  
  public static Date stringToDate(String s) throws ParseException {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date d = format.parse(s);
    return d;
  }  
  
  public static Date dateFromDateComponents(int y, int m, int d, int h, int minute, int second) {
    Timestamp tstamp = new Timestamp(false, 
        (short)y, 
        (short)m, 
        (short)d, 
        (short)h, 
        (short)minute, 
        (short)second, 
        (short)0);
    return timestampToDate(tstamp);
  }
}
