package anxa.com.smvideo.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;

import org.joda.time.DateTime;
import org.joda.time.Weeks;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.contracts.Carnet.ExerciseContract;
import anxa.com.smvideo.contracts.Carnet.MealCommentContract;
import anxa.com.smvideo.contracts.Carnet.MealContract;
import anxa.com.smvideo.contracts.Carnet.MoodContract;
import anxa.com.smvideo.contracts.WeightGraphContract;
import anxa.com.smvideo.models.Workout;

/**
 * Created by angelaanxa on 5/23/2017.
 */

public class AppUtil {
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    public static String SHA1(String text) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();
        return asHex(sha1hash);
    }

    public static String asHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    public static String getDefaultUserAgent(Context context) {
        StringBuilder result = new StringBuilder(64);
        result.append(context.getString(R.string.USER_AGENT_APP_NAME));
        result.append(" ");
        result.append(context.getString(R.string.app_version));
        result.append(" Dalvik/");
        result.append(System.getProperty("java.vm.version")); // such as 1.1.0
        result.append(" (Linux; U; Android ");

        String version = Build.VERSION.RELEASE; // "1.0" or "3.4b5"
        result.append(version.length() > 0 ? version : "1.0");

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model.length() > 0) {
                result.append("; ");
                result.append(model);
            }
        }
        String id = Build.ID; // "MASTER" or "M4-rc20"
        if (id.length() > 0) {
            result.append(" Build/");
            result.append(id);
        }
        result.append(")");

        System.out.println("useragent: " + result.toString());
        return result.toString();
    }

    public static boolean isEmail(String email) {
        Pattern pattern1 = Pattern
                .compile("^([a-zA-Z0-9_.+-])+@([a-zA-Z0-9_.-])+\\.([a-zA-Z])+([a-zA-Z])+");

        Matcher matcher1 = pattern1.matcher(email);

        return matcher1.matches();
    }

    public static String get1MDateRangeDisplay(boolean initDate, boolean previous, int index) {
        String stringDisplay = "";

        Calendar cal = Calendar.getInstance();

        if (ApplicationData.getInstance().currentDateRangeDisplay_date == null || initDate) {
            cal.setTime(new Date());
        } else {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date);
        }

        if (previous) {
            cal.add(Calendar.MONTH, -1);
        } else {
            cal.add(Calendar.MONTH, 1);
        }

        if (initDate) {
            stringDisplay = new SimpleDateFormat("MMM dd").format(new Date());
            stringDisplay = new SimpleDateFormat("MMM dd").format(cal.getTime()) + " - " + stringDisplay;
            ApplicationData.getInstance().currentDateRangeDisplay_date = new Date();

        } else {
            stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime());
            ApplicationData.getInstance().currentDateRangeDisplay_date = cal.getTime();
        }

        ApplicationData.getInstance().currentDateRangeDisplay = stringDisplay;

        return stringDisplay;
    }

    private static boolean fromInitDate = false;

    public static String get1WDateRangeDisplay(boolean initDate, boolean previous)
    {

        String stringDisplay = "";
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        if (ApplicationData.getInstance().currentDateRangeDisplay_date == null || initDate)
        {
            fromInitDate = true;
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, -6);

            stringDisplay = new SimpleDateFormat("MMM dd").format(cal.getTime()) + " - " + new SimpleDateFormat("MMM dd").format(new Date());
            //store previous month
            ApplicationData.getInstance().currentDateRangeDisplay_date = cal.getTime();
            ApplicationData.getInstance().currentDateRangeDisplay_date2 = new Date();
        }
        else
        {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date);
            cal2.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date2);

            if (previous)
            {
                if (!fromInitDate) {
                    cal.add(Calendar.WEEK_OF_YEAR, -1);
                    cal2.add(Calendar.WEEK_OF_YEAR, -1);
                } else {
                    fromInitDate = false;
                }
            } else {
                cal.add(Calendar.WEEK_OF_YEAR, +1);
                cal2.add(Calendar.WEEK_OF_YEAR, +1);
            }

            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            cal2.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

            stringDisplay = new SimpleDateFormat("MMM dd").format(cal2.getTime());
            stringDisplay = new SimpleDateFormat("MMM dd").format(cal.getTime()) + " - " + stringDisplay;

            //store previous month
            ApplicationData.getInstance().currentDateRangeDisplay_date = cal.getTime();
            ApplicationData.getInstance().currentDateRangeDisplay_date2 = cal2.getTime();
        }
        ApplicationData.getInstance().currentDateRangeDisplay = stringDisplay;

        return stringDisplay;
    }

    public static String get3MDateRangeDisplay(boolean initDate, boolean previous, int index) {
        String stringDisplay = "";
        Calendar cal = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        if (ApplicationData.getInstance().currentDateRangeDisplay_date == null || initDate) {
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, -2);

            stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime()) + " - " + new SimpleDateFormat("MMM yyyy").format(new Date());
            //store previous month
            ApplicationData.getInstance().currentDateRangeDisplay_date = cal.getTime();
            ApplicationData.getInstance().currentDateRangeDisplay_date2 = new Date();

        } else {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date);
            cal2.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date2);
            if (previous) {
                cal.add(Calendar.MONTH, -3);
                cal2.add(Calendar.MONTH, -3);
                stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal2.getTime());
                stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime()) + " - " + stringDisplay;

                //store previous month
                ApplicationData.getInstance().currentDateRangeDisplay_date = cal.getTime();
                ApplicationData.getInstance().currentDateRangeDisplay_date2 = cal2.getTime();

            } else {
                cal.add(Calendar.MONTH, +3);
                cal2.add(Calendar.MONTH, +3);
                stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal2.getTime());
                stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime()) + " - " + stringDisplay;

                //store previous month
                ApplicationData.getInstance().currentDateRangeDisplay_date = cal.getTime();
                ApplicationData.getInstance().currentDateRangeDisplay_date2 = cal2.getTime();
            }
        }

        ApplicationData.getInstance().currentDateRangeDisplay = stringDisplay;

        return stringDisplay;
    }

    public static String get1YDateRangeDisplay(boolean initDate, boolean previous) {
        String stringDisplay = "";

        Calendar cal = Calendar.getInstance();

        if (ApplicationData.getInstance().currentDateRangeDisplay_date == null || initDate) {
            cal.setTime(new Date());
        } else {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date);

            if (previous) {
                cal.add(Calendar.YEAR, -1);
            } else {
                cal.add(Calendar.YEAR, 1);
            }
        }

        if (initDate) {
            cal.add(Calendar.MONTH, -11);
            stringDisplay = new SimpleDateFormat("MMM yyyy").format(new Date());
            stringDisplay = new SimpleDateFormat("MMM yyyy").format(cal.getTime()) + " - " + stringDisplay;
            ApplicationData.getInstance().currentDateRangeDisplay_date = new Date();

        } else {
            stringDisplay = new SimpleDateFormat("yyyy").format(cal.getTime());
            ApplicationData.getInstance().currentDateRangeDisplay_date = cal.getTime();
        }

        ApplicationData.getInstance().currentDateRangeDisplay = stringDisplay;

        return stringDisplay;
    }

    /**
     * @return list of Weight data in a year
     * must be 12 items or less
     * return the latest weight recorderd in the month
     **/
    public static List<WeightGraphContract> get1YWeightList(boolean initDate, int dateRangeIndex) {
        List<WeightGraphContract> weightGraphDataArrayList = ApplicationData.getInstance().weightGraphContractList;
        Hashtable<Date, WeightGraphContract> weightDate = new Hashtable<Date, WeightGraphContract>();
        ArrayList<Date> dateList = dateList = new ArrayList<>(12);

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        if (initDate) {
            cal.setTime(new Date());

        } else {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date);
            cal.set(Calendar.MONTH, Calendar.DECEMBER);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            cal.set(Calendar.DAY_OF_YEAR, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_YEAR));

            calValid.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date);
            calValid.set(Calendar.YEAR, calValid.get(Calendar.YEAR));
            calValid.set(Calendar.MONTH, Calendar.DECEMBER);
        }

        dateList.add(cal.getTime());

        //include current date;

        WeightGraphContract dummyWeight = new WeightGraphContract();
        dummyWeight.Date = cal.getTime().getTime();
        dummyWeight.WeightKg = 0;
        weightDate.put(cal.getTime(), dummyWeight);

        for (int i = 1; i < 12; i++) {
            calValid.add(Calendar.MONTH, -1);
            dateList.add(calValid.getTime());

            dummyWeight = new WeightGraphContract();
            dummyWeight.Date = calValid.getTime().getTime();
            dummyWeight.WeightKg = 0;
            weightDate.put(calValid.getTime(), dummyWeight);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (WeightGraphContract weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(toDate(weight.Date));
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (toDate(weight.Date).before(cal.getTime()) && toDate(weight.Date).after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (monthIndex == calIndex_date.get(Calendar.MONTH)) {
                        weightDate.put(date_list, weight);
                    }
                }
            }
        }

        ArrayList<WeightGraphContract> weightGraphDataArrayList_1y = new ArrayList<WeightGraphContract>();
        for (Date date_list : dateList) {
            weightGraphDataArrayList_1y.add(weightDate.get(date_list));
        }

        return weightGraphDataArrayList_1y;
    }

    public static List<WeightGraphContract> getAllWeightList() {
        List<WeightGraphContract> weightGraphDataArrayList = ApplicationData.getInstance().weightGraphContractList;
        WeightGraphContract oldestWeight = AppUtil.getOldestWeight();

        Collections.sort(weightGraphDataArrayList, new Comparator<WeightGraphContract>() {
            public int compare(WeightGraphContract o1, WeightGraphContract o2) {
                return toDate(o1.Date).compareTo(toDate(o2.Date));
            }
        });

        ArrayList<WeightGraphContract> weightGraphDataArrayList_all = new ArrayList<WeightGraphContract>();

        Hashtable<Date, WeightGraphContract> weightDate = new Hashtable<Date, WeightGraphContract>();
        ArrayList<Date> dateList = new ArrayList<>();

        //newest date
        Calendar calLatest = Calendar.getInstance();
        calLatest.setTime(new Date());

        //oldest weight
        Calendar calValid = Calendar.getInstance();
        if (toDate(oldestWeight.Date) != null) {
            calValid.setTime(toDate(oldestWeight.Date));
        } else {
            calValid.setTime(new Date());
        }

        //present year only
        if (calValid.get(Calendar.YEAR) == calLatest.get(Calendar.YEAR)) {
            return get1YWeightList(true, 0);
        }


        dateList.add(calLatest.getTime());

        WeightGraphContract dummyWeight = new WeightGraphContract();
        dummyWeight.Date = calLatest.getTime().getTime();
        dummyWeight.WeightKg = 0;
        weightDate.put(calLatest.getTime(), dummyWeight);

        int monthsDiff = getMonthsDifference(calValid.getTime(), calLatest.getTime());

        for (int i = 1; i < monthsDiff; i++) {
            calLatest.add(Calendar.MONTH, -1);
            dateList.add(calLatest.getTime());

            dummyWeight = new WeightGraphContract();
            dummyWeight.Date = calLatest.getTime().getTime();
            dummyWeight.WeightKg = 0;
            weightDate.put(calLatest.getTime(), dummyWeight);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (WeightGraphContract weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(toDate(weight.Date));
            int monthIndex = calIndex.get(Calendar.MONTH);
            int yearIndex = calIndex.get(Calendar.YEAR);

            if (toDate(weight.Date).before(new Date()) && toDate(weight.Date).after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (monthIndex == calIndex_date.get(Calendar.MONTH) && yearIndex == calIndex_date.get(Calendar.YEAR)) {
                        weightDate.put(date_list, weight);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            weightGraphDataArrayList_all.add(weightDate.get(date_list));
        }
        return weightGraphDataArrayList_all;
    }

    public static WeightGraphContract getOldestWeight() {
        List<WeightGraphContract> weightGraphDataArrayList = ApplicationData.getInstance().weightGraphContractList;
        WeightGraphContract oldestWeight = new WeightGraphContract();

        if (weightGraphDataArrayList.size() > 0) {
            oldestWeight = weightGraphDataArrayList.get(0);
        }

        for (WeightGraphContract weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(toDate(weight.Date));
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (toDate(weight.Date).before(toDate(oldestWeight.Date))) {
                oldestWeight = weight;
            }
        }

        return oldestWeight;
    }

    public static Date convertStringToDate(String dateToConvert) {
        //sample date: 2017-06-26T19:32:57.247
//        2017-06-26T19:32:57.247
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);

        try {
            cal.setTime(sdf.parse(dateToConvert));
            return cal.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return cal.getTime();
    }

    public static int getMonthsDifference(Date date1, Date date2) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(date1);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(date2);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);

        return diffMonth;
    }

    public static List<WeightGraphContract> get3MWeightList(boolean initDate) {
        List<WeightGraphContract> weightGraphDataArrayList = ApplicationData.getInstance().weightGraphContractList;
        ArrayList<WeightGraphContract> weightGraphDataArrayList_3m = new ArrayList<WeightGraphContract>();

        Hashtable<Date, WeightGraphContract> weightDate = new Hashtable<Date, WeightGraphContract>();

        Collections.sort(weightGraphDataArrayList, new Comparator<WeightGraphContract>() {
            public int compare(WeightGraphContract o1, WeightGraphContract o2) {
                return toDate(o1.Date).compareTo(toDate(o2.Date));
            }
        });

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        ArrayList<Date> dateList = new ArrayList(3);

        if (initDate) {
            cal.setTime(new Date());
            calValid.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date2);
            cal.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
            calValid.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date2);
            calValid.set(Calendar.DAY_OF_MONTH, 1);
        }

        //include current date;
        dateList.add(cal.getTime());
        WeightGraphContract dummyWeight = new WeightGraphContract();
        dummyWeight.Date = cal.getTime().getTime();
        dummyWeight.WeightKg = 0;
        weightDate.put(cal.getTime(), dummyWeight);

        for (int i = 1; i < 3; i++) {
            calValid.add(Calendar.MONTH, -1);
            dateList.add(calValid.getTime());

            dummyWeight = new WeightGraphContract();
            dummyWeight.Date = calValid.getTime().getTime();
            dummyWeight.WeightKg = 0;
            weightDate.put(calValid.getTime(), dummyWeight);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (WeightGraphContract weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(toDate(weight.Date));
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (toDate(weight.Date).before(cal.getTime()) && toDate(weight.Date).after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (monthIndex == calIndex_date.get(Calendar.MONTH)) {
                        weightDate.put(date_list, weight);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            weightGraphDataArrayList_3m.add(weightDate.get(date_list));
        }

        return weightGraphDataArrayList_3m;
    }

    public static List<WeightGraphContract> get1MWeightList(boolean initDate, int dateIndex) {

        ArrayList<WeightGraphContract> weightGraphDataArrayList_1m = new ArrayList<WeightGraphContract>();
        List<WeightGraphContract> weightGraphDataArrayList = ApplicationData.getInstance().weightGraphContractList;
        Hashtable<Date, WeightGraphContract> weightDate = new Hashtable<Date, WeightGraphContract>();
        ArrayList<Date> dateList;

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        if (initDate) {
            cal.setTime(new Date());
            dateList = new ArrayList<>(31);
        } else {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date);
            calValid.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date);

            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            calValid.set(Calendar.MONTH, calValid.get(Calendar.MONTH));
            calValid.set(Calendar.DAY_OF_MONTH, calValid.getActualMaximum(Calendar.DAY_OF_MONTH));
            dateList = new ArrayList<>(cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        }
        //include current date;
        dateList.add(cal.getTime());

        WeightGraphContract dummyWeight = new WeightGraphContract();
        dummyWeight.Date = cal.getTime().getTime();
        dummyWeight.WeightKg = 0;

        weightDate.put(cal.getTime(), dummyWeight);

        for (int i = 1; i < 31; i++) {
            calValid.add(Calendar.DAY_OF_MONTH, -1);
            dateList.add(calValid.getTime());

            dummyWeight = new WeightGraphContract();
            dummyWeight.Date = calValid.getTime().getTime();
            dummyWeight.WeightKg = 0;

            weightDate.put(calValid.getTime(), dummyWeight);
        }

        Collections.sort(dateList, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (WeightGraphContract weight : weightGraphDataArrayList) {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(toDate(weight.Date));
            int dayIndex = calIndex.get(Calendar.DAY_OF_MONTH);

            if (toDate(weight.Date).before(cal.getTime()) && toDate(weight.Date).after((Date) dateList.get(0))) {
                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (dayIndex == calIndex_date.get(Calendar.DAY_OF_MONTH)) {
                        weightDate.put(date_list, weight);
                    }
                }
            }
        }

        for (Date date_list : dateList) {
            weightGraphDataArrayList_1m.add(weightDate.get(date_list));
        }

        Collections.sort(weightGraphDataArrayList_1m, new Comparator<WeightGraphContract>() {
            public int compare(WeightGraphContract o1, WeightGraphContract o2) {
                return toDate(o1.Date).compareTo(toDate(o2.Date));
            }
        });

        return weightGraphDataArrayList_1m;
    }

    public static double getHeighestWeight(List<WeightGraphContract> weightList) {
        double heighestWeight = 0.0;

        for (WeightGraphContract weight : weightList) {
            heighestWeight = weight.WeightKg > heighestWeight ? weight.WeightKg : heighestWeight;
        }
        return heighestWeight;
    }

    public static double getLowestWeight(List<WeightGraphContract> weightList) {
        double lowestWeight = 0.0;

        //do not include zero
        if (weightList.size() > 0) {
            //get lowest with value except 0
            for (WeightGraphContract weight : weightList) {
                if (weight.WeightKg > 0) {
                    lowestWeight = weight.WeightKg;
                    break;
                }
            }

            for (WeightGraphContract weight : weightList) {
                if (weight.WeightKg > 0) {
                    lowestWeight = weight.WeightKg < lowestWeight ? weight.WeightKg : lowestWeight;
                }
            }
        }
        return lowestWeight;
    }

    public static boolean isWeightDataHistory1Year() {

        boolean isTrue = true;

        //get date same year from present
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        List<WeightGraphContract> weightGraphDataArrayList = new ArrayList<WeightGraphContract>();
        weightGraphDataArrayList = ApplicationData.getInstance().weightGraphContractList;

        for (int i = 0; i < weightGraphDataArrayList.size(); i++) {
            Date dateIndex = toDate(weightGraphDataArrayList.get(i).Date);

            cal = Calendar.getInstance();
            cal.setTime(dateIndex);
            int yearIndex = cal.get(Calendar.YEAR);
            int monthIndex = cal.get(Calendar.MONTH);

            if (yearIndex == year - 1) {
                if (monthIndex <= month) {
                    return true;
                } else {
                    isTrue = false;
                }
            } else {
                if (yearIndex == year) {
                    if (monthIndex == 1 && month == 12) {
                        return true;
                    }
                }
                isTrue = false;
            }
        }
        return isTrue;
    }

    public static boolean isWeightDataHistory3MonthsMore() {

        boolean isTrue = true;

        //get date same year from present
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        int month3less = (month - 2) % 12;

        List<WeightGraphContract> weightGraphDataArrayList = new ArrayList<WeightGraphContract>();
        weightGraphDataArrayList = ApplicationData.getInstance().weightGraphContractList;

        for (int i = 0; i < weightGraphDataArrayList.size(); i++) {
            Date dateIndex = toDate(weightGraphDataArrayList.get(i).Date);

            cal = Calendar.getInstance();
            cal.setTime(dateIndex);
            int yearIndex = cal.get(Calendar.YEAR);
            int monthIndex = cal.get(Calendar.YEAR);

            //01 2016
            //11 2015
            //9 2015

            if (yearIndex == year - 1) {
                if (monthIndex >= month3less) {
                    return true;
                }
            } else {
                if (yearIndex == year) {
                    if (monthIndex >= month3less) {
                        return true;
                    }
                }
                isTrue = false;
            }
        }
        return isTrue;
    }

    public static boolean isWeightDataHistory3MonthsLess() {

        boolean isTrue = true;

        //get date same year from present
        java.util.Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        int month3less = (month - 2) % 12;

        List<WeightGraphContract> weightGraphDataArrayList = new ArrayList<WeightGraphContract>();
        weightGraphDataArrayList = ApplicationData.getInstance().weightGraphContractList;

        for (int i = 0; i < weightGraphDataArrayList.size(); i++) {
            Date dateIndex = toDate(weightGraphDataArrayList.get(i).Date);

            cal = Calendar.getInstance();
            cal.setTime(dateIndex);
            int yearIndex = cal.get(Calendar.YEAR);
            int monthIndex = cal.get(Calendar.YEAR);
            if (yearIndex == year - 1) {
                if (monthIndex < month3less) {
                    return true;
                }
            } else {
                if (yearIndex == year) {
                    if (monthIndex < month3less) {
                        return true;
                    }
                }
                isTrue = false;
            }
        }
        return isTrue;
    }

    public static String getWeightDateFormat(Date date) {

        String localTime = "";
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

        /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static String getEditWeightDateFormat(Date date) {

        String localTime = "";
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE);

            sdf.setTimeZone(tz);

            localTime = sdf.format(date);

        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static int getIMCRange(String inputtedIMC) {

//        Maigreur           below 18.5  - 3
//        Bonne santé     18.5 - 25  - 2
//        Surpoids           25 - 30  - 1
//        Obésité            30 - 35 and above  - 0

        float inputtedIMCinFloat = 0;

        if (inputtedIMC != null) {
            //replace comma with period
            inputtedIMC = inputtedIMC.replace(",", ".");
            inputtedIMCinFloat = Float.parseFloat(inputtedIMC);
        }

        if (inputtedIMCinFloat < 18.5) {
            return 3;
        } else if (inputtedIMCinFloat < 25) {
            return 2;
        } else if (inputtedIMCinFloat < 30) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Date toDate(Long timestamplong) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamplong * 1000);
        return calendar.getTime();

    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String convertToFrenchDecimal(float toConvert) {
        String convertedString = String.format("%.2f", toConvert);

        convertedString = convertedString.replace(".", ",");

        return convertedString;
    }

    public static String convertToWholeNumber(float toConvert) {
        String convertedString = String.format("%.0f", toConvert);

        return convertedString;
    }

    public static float convertToEnglishDecimal(String toConvert) {
        toConvert = toConvert.replace(",", ".");

        return Float.parseFloat(toConvert);
    }

    public static int getCurrentWeekNumber(long coachingStartDate, Date endTime) {

        int weekNumber = Weeks.weeksBetween(new DateTime(coachingStartDate*1000),new DateTime()).getWeeks();
        weekNumber = weekNumber + 1;
        if(weekNumber < 0){
            weekNumber = 0;
        }
    /*    Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTimeInMillis(coachingStartDate * 1000);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endTime);

        int weekNumber = endCalendar.get(Calendar.WEEK_OF_YEAR) - startCalendar.get(Calendar.WEEK_OF_YEAR) + 1;

        if (endCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            weekNumber = weekNumber - 1;
        }*/

        return weekNumber;
    }

    public static int getCurrentWeekNumberRepas(long coachingStartDate, Date endTime) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTimeInMillis(coachingStartDate * 1000);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endTime);

        int weekNumber = endCalendar.get(Calendar.WEEK_OF_YEAR) - startCalendar.get(Calendar.WEEK_OF_YEAR) + 1;

        return weekNumber + 2;
    }

    public static int getDaysDiffToCurrent(long coachingStartDate) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTimeInMillis(coachingStartDate * 1000);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(new Date());

        long diff = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis(); //result in millis

        double days = diff / (24 * 60 * 60 * 1000);

        return (int) days;
    }

    public static int getAddDaysToCurrent() {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(new Date());
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(new Date());
        endCalendar.add(Calendar.WEEK_OF_YEAR, 1);
        endCalendar.add(Calendar.DAY_OF_YEAR, Calendar.SATURDAY - startCalendar.get(Calendar.DAY_OF_WEEK));

        long diff = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis(); //result in millis

        double days = diff / (24 * 60 * 60 * 1000);

        System.out.println("getAddDaysToCurrent: " + days + " endCalendar: " + endCalendar.getTime());
        System.out.println("getAddDaysToCurrent: " + days + " startCalendar: " + startCalendar.getTime());

        return (int) days + 1;
    }


    public static int getCurrentDayNumber() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        int dayNumber = calendar.get(Calendar.DAY_OF_WEEK);

        System.out.println("getAddDaysToCurrent: " + calendar.getTime() + " day: " + dayNumber);

        if (dayNumber == 0) {
            dayNumber = 7;
        }
        return dayNumber;
    }

    public static int getDayNumber(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int dayNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        System.out.println("getAddDaysToCurrent: getDayNumber " + calendar.getTime() + " day: " + dayNumber);

        if (dayNumber == 0) {
            dayNumber = 7;
        }
        return dayNumber;
    }


    public static String getRepasDateHeader(Date date, boolean init)
    {
        String stringHeader;

        if (ApplicationData.getInstance().userDataContract.MembershipType == 0 && ApplicationData.getInstance().userDataContract.WeekNumber > 1)
        {
            if (init) {
                stringHeader = " (semaine " + 1 + ")";
                stringHeader = getCurrentDayName(getCurrentDayNumber()) + " " + stringHeader;
            } else {
                stringHeader = " (semaine " + 1 + ")";
                stringHeader = getGivenDayName(date) + " " + stringHeader;
            }
        }
        else
        {
            if (init) {
                stringHeader = " (semaine " + ApplicationData.getInstance().userDataContract.WeekNumber + ")";
                stringHeader = getCurrentDayName(getCurrentDayNumber()) + " " + stringHeader;
            } else {
                stringHeader = " (semaine " + Long.toString(getCurrentWeekNumber(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate), date)) + ")";
                stringHeader = getGivenDayName(date) + " " + stringHeader;
            }
        }

        return stringHeader;
    }

    public static String getRepasDateHeaderWeekDay(int week, int day)
    {
        String stringHeader;
        stringHeader = " (semaine " + week + ")";
        stringHeader = getStringDayName(day) + " " + stringHeader;

        return stringHeader;
    }

    public static String getShoppingListDateHeader(int weekNumber)
    {
        return " Semaine " + weekNumber;
    }

    public static String getCurrentDayName(int i)
    {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.FRANCE);

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());

        return weekDay;
    }

    public static String getStringDayName(int i)
    {
        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.FRANCE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, i + 1);
        weekDay = dayFormat.format(calendar.getTime());

        return weekDay;
    }

    public static String getGivenDayName(Date date) {

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.FRANCE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        weekDay = dayFormat.format(calendar.getTime());

        return weekDay;
    }

    public static String getMealTypeString(int mealTypeIndex, Context context) {

        String mealTypeString = "";
        switch (mealTypeIndex) {
            case 1:
                mealTypeString = context.getString(R.string.mon_compte_plans_classique);
                break;
            case 2:
                mealTypeString = context.getString(R.string.mon_compte_plans_laitages_avec);
                break;
            case 3:
                mealTypeString = context.getString(R.string.mon_compte_plans_laitages_avec);
                break;
            case 4:
                mealTypeString = context.getString(R.string.mon_compte_plans_laitages_sans);
                break;
            case 5:
                mealTypeString = context.getString(R.string.mon_compte_plans_special);
                break;
            case 6:
                mealTypeString = context.getString(R.string.mon_compte_plans_viande_porc);
                break;
            case 7:
                mealTypeString = context.getString(R.string.mon_compte_plans_vegetarien);
                break;
            case 8:
                mealTypeString = context.getString(R.string.mon_compte_plans_gluten);
                break;
            case 9:
                mealTypeString = context.getString(R.string.mon_compte_plans_petit_dejeuner);
                break;
            case 10:
                mealTypeString = context.getString(R.string.mon_compte_plans_low_budget);
                break;
            case 11:
                mealTypeString = context.getString(R.string.mon_compte_plans_laitages_sans);
                break;
            case 12:
                mealTypeString = context.getString(R.string.mon_compte_plans_brunch);
                break;
            case 13:
                mealTypeString = context.getString(R.string.mon_compte_plans_diner);
                break;
            case 14:
                mealTypeString = context.getString(R.string.mon_compte_plans_jeune_cohen);
                break;
            case 15:
                mealTypeString = context.getString(R.string.mon_compte_plans_dejeuner);
                break;
            case 16:
                mealTypeString = context.getString(R.string.mon_compte_plans_plat_prepares);
                break;
            default:
                mealTypeString = context.getString(R.string.mon_compte_plans_classique);
                break;
        }

        return mealTypeString;

    }

    public static int getMealTypeIndex(String mealType, Context context) {
        if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_classique))) {
            return 1;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_laitages_avec))) {
            return 2;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_laitages_avec))) {
            return 3;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_laitages_sans))) {
            return 4;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_special))) {
            return 5;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_viande_porc))) {
            return 6;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_vegetarien))) {
            return 7;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_gluten))) {
            return 8;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_petit_dejeuner))) {
            return 9;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_low_budget))) {
            return 10;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_laitages_sans))) {
            return 11;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_brunch))) {
            return 12;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_diner))) {
            return 13;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_jeune_cohen))) {
            return 14;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_dejeuner))) {
            return 15;
        } else if (mealType.equalsIgnoreCase(context.getString(R.string.mon_compte_plans_plat_prepares))) {
            return 16;
        } else {
            return 1;
        }
    }

    public static int getCoachingIndex(String coaching, Context context) {
        if (coaching.equalsIgnoreCase(context.getString(R.string.mon_compte_coaching_classic_female))) {
            return 1;
        } else if (coaching.equalsIgnoreCase(context.getString(R.string.mon_compte_coaching_menopause))) {
            return 2;
        } else if (coaching.equalsIgnoreCase(context.getString(R.string.mon_compte_coaching_medication))) {
            return 3;
        } else if (coaching.equalsIgnoreCase(context.getString(R.string.mon_compte_coaching_difficult))) {
            return 4;
        } else if (coaching.equalsIgnoreCase(context.getString(R.string.mon_compte_coaching_overwhelmed))) {
            return 5;
        } else if (coaching.equalsIgnoreCase(context.getString(R.string.mon_compte_coaching_moderatemobility))) {
            return 6;
        } else if (coaching.equalsIgnoreCase(context.getString(R.string.mon_compte_coaching_classic_male))) {
            return 7;
        } else {
            return 1;
        }
    }

    public static int getGenderIndex(String gender, Context context) {
        if (gender.equalsIgnoreCase(context.getString(R.string.mon_compte_sexe_fem))) {
            return 0;
        }
        return 1;
    }

    public static String getCalorieType(int calorieType, Context context) {
        String calorieString = "";
        switch (calorieType) {
            case 1:
                calorieString = context.getString(R.string.mon_compte_niveau_calorique_900);
                break;
            case 2:
                calorieString = context.getString(R.string.mon_compte_niveau_calorique_1200);
                break;
            case 3:
                calorieString = context.getString(R.string.mon_compte_niveau_calorique_1400);
                break;
            case 4:
                calorieString = context.getString(R.string.mon_compte_niveau_calorique_1600);
                break;
            case 5:
                calorieString = context.getString(R.string.mon_compte_niveau_calorique_1800);
                break;
            default:
                calorieString = context.getString(R.string.mon_compte_niveau_calorique_900);
        }
        return calorieString;
    }

    public static String getCoaching(int coaching, Context context) {
        String calorieString = "";
        switch (coaching) {
            case 1:
                calorieString = context.getString(R.string.mon_compte_coaching_classic_female);
                break;
            case 2:
                calorieString = context.getString(R.string.mon_compte_coaching_menopause);
                break;
            case 3:
                calorieString = context.getString(R.string.mon_compte_coaching_medication);
                break;
            case 4:
                calorieString = context.getString(R.string.mon_compte_coaching_difficult);
                break;
            case 5:
                calorieString = context.getString(R.string.mon_compte_coaching_overwhelmed);
                break;
            case 6:
                calorieString = context.getString(R.string.mon_compte_coaching_moderatemobility);
                break;
            case 7:
                calorieString = context.getString(R.string.mon_compte_coaching_classic_male);
                break;
            default:
                calorieString = context.getString(R.string.mon_compte_coaching_classic_female);
        }
        return calorieString;
    }

    public static int getCalorieTypeIndex(String calorieTypeString, Context context) {
        if (calorieTypeString.equalsIgnoreCase(context.getString(R.string.mon_compte_niveau_calorique_900))) {
            return 1;
        } else if (calorieTypeString.equalsIgnoreCase(context.getString(R.string.mon_compte_niveau_calorique_1200))) {
            return 2;
        } else if (calorieTypeString.equalsIgnoreCase(context.getString(R.string.mon_compte_niveau_calorique_1400))) {
            return 3;
        } else if (calorieTypeString.equalsIgnoreCase(context.getString(R.string.mon_compte_niveau_calorique_1600))) {
            return 4;
        } else if (calorieTypeString.equalsIgnoreCase(context.getString(R.string.mon_compte_niveau_calorique_1800))) {
            return 5;
        } else {
            return 1;
        }
    }

    public static String getShoppingCategoryString(int index, Context context) {

        String categoryString = "";
        switch (index) {
            case 1:
                categoryString = context.getString(R.string.shopping_list_category_1);
                break;
            case 2:
                categoryString = context.getString(R.string.shopping_list_category_2);
                break;
            case 3:
                categoryString = context.getString(R.string.shopping_list_category_3);
                break;
            case 4:
                categoryString = context.getString(R.string.shopping_list_category_4);
                break;
            case 5:
                categoryString = context.getString(R.string.shopping_list_category_5);
                break;
            case 6:
                categoryString = context.getString(R.string.shopping_list_category_6);
                break;
            case 7:
                categoryString = context.getString(R.string.shopping_list_category_7);
                break;
            case 8:
                categoryString = context.getString(R.string.shopping_list_category_8);
                break;
            case 9:
                categoryString = context.getString(R.string.shopping_list_category_9);
                break;
            case 10:
                categoryString = context.getString(R.string.shopping_list_category_10);
                break;
            case 11:
                categoryString = context.getString(R.string.shopping_list_category_11);
                break;
            default:
                categoryString = context.getString(R.string.shopping_list_category_1);
                break;
        }
        return categoryString;
    }

    public static int getCategoryTypeIndex(String category, Context context) {
        if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_1))) {
            return 1;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_2))) {
            return 2;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_3))) {
            return 3;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_4))) {
            return 4;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_5))) {
            return 5;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_6))) {
            return 6;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_7))) {
            return 7;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_8))) {
            return 8;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_9))) {
            return 9;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_10))) {
            return 10;
        } else if (category.equalsIgnoreCase(context.getString(R.string.shopping_list_category_11))) {
            return 11;
        } else {
            return 1;
        }
    }

    public static String SortableDateTimeNow() {
        String date = null;

        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
        return date;
    }

    public static boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String BuildProfilePicUrl(String format, String profileImageUrl) {

        if (profileImageUrl != null) {
            String proFileImageURL = format.replace("%@", profileImageUrl);
            if (profileImageUrl.endsWith("_")) {
                proFileImageURL = format.replace("%@/", profileImageUrl);
            }
            return proFileImageURL;
        }
        return format.replace("%@/", "male_");
    }

    public static String getMonthDay(Date date)
    {
        String localTime;

        try
        {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");

            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static String getFRMonthDay(Date date)
    {
        String localTime;

        try
        {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.FRENCH);

            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static String getTimeOnly24(long millisec) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisec);
        c.setTimeZone(TimeZone.getDefault());
        //french timezone
        c.add(Calendar.HOUR, -2);

        String temp = ((c.get(Calendar.HOUR) < 10) ? ("0" + c.get(Calendar.HOUR)) : c.get(Calendar.HOUR))
                + ":"
                + ((c.get(Calendar.MINUTE) < 10) ? ("0" + c.get(Calendar.MINUTE)) : c.get(Calendar.MINUTE))
                + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM");

        return temp;
    }

    public static String getTimeOnly(long millisec)
    {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisec);
        c.setTimeZone(TimeZone.getDefault());

        String temp = ((c.get(Calendar.HOUR) < 10) ? ("0" + c.get(Calendar.HOUR)) : c.get(Calendar.HOUR))
                + ":"
                + ((c.get(Calendar.MINUTE) < 10) ? ("0" + c.get(Calendar.MINUTE)) : c.get(Calendar.MINUTE))
                + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM");

        return temp;
    }

    public static long getCurrentDateinLongGMT() {
        Calendar calendar = Calendar.getInstance();
        long unixtime = calendar.getTime().getTime() / 1000L;
        return unixtime;
    }

    public static long getCurrentDateinLong() {
        Calendar calendar = Calendar.getInstance();

        final Date currentTime = new Date();

        final SimpleDateFormat sdf =
                new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        calendar.setTime(currentTime);
        calendar.add(Calendar.HOUR, 8);
        long unixtime = calendar.getTime().getTime() / 1000L;
        return unixtime;
    }

    public static long getCreatedDateForDisplay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp * 1000);
        calendar.add(Calendar.HOUR, 1);
        long unixtime = calendar.getTime().getTime() / 1000L;
        return unixtime;
    }

    /**
     * Native Carnet
     **/
    public static Date updateDate(Date date, int dayOffset) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int DayoftheMonth = c.get(Calendar.DAY_OF_MONTH);
        c.set(Calendar.DAY_OF_MONTH, DayoftheMonth + (dayOffset));
        return c.getTime();
    }

    public static String getMonthDayYear(Date date) {
        String localTime;
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

        /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    /**
     * return 0 if date falls on Sunday
     * //return 1 if date falls on Monday
     * //return 2 if date falls on Tuesday
     * //return 3 if date falls on Wednesday
     * //return 4 if date falls on Thursday
     * //return 5 if date falls on Friday
     * //return 6 if date falls on Saturday
     **/
    public static int getWeekIndexOfDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);

        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }

    public static int getDayDifference(Date startDate, Date endDate) {
        int daysbetween = 0;
        if (startDate != null && endDate != null) {
            Calendar cstartDate = getDatePart(startDate);
            Calendar cendDate = getDatePart(endDate);

            while (cstartDate.before(cendDate)) {
                cstartDate.add(Calendar.DAY_OF_MONTH, 1);
                daysbetween++;

            }
        }
        return daysbetween;
    }

    public static Calendar getDatePart(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);// set this to midnight

        cal.set(Calendar.MINUTE, 0);// set this to 0
        cal.set(Calendar.SECOND, 0);// set this to 0
        cal.set(Calendar.MILLISECOND, 0);// set this to 0
        return cal;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getCurrentDateinDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getCurrentDate(int addDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, addDays);
        return (calendar.getTime());
    }

    public static Date toDateGMT(long timestamp) {

        try {
            Date date = new Date();
            date.setTime(timestamp * 1000);

            DateFormat gmtFormat = new SimpleDateFormat();
            TimeZone gmtTime = TimeZone.getTimeZone("GMT");
            gmtFormat.setTimeZone(gmtTime);

            String gmtDate = gmtFormat.format(date);

            DateFormat gmtFormat2 = new SimpleDateFormat();

            return gmtFormat2.parse(gmtDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Date();
    }

    private static final int BREAKFAST = 1;
    private static final int LUNCH = 2;
    private static final int DINNER = 4;
    private static final int AM_SNACK = 5;
    private static final int PM_SNACK = 6;

    public static String getMealTitle(Context context, int mealtype) {

        switch (mealtype) {
            case BREAKFAST:
                return context.getResources().getString(R.string.MEALTYPE_BREAKFAST);
            case AM_SNACK:
                return context.getResources().getString(R.string.MEALTYPE_MORNINGSNACK);
            case LUNCH:
                return context.getResources().getString(R.string.MEALTYPE_LUNCH);
            case PM_SNACK:
                return context.getResources().getString(R.string.MEALTYPE_AFTERNOONSNACK);
            case DINNER:
                return context.getResources().getString(R.string.MEALTYPE_DINNER);
            default:
                return context.getResources().getString(R.string.MEALTYPE_DINNER);
        }
    }


    /**
     * Used in time display for comments in meals
     **/

    public static String getTimeOnly24Comments(long millisec) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisec);
        c.setTimeZone(TimeZone.getDefault());

        String temp = ((c.get(Calendar.HOUR) < 10) ? ("0" + c
                .get(Calendar.HOUR)) : c.get(Calendar.HOUR))
                + ":"
                + ((c.get(Calendar.MINUTE) < 10) ? ("0" + c
                .get(Calendar.MINUTE)) : c.get(Calendar.MINUTE))
                + " " + (c.get(Calendar.AM_PM) == 0 ? "AM" : "PM");

        return temp;
    }

    public static String getTimeOnly12(long millisec) {
        return getTimeOnly(millisec, true);
    }

    public static String getTimeOnly12() {
        long millisec = new Date().getTime() / 1000;
        return getTimeOnlyMeals(millisec);
    }

    public static String getTimeOnly(long millisec, boolean is24) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisec);
        c.setTimeZone(TimeZone.getDefault());

        if (!is24) {
            Date dt = new Date();
            dt.setTime(millisec * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String temp = sdf.format(dt);

            return temp;
        } else {
            Date dt = new Date();
            dt.setTime(millisec * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String temp = sdf.format(dt);

            return temp;
        }
    }

    public static String getTimeOnlyMeals(long millisec) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millisec);
        c.setTimeZone(TimeZone.getDefault());

        Date dt = new Date();
        dt.setTime(millisec * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String temp = sdf.format(dt);

        return temp;
    }

    public static String getExercise(Context context, Integer exerciseType) {
        String exerciseString = "";

        exerciseString = getExerciseValue(context, Workout.EXERCISE_TYPE.getExerciseType(exerciseType));

        return exerciseString;
    }

    public static String getExerciseValue(Context context, Workout.EXERCISE_TYPE exerciseTypeIndex) {
        String exerciseString = "";

        if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ACTIVITY_IOS) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ACTIVITY);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.OTHER) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_OTHER);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.RUNNING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_RUN);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CYCLING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_BIKE);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.MOUNTAIN_BIKING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_MOUNTAIN_BIKING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.WALKING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_WALK);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.HIKING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_HIKING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.DOWNHILL_SKIING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_DOWNHILL_SKIING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CROSSCOUNTRY_SKIING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_CROSS_COUNTRY_SKIING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SNOWBOARDING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SNOWBOARDING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SKATING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SKATING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SWIMMING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SWIM);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.WHEELCHAIR) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_WHEELCHAIR);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ROWING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ROWING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ELLIPTICAL) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ELLIPTICAL);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.NO_EXERCISE) {
            exerciseString = "";
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.YOGA) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_YOGA);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.PILATES) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_PILATES);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CROSSFIT) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_CROSSFIT);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SPINNING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SPINNING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ZUMBA) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ZUMBA);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.BARRE) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_BARRE);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.GROUP_WORKOUT) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_GROUP_WORKOUT);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.DANCE) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_DANCE);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.BOOTCAMP) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_BOOTCAMP);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.BOXING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_BOXING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.MMA) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_MMA);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.MEDITATION) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_MEDITATION);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.STRENGTH_TRAINING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_STRENGTH_TRAINING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CIRCUIT_TRAINING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_CIRCUIT_TRAINING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.CORE_STRENGTHENING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_CORE_STRENGTHENING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.ARC_TRAINER) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_ARC_TRAINER);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.STAIR_MASTER) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_STAIRMASTER);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.STEPWELL) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_STEPWELL);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.NORDIC_WALKING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_NORDIC_WALKING);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.SPORTS) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_SPORTS);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.WORKOUT) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_WORKOUT);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.AQUAGYM) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_AQUAGYM);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.GOLF) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_GOLF);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.HOUSEWORK) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_HOUSEWORK);
        } else if (exerciseTypeIndex == Workout.EXERCISE_TYPE.GARDENING) {
            exerciseString = context.getResources().getString(R.string.EXERCISE_MENU_GARDENING);
        }

        return exerciseString;
    }

    public static String getDateinString(Date dateRaw) {
        //format: 16 May 2016
        String strDateFormat = "d MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(dateRaw);

    }


    public static int getPhotoResource(int type) {
        //depending on type image dummy meal color changes
        switch (type) {
            case BREAKFAST:
                return R.drawable.meal_default_breakfast;
            case AM_SNACK:
                return R.drawable.meal_default_amsnack;
            case LUNCH:
                return R.drawable.meal_default_lunch;
            case PM_SNACK:
                return R.drawable.meal_default_pmsnack;
            default:
                return R.drawable.meal_default_dinner;
        }
    }

    public static long dateToUnixTimestamp(Date dt) {
        long unixtime = dt.getTime() / 1000L;
        return unixtime;
    }

    public static Hashtable<String, List<MealContract>> getMealsByDateRange(Date todate, Date fromdate, Hashtable<String, MealContract> list, int dayInTheRange) {

        Hashtable<String, List<MealContract>> weeklyMeal = new Hashtable<String, List<MealContract>>();
        String key = "";
        int date_day;

        Calendar c = Calendar.getInstance();
        c.setTime(fromdate);

        int month_day = c.get(Calendar.MONTH) + 1;
        Boolean last_day_reached = false;

        //check if last day of month is reached
        int max_day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        //loop from date to to date range
        for (int i = 0; i < dayInTheRange; i++) {
            date_day = c.get(Calendar.DAY_OF_MONTH);

            if (last_day_reached) {
                month_day = (month_day + 1) % 12;
                last_day_reached = false;
            }

            if (date_day == max_day) {
                last_day_reached = true;
            }
            //use month_daydate as a key 1_10
            key = month_day + "_" + date_day;
            //create dummy data
            List<MealContract> value = new ArrayList<MealContract>();
            weeklyMeal.put(key, value);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);

        }//end for

        Enumeration<MealContract> mealEnum = list.elements();

        while (mealEnum.hasMoreElements()) {
            MealContract meal = mealEnum.nextElement();

            Calendar cal = Calendar.getInstance();
            cal.setTime(AppUtil.toDateGMT(meal.MealCreationDate));

            date_day = cal.get(Calendar.DAY_OF_MONTH);
            month_day = cal.get(Calendar.MONTH) + 1;

            List<MealContract> dummy = weeklyMeal.get(month_day + "_" + date_day);

            if (dummy != null) {
                dummy.add(meal);
                weeklyMeal.remove(month_day + "_" + date_day);
                weeklyMeal.put(month_day + "_" + date_day, dummy);
            }


        }//end while
        //insert dummy meals
        for (Object dateKey : weeklyMeal.keySet().toArray()) {
            List<MealContract> arr = weeklyMeal.get("" + dateKey);
            int addNullCount = (arr.size());
            for (int ctr = 0; ctr < (5 - addNullCount); ctr++) {
                arr.add(null);
            }
            weeklyMeal.remove("" + dateKey);
            weeklyMeal.put("" + dateKey, arr);
        }
        return weeklyMeal;
    }

    public static Hashtable<String, List<MoodContract>> getMoodByDateRange(Date todate, Date fromdate, Hashtable<String, MoodContract> list, int dayInTheRange) {
        Hashtable<String, List<MoodContract>> weeklyMood = new Hashtable<String, List<MoodContract>>();
        String key = "";

        int date_day;

        Calendar c = Calendar.getInstance();
        c.setTime(fromdate);

        int month_day = c.get(Calendar.MONTH) + 1;
        Boolean last_day_reached = false;
        //check if last day of month is reached
        int max_day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        //loop from date to to date range
        for (int i = 0; i < dayInTheRange; i++) {
            date_day = c.get(Calendar.DAY_OF_MONTH);
            if (last_day_reached) {
                month_day = month_day + 1;
                if(month_day > 12)
                {
                    month_day = 1;
                }
                last_day_reached = false;
            }
            if (date_day == max_day) {
                last_day_reached = true;
            }
            //use month_daydate as a key 1_10
            key = month_day + "_" + date_day;

            //create dummy data
            List<MoodContract> value = new ArrayList<MoodContract>();
            weeklyMood.put(key, value);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);

        }//end for
        Enumeration<MoodContract> moodEnumeration = list.elements();

        while (moodEnumeration.hasMoreElements()) {
            MoodContract mood = moodEnumeration.nextElement();

            //get date_day of the meal
            c.setTime(AppUtil.toDate(mood.CreationDate));

            date_day = c.get(Calendar.DAY_OF_MONTH);
            month_day = c.get(Calendar.MONTH) + 1;

            List<MoodContract> dummy = weeklyMood.get(month_day + "_" + date_day);

            if (dummy != null) {
                dummy.add(mood);
                weeklyMood.remove(month_day + "_" + date_day);
                weeklyMood.put(month_day + "_" + date_day, dummy);
            }
        }//end while

        //end for
        return weeklyMood;

    }

    public static Hashtable<String, List<ExerciseContract>> getWorkOutByDateRange(Date todate, Date fromdate, Hashtable<String, ExerciseContract> list, int dayInTheRange) {

        Hashtable<String, List<ExerciseContract>> weeklyWorkOut = new Hashtable<String, List<ExerciseContract>>();
        String key = "";
        int date_day;

        Calendar c = Calendar.getInstance();
        c.setTime(fromdate);

        int month_day = c.get(Calendar.MONTH) + 1;
        Boolean last_day_reached = false;

        //check if last day of month is reached
        int max_day = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        //loop from date to to date range
        for (int i = 0; i < dayInTheRange; i++) {
            date_day = c.get(Calendar.DAY_OF_MONTH);

            if (last_day_reached) {
                month_day = (month_day + 1) % 12;
                last_day_reached = false;
            }

            if (date_day == max_day) {
                last_day_reached = true;
            }
            //use month_daydate as a key 1_10
            key = month_day + "_" + date_day;
            //create dummy data
            List<ExerciseContract> value = new ArrayList<ExerciseContract>();
            weeklyWorkOut.put(key, value);
            c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + 1);

        }//end for

        Enumeration<ExerciseContract> workOutEnum = list.elements();

        while (workOutEnum.hasMoreElements()) {
            ExerciseContract workOut = workOutEnum.nextElement();

            Calendar cal = Calendar.getInstance();
            cal.setTime(AppUtil.toDateGMT(workOut.CreationDate));

            date_day = cal.get(Calendar.DAY_OF_MONTH);
            month_day = cal.get(Calendar.MONTH) + 1;

            List<ExerciseContract> dummy = weeklyWorkOut.get(month_day + "_" + date_day);

            if (dummy != null) {
                dummy.add(workOut);
                weeklyWorkOut.remove(month_day + "_" + date_day);
                weeklyWorkOut.put(month_day + "_" + date_day, dummy);
            }


        }//end while


        return weeklyWorkOut;
    }

    public static Hashtable<String, Integer> getMealCountPerWeek(Hashtable<String, List<MealContract>> weeklyMeal) {

        Hashtable<String, Integer> mealCount = new Hashtable<String, Integer>();

        for (Object dateKey : weeklyMeal.keySet().toArray()) {

            List<MealContract> arr = weeklyMeal.get((String) dateKey);

            int count = 0;
            for (MealContract meal : arr) {

                if (meal != null && meal.MealId > 0) {
                    count++;
                }
                if (count > 5) //max count of meal is just 5 meals
                    count = 5;
            }
            mealCount.put("" + dateKey, count);
        }

        return mealCount;
    }

    public static Hashtable<String, Integer> getCommentCountPerWeek(Hashtable<String, List<MealContract>> weeklyMeal) {

        Hashtable<String, Integer> mealCount = new Hashtable<String, Integer>();

        for (Object dateKey : weeklyMeal.keySet().toArray()) {

            List<MealContract> arr = weeklyMeal.get((String) dateKey);

            int count = 0;

            for (MealContract meal : arr) {

                if (meal != null && meal.Comments.Count > 0) {
                    for (MealCommentContract comment: meal.Comments.Comments){
                        try {
                            if (comment.Coach.CoachProfileId != null) {
                                count++;
                                break;
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

                    }
//                    count++;
                }
                if (count > 5) //max count of meal is just 5 meals
                    count = 5;
            }
            mealCount.put("" + dateKey, count);

        }

        return mealCount;
    }

    public static Date stringToDateWeight(String dateString) {
        return stringToDateFormat(dateString, "yyyy-MM-dd");
    }

    public static Date stringToDateFormat(String str, String dateformat) {
        if (str != null && str.length() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateformat, Locale.getDefault());

            Date d;
            try {
                d = sdf.parse(str);

            } catch (ParseException e) {
                return null;
            }
            return d;
        } else {
            return null;
        }
    }
    public static String getMonthonDate(Date date) {
        //Ex: Nov 17, 2014 returns 17
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return "" + (c.get(Calendar.MONTH) + 1);
    }


    public static String getDateStringGetSync(Date date) {
        String localTime;
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            sdf.setTimeZone(tz);
            localTime = sdf.format(date);
        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static String getDateinStringWeight(Date dateRaw)
    {
        String strDateFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
        return sdf.format(dateRaw);
    }

    public static String getMealTip(Context context, int dayIndex, int mealtype) {
        if (dayIndex == 0) { //sunday
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SUNDAYDINNER);
            }
        } else if (dayIndex == 1) { //Monday
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_MONDAYDINNER);
            }
        } else if (dayIndex == 2) { //TUesday
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_TUESDAYDINNER);
            }
        } else if (dayIndex == 3) { //Wen
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_WEDNESDAYDINNER);
            }
        } else if (dayIndex == 4) { //THurs
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_THURSDAYDINNER);
            }
        } else if (dayIndex == 5) { //Fri
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_FRIDAYDINNER);
            }
        } else if (dayIndex == 6) { //Sat
            switch (mealtype) {
                case BREAKFAST:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYBREAKFAST);
                case AM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYMORNINGSNACK);
                case LUNCH:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYLUNCH);
                case PM_SNACK:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYAFTERNOONSNACK);
                case DINNER:
                    return context.getResources().getString(R.string.ONETOONECOACHING_TIPS_SATURDAYDINNER);
            }
        }
        return "";
    }

    public static String getDayonDate(Date date) {
        //Ex: Nov 17, 2014 returns 17
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return ("" + c.get(Calendar.DAY_OF_MONTH));
    }

    public static long dateToTimestamp(Date dt) {
        long unixtime = dateToLongFormat(dt);

        return unixtime;
    }

    public static long dateToLongFormat(Date date) {
        long returnValue;

        Date d = date;

        Calendar c = Calendar.getInstance();
        c.setTime(d);

        TimeZone z = c.getTimeZone();
        int offset = z.getRawOffset();

        if (z.inDaylightTime(new Date())) {
            offset = offset + z.getDSTSavings();
        }
        int offsetHrs = offset / 1000 / 60 / 60;
        int offsetMins = offset / 1000 / 60 % 60;

        c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) + (offsetHrs));
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + (offsetMins));

        returnValue = c.getTimeInMillis() / 1000;

        return returnValue;

    }

    public static Date formatDate(TimePicker timepicker, int month, int day, int year) {

        System.out.println("AppUtil formatDate: " + day);
        String time = formatDate(timepicker);
        time = year + "-" + month + "-" + day + " " + time;

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return date;
    }

    public static String formatDate(TimePicker timepicker) {
        String str;
        String time;
        if (timepicker.getCurrentHour() < 11) { // Am
            time = ((timepicker.getCurrentHour() < 10) ? ("0" + timepicker
                    .getCurrentHour()) : timepicker.getCurrentHour())
                    + ":"
                    + ((timepicker.getCurrentMinute() < 10) ? ("0" + timepicker
                    .getCurrentMinute()) : timepicker
                    .getCurrentMinute());
            // + "am";
        } else { // pm
            time = timepicker.getCurrentHour()
                    + ":"
                    + ((timepicker.getCurrentMinute() < 10) ? ("0" + timepicker
                    .getCurrentMinute()) : timepicker
                    .getCurrentMinute());
            // + "pm";

        }
        str = time;
        return str;
    }

    public static int getIntYearonDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.YEAR);
    }

    public static int getIntMonthonDate(Date date) {
        //Ex: Nov 17, 2014 returns 17
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.MONTH) + 1;
    }

    public static int getIntDayonDate(Date date) {
        //Ex: Nov 17, 2014 returns 17
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        return c.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHour() {

        Calendar calendar = Calendar.getInstance();
        // Add one minute to current date time
        calendar.setTime(new Date());
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute() {

        Calendar calendar = Calendar.getInstance();
        // Add one minute to current date time
        calendar.setTime(new Date());
        return calendar.get(Calendar.MINUTE);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }
    public static long getDateInLongUtc(Calendar calendar, String time) {
        calendar.setTimeZone(TimeZone.getDefault());
        String DATE_FORMAT = "dd-M-yyyy hh:mm:ss a";
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        formatter.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat sdfUtc = new SimpleDateFormat(DATE_FORMAT);
        TimeZone tzInUtc = TimeZone.getTimeZone("UTC");
        sdfUtc.setTimeZone(tzInUtc);

        String sDateInUtc = sdfUtc.format(calendar.getTime()); // Convert to String first
        try {
            Date dateInUtc = formatter.parse(sDateInUtc); // Create a new Date object
            return dateInUtc.getTime() / 1000L;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Long.parseLong(null);
    }

    public static long getDateInLong(Calendar calendar, String time) {
        calendar.setTimeZone(TimeZone.getDefault());

        long unixtime = calendar.getTime().getTime() / 1000L;

//        System.out.println("AppUtil time: " + time);
//        System.out.println("AppUtil CurrentDateinTime: " + unixtime);

        return unixtime;
    }

    public static String getDateFormatNotifications(Date date)
    {
        String localTime;
        try {

            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

            /* date formatter in local timezone */
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRENCH);
            sdf.setTimeZone(tz);
            localTime = sdf.format(date);

        } catch (NullPointerException e) {
            return "";
        }

        return localTime;
    }

    public static String CheckImageUrl(String img)
    {
        if (img != null && !img.isEmpty() && img.toLowerCase().contains("http://img.aujourdhui.com/"))
        {
            return img.replace("http://img.aujourdhui.com/", WebkitURL.domainURL + "/img_aj/");
        }
        if (img != null && !img.isEmpty() && img.toLowerCase().contains("http://photos.aujourdhui.com/"))
        {
            return img.replace("http://photos.aujourdhui.com/", WebkitURL.domainURL +"/img1_aj/");
        }
        if (img != null && !img.isEmpty() && img.toLowerCase().contains("http://img1.aujourdhui.com/"))
        {
            return img.replace("http://img1.aujourdhui.com/", WebkitURL.domainURL + "/img1_aj/");
        }

        return img;


    }
}
