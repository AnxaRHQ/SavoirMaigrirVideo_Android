package anxa.com.smvideo.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.contracts.Graph.StepDataContract;

/**
 * Created by elaineanxa on 18/07/2019
 */
public class StepsUtil
{
    public static String format(double number, int iteration)
    {
        char[] c = new char[]{'k', 'm', 'b', 't'};

        double d = ((long) number / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : format(d, iteration+1));
    }

    public static List<StepDataContract> get1MStepsList(boolean initDate, int dateIndex)
    {
        double totalSteps = 0.0;
        double totalCalories = 0.0;
        double totalKmTravelled = 0.0;

        ArrayList<StepDataContract> stepsGraphDataArrayList_1m = new ArrayList<StepDataContract>();
        List<StepDataContract> stepsGraphDataArrayList = ApplicationData.getInstance().stepsList;
        Hashtable<Date, StepDataContract> stepsDate = new Hashtable<Date, StepDataContract>();
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

        StepDataContract dummySteps = new StepDataContract();
        dummySteps.StepDate = AppUtil.getDateinStringWeight(cal.getTime());
        dummySteps.Steps = 0;

        stepsDate.put(cal.getTime(), dummySteps);

        for (int i = 1; i < 31; i++)
        {
            calValid.add(Calendar.DAY_OF_MONTH, -1);
            dateList.add(calValid.getTime());

            dummySteps = new StepDataContract();
            dummySteps.StepDate = AppUtil.getDateinStringWeight(calValid.getTime());
            dummySteps.Steps = 0;

            stepsDate.put(calValid.getTime(), dummySteps);
        }

        Collections.sort(dateList, new Comparator<Date>()
        {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (StepDataContract steps : stepsGraphDataArrayList)
        {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(AppUtil.stringToDateWeight(steps.StepDate));
            int dayIndex = calIndex.get(Calendar.DAY_OF_MONTH);

            if (AppUtil.stringToDateWeight(steps.StepDate).before(cal.getTime()) && AppUtil.stringToDateWeight(steps.StepDate).after((Date) dateList.get(0)))
            {
                totalSteps = totalSteps + steps.Steps;
                totalCalories = totalCalories + steps.Calories;
                totalKmTravelled = totalKmTravelled + steps.Distance;

                for (Date date_list : dateList)
                {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (dayIndex == calIndex_date.get(Calendar.DAY_OF_MONTH)) {
                        stepsDate.put(date_list, steps);
                    }
                }
            }
        }

        for (Date date_list : dateList)
        {
            stepsGraphDataArrayList_1m.add(stepsDate.get(date_list));
        }

        Collections.sort(stepsGraphDataArrayList_1m, new Comparator<StepDataContract>()
        {
            public int compare(StepDataContract o1, StepDataContract o2) {
                return AppUtil.stringToDateWeight(o1.StepDate).compareTo(AppUtil.stringToDateWeight(o2.StepDate));
            }
        });

        ApplicationData.getInstance().currentTotalSteps = totalSteps;
        ApplicationData.getInstance().currentKmTravelled = totalKmTravelled/1000;
        ApplicationData.getInstance().currentTotalCalories = totalCalories;

        return stepsGraphDataArrayList_1m;
    }

    public static List<StepDataContract> getStepsLogsList()
    {
        List<StepDataContract> stepsGraphDataArrayList = new ArrayList<StepDataContract>();
        stepsGraphDataArrayList = ApplicationData.getInstance().stepsList;

        System.out.println("getSteplsLogsList ApplicationData : " + ApplicationData.getInstance().stepsList.size());
        System.out.println("getSteplsLogsList: " + stepsGraphDataArrayList.size());

        Collections.sort(stepsGraphDataArrayList, new Comparator<StepDataContract>()
        {
            public int compare(StepDataContract o1, StepDataContract o2) {
                return AppUtil.stringToDateWeight(o2.StepDate).compareTo(AppUtil.stringToDateWeight(o1.StepDate));
            }
        });

        return stepsGraphDataArrayList;
    }

    /**
     * @return list of Steps data in a year
     * must be 12 items or less
     * return the latest weight recorded in the month
     **/
    public static List<StepDataContract> get1YStepsList(boolean initDate, int dateRangeIndex)
    {
        double totalSteps = 0.0;
        double totalCalories = 0.0;
        double totalKmTravelled = 0.0;

        List<StepDataContract> stepsGraphDataArrayList = ApplicationData.getInstance().stepsList;
        Hashtable<Date, StepDataContract> stepsDate = new Hashtable<Date, StepDataContract>();
        ArrayList<Date> dateList = dateList = new ArrayList<>(12);
        Hashtable<Date, Float> stepsTotalPerMonth = new Hashtable<Date, Float>();

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

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);

        calValid.set(Calendar.HOUR_OF_DAY, 0);
        calValid.set(Calendar.MINUTE, 0);
        calValid.set(Calendar.SECOND, 0);
        calValid.set(Calendar.MILLISECOND, 0);

        dateList.add(cal.getTime());

        //include current date;
        StepDataContract dummySteps = new StepDataContract();
        dummySteps.StepDate = AppUtil.getDateinStringWeight(cal.getTime());
        dummySteps.Steps = 0;
        stepsDate.put(cal.getTime(), dummySteps);
        stepsTotalPerMonth.put(cal.getTime(), 0f);

        for (int i = 1; i < 12; i++)
        {
            calValid.add(Calendar.MONTH, -1);
            dateList.add(calValid.getTime());

            dummySteps = new StepDataContract();
            dummySteps.StepDate = AppUtil.getDateinStringWeight(calValid.getTime());
            dummySteps.Steps = 0;
            stepsDate.put(calValid.getTime(), dummySteps);
            stepsTotalPerMonth.put(calValid.getTime(), 0f);
        }

        Collections.sort(dateList, new Comparator<Date>()
        {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (StepDataContract steps : stepsGraphDataArrayList)
        {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(AppUtil.stringToDateWeight(steps.StepDate));
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (AppUtil.stringToDateWeight(steps.StepDate).before(cal.getTime()) && AppUtil.stringToDateWeight(steps.StepDate).after((Date) dateList.get(0))) {
                totalSteps = totalSteps + steps.Steps;
                totalCalories = totalCalories + steps.Calories;
                totalKmTravelled = totalKmTravelled + steps.Distance;

                for (Date date_list : dateList) {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);
                    if (monthIndex == calIndex_date.get(Calendar.MONTH)) {
                        stepsTotalPerMonth.put(date_list, stepsTotalPerMonth.get(date_list) + steps.Steps);
                        stepsDate.put(date_list, steps);
                    }
                }
            }
        }

        ArrayList<StepDataContract> stepsGraphDataArrayList_1y = new ArrayList<StepDataContract>();

        for (Date date_list : dateList)
        {
            dummySteps = new StepDataContract();
            dummySteps.StepDate = AppUtil.getDateinStringWeight(date_list);
            dummySteps.Steps = Math.round(stepsTotalPerMonth.get(date_list));
            stepsGraphDataArrayList_1y.add(dummySteps);
        }

        ApplicationData.getInstance().currentTotalSteps = totalSteps;
        ApplicationData.getInstance().currentKmTravelled = totalKmTravelled/1000;
        ApplicationData.getInstance().currentTotalCalories = totalCalories;

        return stepsGraphDataArrayList_1y;
    }

    public static List<StepDataContract> get3MStepsList(boolean initDate)
    {
        double totalSteps = 0.0;
        double totalCalories = 0.0;
        double totalKmTravelled = 0.0;

        List<StepDataContract> stepsGraphDataArrayList = ApplicationData.getInstance().stepsList;
        ArrayList<StepDataContract> stepsGraphDataArrayList_3m = new ArrayList<StepDataContract>();

        Hashtable<Date, StepDataContract> stepsDate = new Hashtable<Date, StepDataContract>();
        Hashtable<Date, Float> stepsTotalPerMonth = new Hashtable<Date, Float>();
        ArrayList<Date> dateList = new ArrayList<>(3);

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        Collections.sort(stepsGraphDataArrayList, new Comparator<StepDataContract>()
        {
            public int compare(StepDataContract o1, StepDataContract o2) {
                return AppUtil.stringToDateWeight(o1.StepDate).compareTo(AppUtil.stringToDateWeight(o2.StepDate));
            }
        });

        if (initDate) {
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            calValid.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date2);
            cal.set(Calendar.DAY_OF_MONTH, 1);// This is necessary to get proper results
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

            calValid.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date2);
            calValid.set(Calendar.DAY_OF_MONTH, 1);
        }

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);

