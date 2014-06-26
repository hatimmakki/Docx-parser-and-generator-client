package com.docreader.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UtilDate {

	private static final String DATE_FORMAT = "d MMM y";

	static public long getDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		return (long) (c.getTimeInMillis() / 1000L);
	}
	
	static public int getSecondsFromMidnight() {
	    Calendar c = Calendar.getInstance();
	    long now = c.getTimeInMillis();
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    long passed = now - c.getTimeInMillis();
	    int secondsPassed = (int) (passed / 1000);
	    
	    return secondsPassed;
	}

	static public String formatDate(String timestamp) {
		if (null == timestamp || 0 == timestamp.length())
			return "";
		
		try {
			long t = Long.parseLong(timestamp);
			return formatDate(t);
		} catch (NumberFormatException e) {
			return "";
		}
	}

	static public String formatDate(Calendar cal) {
        return formatDate(cal.getTime());
    }

	static public String formatDate(Date timestamp) {
		return formatDate(timestamp.getTime() / 1000L);
	}

	static public String formatDate(long unixTimestamp) {
	  SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
	  String result = formatter.format(unixTimestamp * 1000L);
	  
	  return result;
	}
	
	/**
	 * Get unix timestamp
	 * 
	 * @return
	 */
	public static long getCurrentTimestamp() {
	  return (long) (System.currentTimeMillis() / 1000L);
  }

}
