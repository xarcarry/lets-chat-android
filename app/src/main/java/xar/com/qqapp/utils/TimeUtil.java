package xar.com.qqapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static String getCurrentTime(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(date);
    }

    public static String longTimeToString(long time){
        String str = "" + time;
        String timeStr = "";
        if (str.length() > 3){
            timeStr = "上滑取消，录音时长：" + (str.substring(0,str.length() - 3) + "s");
        }else{
            timeStr = "上滑取消，录音时长：" + 0 + "s";
        }
        return timeStr;
    }
}