        calValid.set(Calendar.HOUR_OF_DAY, 0);
        calValid.set(Calendar.MINUTE, 0);
        calValid.set(Calendar.SECOND, 0);
        calValid.set(Calendar.MILLISECOND, 0);

        //include current date;
        dateList.add(cal.getTime());
        StepDataContract dummySteps = new StepDataContract();
        dummySteps.StepDate = AppUtil.getDateinStringWeight(cal.getTime());
        dummySteps.Steps = 0;
        stepsDate.put(cal.getTime(), dummySteps);
        stepsTotalPerMonth.put(cal.getTime(), 0f);

        for (int i = 1; i < 3; i++)
        {
            calValid.add(Calendar.MONTH, -1);
            dateList.add(calValid.getTime());

            dummySteps = new StepDataContract();
            dummySteps.StepDate = AppUtil.getDateinStringWeight(calValid.getTime());
            dummySteps.Steps = 0;
            stepsDate.put(calValid.getTime(), dummySteps);
            stepsTotalPerMonth.put(calValid.getTime(), 0f);
        }

        Collections.sort(dateList, new Comparator<Date>()
        {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        for (StepDataContract steps : stepsGraphDataArrayList)
        {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(AppUtil.stringToDateWeight(steps.StepDate));
            int monthIndex = calIndex.get(Calendar.MONTH);

            if (AppUtil.stringToDateWeight(steps.StepDate).before(cal.getTime()) && (AppUtil.stringToDateWeight(steps.StepDate).after((Date) dateList.get(0)) || (AppUtil.stringToDateWeight(steps.StepDate).compareTo((Date) dateList.get(0))==0)))
            {
                System.out.println("steps3m IF: " + AppUtil.stringToDateWeight(steps.StepDate) + "entry date: " + cal.getTime() + "dateList.get0: " + dateList.get(0));

                totalSteps = totalSteps + steps.Steps;
                totalCalories = totalCalories + steps.Calories;
                totalKmTravelled = totalKmTravelled + steps.Distance;

                for (Date date_list : dateList)
                {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);

                    if (monthIndex == calIndex_date.get(Calendar.MONTH))
                    {
                        stepsTotalPerMonth.put(date_list, stepsTotalPerMonth.get(date_list) + steps.Steps);
                        stepsDate.put(date_list, steps);
                    }
                }
            }
        }

        for (Date date_list : dateList)
        {
            dummySteps = new StepDataContract();
            dummySteps.StepDate = AppUtil.getDateinStringWeight(date_list);
            dummySteps.Steps= Math.round(stepsTotalPerMonth.get(date_list));

            stepsGraphDataArrayList_3m.add(dummySteps);
        }

        ApplicationData.getInstance().currentTotalSteps = totalSteps;
        ApplicationData.getInstance().currentKmTravelled = totalKmTravelled/1000;
        ApplicationData.getInstance().currentTotalCalories = totalCalories;

        System.out.println("steps3m: " + dateList);
        System.out.println("steps3m data: " + stepsGraphDataArrayList_3m);

        return stepsGraphDataArrayList_3m;
    }

