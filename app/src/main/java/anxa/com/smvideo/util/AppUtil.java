package anxa.com.smvideo.util;

import android.content.Context;
import android.os.Build;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.contracts.WeightGraphContract;

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
        result.append(context.getString(R.string.app_name));
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
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

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
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTimeInMillis(coachingStartDate * 1000);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endTime);

        int weekNumber = endCalendar.get(Calendar.WEEK_OF_YEAR) - startCalendar.get(Calendar.WEEK_OF_YEAR) + 1;

        if (endCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            weekNumber = weekNumber - 1;
        }

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


    public static String getRepasDateHeader(Date date, boolean init) {
        String stringHeader;
        if (init) {
            stringHeader = " (semaine " + Long.toString(getCurrentWeekNumber(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate), new Date())) + ")";
            stringHeader = getCurrentDayName(getCurrentDayNumber()) + " " + stringHeader;

        } else {
            stringHeader = " (semaine " + Long.toString(getCurrentWeekNumber(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate), date)) + ")";
            stringHeader = getGivenDayName(date) + " " + stringHeader;
        }

        return stringHeader;
    }

    public static String getRepasDateHeaderWeekDay(int week, int day) {
        String stringHeader;
        stringHeader = " (semaine " + week + ")";
        stringHeader = getStringDayName(day) + " " + stringHeader;

        return stringHeader;
    }

    public static String getShoppingListDateHeader(Date date, boolean init) {
        String stringHeader;
        if (init) {
            stringHeader = " Semaine " + Long.toString(getCurrentWeekNumber(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate), new Date()));
        } else {
            stringHeader = " Semaine " + Long.toString(getCurrentWeekNumber(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate), date));
        }

        return stringHeader;
    }

    public static String getCurrentDayName(int i) {

        String weekDay;
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.FRANCE);

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());

        return weekDay;
    }

    public static String getStringDayName(int i) {

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

}
