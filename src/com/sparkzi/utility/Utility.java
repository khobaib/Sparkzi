package com.sparkzi.utility;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

@SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
public class Utility {

	public static String token;
	public static final String EXTRA_MESSAGE = "message";
	public static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";
	public static final String SENDER_ID = "326205185342";

	public static final String[] MONTH_NAME = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT",
			"NOV", "DEC" };

	public static final String[] WHO_I_AM = { "Man looking for a woman", "Woman looking for a man" };

	public static final String[] SHOW_ME = { "Both men & women", "Men", "Women" };

	public static final String[] ACTIVITY_TEMPLATE = { "saw a movie", "listened to music", "watched television",
			"ate at a restaurant", "went to a bar", "attended an event", "read a book", "something else" };

	public static final String[] ACTIVITY_QUESTION = { "Which movie did you see?", "Which music did you listen to?",
			"What did you watch?", "Which restaurant?", "Which bar?", "Which event?", "Which book?", "What did you do?" };

	public static final String[] COUNTRY_LIST = { "Australia", "Canada", "India", "UK", "US" };

	public static final String[] QUESTION_TEXTS = { "Two truths and a lie about me are ...",
			"You come into possesion of a comically oversized bag of money. How do you spend it?",
			"I'd be excited to meet someone who...", "On a typical day off, I like to...",
			"If i had to appoint a panel of three people, dead or alive, to make every decision for me, I'd choose",
			"If i could my sixteen-year-old-self any piece of advice, it would be...", "Describe your best friend",
			"If a friend were visiting from out of town, three things we'd have to do would be...",
			"The fictional character I'd love to go on date with is... because...", };

	public static final String[] ESSENTIAL_TEXTS = { "education", "ethnicity", "diet", "drinks", "smokes", "religion",
			"kids", "politics", "starsign", "profession", "hometown", "languages" };

	public static final String[] AGE_RANGE = { "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32",
			"33", "34", "35", "36", "37", "38", "39" };

	public static final String[] Gender = { "Male", "Female" };

	public static final String[] LAST_NIGHT_VERB = { "dummy", "saw", "listened", "watched", "ate at", "went to",
			"attended", "read" };

	public static final String[] education_spinner_options = { "School of life", "High School", "College",
			"Grad School", "PhD" };

	public static final String[] ethnicity_spinner_options = { "Indian", "Asian", "White", "Black",
			"Not Indian, but kinda like an Indian" };

	public static final String[] diet_spinner_options = { "I'll eat anything", "Carnivorous", "Vegetarian", "Vegan" };

	public static final String[] drinks_spinner_options = { "Never", "Rarely", "Sometimes", "Regularly",
			"Like a rockstar" };

	public static final String[] smokes_spinner_options = { "Never", "When drinking", "Sometimes",
			"Daily but with a tinge of guilt", "Proudly" };

	public static final String[] religion_spinner_options = { "Agnostic", "Atheist", "Buddhist", "Christian", "Hindu",
			"Jain", "Muslim", "Sikh", "Spiritual, not religious" };

	public static final String[] kids_spinner_options = { "Have kids at home", "Have kids elsewhere",
			"Want kids someday", "Not interested in kids" };

	public static final String[] politics_spinner_options = { "Workers of the world, unite!", "Liberal", "Conservative" };

	public static final String[] sign_spinner_options = { "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
			"Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces", "Don't know, don't care" };

	public enum SLIDING_MENU_OPTION {
		HOME(0), CONVERSATIONS(1), LASTNIGHT(2), SEARCH(3), FAVORITES(4), PROFILE(5), SETTINGS(6), LOGOUT(7);

		private int value;

		private SLIDING_MENU_OPTION(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

	};

	public static boolean createDirectory() {
		if (!SdIsPresent())
			return false;

		File directory = Constants.APP_DIRECTORY;
		if (!directory.exists())
			directory.mkdir();
		return true;
	}

	/** Returns whether an SD card is present and writable **/
	public static boolean SdIsPresent() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/*
	 * dateFromDb -> yyyy-mm-dd we want to show in the format dd (month_name)
	 * year
	 */
	public static String parseDate(String dateFromDb) {
		String year = dateFromDb.substring(0, 4);
		int month = Integer.parseInt(dateFromDb.substring(5, 7));
		String day = dateFromDb.substring(8, 10);
		Log.d(">>>>>>>>", "year = " + year + " month = " + month + " day = " + day);
		String dateToShow = dateFromDb;
		if (month > 0 && month <= 12)
			dateToShow = day + " " + MONTH_NAME[month - 1] + " " + year;
		return dateToShow;
	}

	public static boolean hasInternet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@SuppressLint("DefaultLocale")
	public static String capitalizeString(String str) {
		if (str == null || str.equals("") || str.length() == 0)
			return str;
		return (str.substring(0, 1).toUpperCase() + str.substring(1, str.length()));
	}

	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(Constants.DISPLAY_MESSAGE_ACTION);
		intent.putExtra(Constants.EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}

	private static Calendar clearTimes(Calendar c) {
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}

	public static String getFormattedTime(String dateTime) {
		if (dateTime == null)
			return null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		try {
			date = dateFormat.parse(dateTime);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			long millis = c.getTimeInMillis();
			if (isToday(millis)) {
				return formatTOdayTime(millis);
			} else if (getDaysago(millis) < 3) {
				if (getDaysago(millis) == 1)
					return "yesterday";
				return getDaysago(millis) + " days ago";
			}

			else {
				return "more than 3 days ago";
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static String formatTOdayTime(long millis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm a");
		dateFormat.setCalendar(cal);
		return dateFormat.format(cal.getTime());
	}

	public static Boolean isToday(long millis) {
		Calendar today = Calendar.getInstance();
		today = clearTimes(today);

		if (millis > today.getTimeInMillis())
			return true;
		return false;
	}

	public static int getDaysago(long millis) {
		Calendar today = Calendar.getInstance();
		today = clearTimes(today);

		float daysago = ((today.getTimeInMillis() - millis) / (24 * 60 * 60 * 1000));
		int dayagoinint = (int) daysago;
		if (daysago > (float) dayagoinint) {
			dayagoinint++;
		}

		return dayagoinint;
	}

	public static String formatTime(long millis) {

		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);

		int day = cal.get(Calendar.DAY_OF_MONTH);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);

		String timeOfMsg = MONTH_NAME[month] + " " + day;

		long currentMillis = System.currentTimeMillis();
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(currentMillis);

		int currentYear = c.get(Calendar.YEAR);
		if (year != currentYear)
			timeOfMsg = timeOfMsg + ", " + year;

		return timeOfMsg;
	}

}