    public static List<StepDataContract> get1WStepsList(boolean initDate)
    {
        List<StepDataContract> stepsGraphDataArrayList = ApplicationData.getInstance().stepsList;
        ArrayList<StepDataContract> stepsGraphDataArrayList_1w = new ArrayList<>();

        Hashtable<Date, StepDataContract> stepsDate = new Hashtable<>();
        ArrayList<Date> dateList = new ArrayList<>(7);

        Calendar cal = Calendar.getInstance();
        Calendar calValid = Calendar.getInstance();

        Collections.sort(stepsGraphDataArrayList, new Comparator<StepDataContract>()
        {
            public int compare(StepDataContract o1, StepDataContract o2) {
                return AppUtil.stringToDateWeight(o1.StepDate).compareTo(AppUtil.stringToDateWeight(o2.StepDate));
            }
        });

        if (initDate) {
            cal.setTime(new Date());
            calValid.setTime(cal.getTime());
        } else {
            cal.setTime(ApplicationData.getInstance().currentDateRangeDisplay_date2);
            calValid.setTime(cal.getTime());
        }

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);

        calValid.set(Calendar.HOUR_OF_DAY, 0);
        calValid.set(Calendar.MINUTE, 0);
        calValid.set(Calendar.SECOND, 0);
        calValid.set(Calendar.MILLISECOND, 0);

