package fpt.life.finalproject.util;

import android.annotation.SuppressLint;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimestampUtil {

    @SuppressLint("SimpleDateFormat")
    public static String getSendTime(Timestamp timestamp) {
        String sendTime;
        if (getDayDifferenceFromTimestamp(timestamp) >= 1)
            sendTime = new SimpleDateFormat("MMM dd, yyyy, HH:mm").format(timestamp.toDate());
        else
            sendTime = new SimpleDateFormat("HH:mm").format(timestamp.toDate());
        return sendTime;
    }

    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    public static String getLastTimeOnline(Timestamp timestamp, boolean isOnline) {
        String lastTimeOnline;
        if (isOnline)
            lastTimeOnline = "Active now";
        else if (getYearDifferenceFromTimestamp(timestamp) >= 1)
            lastTimeOnline = new SimpleDateFormat("MMM dd, yyyy").format(timestamp.toDate());
        else if (getWeekDifferenceFromTimestamp(timestamp) > 1)
            lastTimeOnline = String.format("Online %d weeks ago", getWeekDifferenceFromTimestamp(timestamp));
        else if (getWeekDifferenceFromTimestamp(timestamp) == 1)
            lastTimeOnline = String.format("Online a week ago");
        else if (getDayDifferenceFromTimestamp(timestamp) > 1)
            lastTimeOnline = String.format("Online %d days ago", getDayDifferenceFromTimestamp(timestamp));
        else if (getDayDifferenceFromTimestamp(timestamp) == 1)
            lastTimeOnline = String.format("Online a day ago");
        else if (getHourDifferenceFromTimestamp(timestamp) > 1)
            lastTimeOnline = String.format("Online %d hours ago", getHourDifferenceFromTimestamp(timestamp));
        else if (getHourDifferenceFromTimestamp(timestamp) == 1)
            lastTimeOnline = String.format("Online an hour ago");
        else if (getMinuteDifferenceFromTimestamp(timestamp) > 1)
            lastTimeOnline = String.format("Online %d minutes ago", getMinuteDifferenceFromTimestamp(timestamp));
        else if (getMinuteDifferenceFromTimestamp(timestamp) == 1)
            lastTimeOnline = String.format("Online a minute ago");
        else
            lastTimeOnline = "Just now";

        return lastTimeOnline;
    }

    private static long getMinuteDifferenceFromTimestamp(Timestamp timestamp) {
        Date now = Timestamp.now().toDate();
        Date otherDate = timestamp.toDate();

        long differenceInTime = now.getTime() - otherDate.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(differenceInTime) % 60;
    }

    private static long getHourDifferenceFromTimestamp(Timestamp timestamp) {
        Date now = Timestamp.now().toDate();
        Date otherDate = timestamp.toDate();

        long differenceInTime = now.getTime() - otherDate.getTime();
        return TimeUnit.MILLISECONDS.toHours(differenceInTime) % 24;
    }

    private static long getDayDifferenceFromTimestamp(Timestamp timestamp) {
        Date now = Timestamp.now().toDate();
        Date otherDate = timestamp.toDate();

        long differenceInTime = now.getTime() - otherDate.getTime();
        return TimeUnit.MILLISECONDS.toDays(differenceInTime) % 365;
    }

    private static long getWeekDifferenceFromTimestamp(Timestamp timestamp) {
        Date now = Timestamp.now().toDate();
        Date otherDate = timestamp.toDate();

        long differenceInTime = now.getTime() - otherDate.getTime();
        return TimeUnit.MILLISECONDS.toDays(differenceInTime) % 365 / 7;
    }

    private static long getYearDifferenceFromTimestamp(Timestamp timestamp) {
        Date now = Timestamp.now().toDate();
        Date otherDate = timestamp.toDate();

        long differenceInTime = now.getTime() - otherDate.getTime();
        return TimeUnit.MILLISECONDS.toDays(differenceInTime) / 365;
    }
}
