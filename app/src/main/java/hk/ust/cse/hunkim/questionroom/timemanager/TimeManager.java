package hk.ust.cse.hunkim.questionroom.timemanager;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by PakShing on 19/11/2015.
 */
public class TimeManager {

    private Long time;
    public TimeManager(Long t) { this.time = t; }

    //If you want to know more about the function, plz visit here http://developer.android.com/reference/android/text/format/DateUtils.html
    public String getDate()
    {
        long currentTime = new Date().getTime();
        long timeResolution = 0;
        long timeDiff = currentTime-this.time;
        if(timeDiff < DateUtils.SECOND_IN_MILLIS*5){
            return "Just now";
        }

        if(timeDiff/DateUtils.MINUTE_IN_MILLIS == 0){
            timeResolution = DateUtils.SECOND_IN_MILLIS;
        }else if(timeDiff/DateUtils.HOUR_IN_MILLIS == 0){
            timeResolution = DateUtils.MINUTE_IN_MILLIS;
        }else if(timeDiff/DateUtils.DAY_IN_MILLIS == 0){
            timeResolution = DateUtils.HOUR_IN_MILLIS;
        }else{
            DateFormat df = new SimpleDateFormat("yyyy/MM/dd KK:mm aa");
            Date date = (new Date(this.time));
            return df.format(date);
        }
        return DateUtils.getRelativeTimeSpanString(this.time, currentTime, timeResolution).toString();
    }


}