        //include current date;
        dateList.add(cal.getTime());

        StepDataContract dummySteps = new StepDataContract();
        dummySteps.StepDate = AppUtil.getDateinStringWeight(cal.getTime());
        dummySteps.Steps = 0;
        stepsDate.put(cal.getTime(), dummySteps);

        for (int i = 1; i < 7; i++)
        {
            calValid.add(Calendar.DAY_OF_YEAR, -1);
            dateList.add(calValid.getTime());

            dummySteps = new StepDataContract();
            dummySteps.StepDate = AppUtil.getDateinStringWeight(calValid.getTime());
            dummySteps.Steps = 0;
            stepsDate.put(calValid.getTime(), dummySteps);
        }

        Collections.sort(dateList, new Comparator<Date>()
        {
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        for (StepDataContract steps : stepsGraphDataArrayList)
        {
            Calendar calIndex = Calendar.getInstance();
            calIndex.setTime(AppUtil.stringToDateWeight(steps.StepDate));
            int dayIndex = calIndex.get(Calendar.DAY_OF_YEAR);

            if (AppUtil.stringToDateWeight(steps.StepDate).before(cal.getTime()) && (AppUtil.stringToDateWeight(steps.StepDate).after((Date) dateList.get(0)) || (AppUtil.stringToDateWeight(steps.StepDate).compareTo((Date) dateList.get(0))==0)))
            {
                for (Date date_list : dateList)
                {
                    Calendar calIndex_date = Calendar.getInstance();
                    calIndex_date.setTime(date_list);

                    if (dayIndex == calIndex_date.get(Calendar.DAY_OF_YEAR))
                    {
                        stepsDate.put(date_list, steps);
                    }
                }
            }
        }

        for (Date date_list : dateList)
        {
            stepsGraphDataArrayList_1w.add(stepsDate.get(date_list));
        }

        return stepsGraphDataArrayList_1w;
    }

    public static double getTotalSteps(List<StepDataContract> stepsList)
    {
        double stepstTotal = 0.0;

        for (StepDataContract steps : stepsList)
        {
            stepstTotal = stepstTotal + steps.Steps;
        }

        return stepstTotal;
    }

    public static double getTotalKmTravelled(List<StepDataContract> stepsList)
    {
        double kmTravelled = 0.0;

        for (StepDataContract steps : stepsList)
        {
            kmTravelled = kmTravelled + steps.Distance;
        }

        return kmTravelled/1000.0;
    }

    public static double getTotalCalories(List<StepDataContract> stepsList)
    {

        double totalCalories = 0.0;

        for (StepDataContract steps : stepsList)
        {
            totalCalories = totalCalories + steps.Calories;
        }

        return totalCalories;
    }

    public static double getHeighestSteps(List<StepDataContract> stepsList)
    {
        double heighestSteps = 0.0;

        for (StepDataContract steps : stepsList)
        {
            double steps_count = steps.Steps;

            heighestSteps = steps_count > heighestSteps ? steps_count : heighestSteps;
        }

        return heighestSteps;
    }

    public static double getLowestSteps(List<StepDataContract> stepsList)
    {
        double lowestSteps = 0.0;

        //do not include zero

        if (stepsList.size() > 0)
        {
            //get lowest with value except 0
            for (StepDataContract steps : stepsList)
            {
                double steps_count = steps.Steps;

                if (steps_count > 0) {
                    lowestSteps = steps_count;
                    break;
                }
            }

            for (StepDataContract steps : stepsList)
            {
                double steps_count = steps.Steps;

                if (steps_count > 0) {
                    lowestSteps = steps_count < lowestSteps ? steps_count : lowestSteps;
                }
            }
        }

        return lowestSteps;
    }
}
