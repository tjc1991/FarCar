package com.squareup.timessquare;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author jin
 * 2014年5月28日 上午9:34:31
 * 
 */
public class DateUtils {
    /** 获取当前时间--如：2012-11-06 12:12:10 */
    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(date);
    }

    /**
     * 返回时间对象;<br/>
     * format为时间格式如("yyyy/MM/dd")<br/>
     * 返回null表示出错了
     */
    public static Date getDate(String time, String format) {
        Date date = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            df.setLenient(false);
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        return date;
    }

    /** 毫秒换成几天前几小时几分钟 */
    public static String periodToString(Long millisecond) {
        String str = "";
        long day = millisecond / 86400000;
        long hour = (millisecond % 86400000) / 3600000;
        long minute = (millisecond % 86400000 % 3600000) / 60000;
        if (day > 0) {
            str = String.valueOf(day) + "天前";
        }
        if (hour > 0) {
            str += String.valueOf(hour) + "小时";
        }
        if (minute > 0) {
            str += String.valueOf(minute) + "分钟";
        }
        return str;
    }

    /**
     * 计算几天前;<br/>
     * 传入开始时间(比如"2012/11/06对应format为"yyyy/MM/dd";<br/>
     * 如果返回-1表示格式错误
     */
    public static int getIntervalDays(String beginTime, String format) {
        int dayNum = 0;
        try {
            Date start = getDate(beginTime, format);
            Date now = new Date();
            long res = now.getTime() - start.getTime();
            dayNum = (int) (((res / 1000) / 3600) / 24);
            System.out.println(dayNum + "天前");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        return dayNum;
    }

    /**
     * 计算几天前;<br/>
     * 传入开始时间(格式：2012-11-06 12:12:10)<br/>
     * 如果返回-1表示格式错误
     */
    public static int getIntervalDays(String beginTime) {
        return getIntervalDays(beginTime, "yyyy-MM-dd hh:mm:ss");
    }

    /**
     * 返回当前日期xxxx年x月xx日 星期x
     * 
     * @return
     */
    public static String getDate() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
        int w = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        String mDate = c.get(Calendar.YEAR) + "年" + c.get(Calendar.MONTH) + "月"
                + c.get(Calendar.DATE) + "日  " + weekDays[w];
        return mDate;
    }
    
    /**
     * 返回当前x月xx日 星期x
     * 
     * @return
     */
    public static String getDateNew() {
      Date date = new Date();
      Calendar c = Calendar.getInstance();
      c.setTime(date);
      String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
      int w = c.get(Calendar.DAY_OF_WEEK) - 1;
      if (w < 0) {
          w = 0;
      }
      String mDate = c.get(Calendar.MONTH)+1 + "月"
              + c.get(Calendar.DATE) + "日  " ;
      return mDate;
    }

    /**
     * 返回毫秒
     * 
     * @param date
     *            日期
     * @return 返回毫秒
     */
    public static long getMillis(java.util.Date date) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }

    /**
     * 日期相加
     * 
     * @param date
     *            日期
     * @param day
     *            天数
     * @return 返回相加后的日期
     */
    public static java.util.Date addDate(java.util.Date date, int day) {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.setTimeInMillis(getMillis(date) + ((long) day) * 24 * 3600 * 1000);
        return c.getTime();
    }

    /**
     * 日期相减
     * 
     * @param date
     *            日期
     * @param date1
     *            日期
     * @return 返回相减后的日期
     */
    public static int diffDate(java.util.Date date, java.util.Date date1) {
        return (int) ((getMillis(date) - getMillis(date1)) / (24 * 3600 * 1000));
    }

    /**
     * 计算当前是什么星座 <br/>
     * 返回如"天蝎座"
     */
    public static String xingzuo(Date s) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(s);
        String xingzuo = "无";
        int day = cal.get(Calendar.DAY_OF_YEAR);
        if ((cal.get(Calendar.YEAR) % 4 == 0)
                && (cal.get(Calendar.YEAR) % 100 != 0)
                || (cal.get(Calendar.YEAR) % 400 == 0)) {
            if ((day >= 1 && day <= 19) || (day >= 357 && day <= 366)) {
                xingzuo = "魔蝎座";
            } else if (day >= 20 && day <= 49) {
                xingzuo = "水瓶座";
            } else if (day >= 50 && day <= 80) {
                xingzuo = "双鱼座";
            } else if (day >= 81 && day <= 110) {
                xingzuo = "白羊座";
            } else if (day >= 111 && day <= 141) {
                xingzuo = "金牛座";
            } else if (day >= 142 && day <= 173) {
                xingzuo = "双子座";
            } else if (day >= 174 && day <= 203) {
                xingzuo = "巨蟹座";
            } else if (day >= 204 && day <= 235) {
                xingzuo = "狮子座";
            } else if (day >= 236 && day <= 266) {
                xingzuo = "处女座";
            } else if (day >= 267 && day <= 296) {
                xingzuo = "天秤座";
            } else if (day >= 297 && day <= 326) {
                xingzuo = "天蝎座";
            } else if (day >= 327 && day <= 356) {
                xingzuo = "射手座";
            }
        } else {
            if ((day >= 1 && day <= 19) || (day >= 357 && day <= 366)) {
                xingzuo = "魔蝎座";
            } else if (day >= 20 && day <= 48) {
                xingzuo = "水瓶座";
            } else if (day >= 49 && day <= 79) {
                xingzuo = "双鱼座";
            } else if (day >= 80 && day <= 109) {
                xingzuo = "白羊座";
            } else if (day >= 110 && day <= 140) {
                xingzuo = "金牛座";
            } else if (day >= 141 && day <= 172) {
                xingzuo = "双子座";
            } else if (day >= 173 && day <= 202) {
                xingzuo = "巨蟹座";
            } else if (day >= 203 && day <= 234) {
                xingzuo = "狮子座";
            } else if (day >= 235 && day <= 265) {
                xingzuo = "处女座";
            } else if (day >= 266 && day <= 295) {
                xingzuo = "天秤座";
            } else if (day >= 296 && day <= 325) {
                xingzuo = "天蝎座";
            } else if (day >= 326 && day <= 355) {
                xingzuo = "射手座";
            }
        }
        return xingzuo;
    }
}
