package anxa.com.smvideo.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.listener.DateChangeListener;
import anxa.com.smvideo.util.AppUtil;


public class DatePagerLayout extends HorizontalScrollView implements OnClickListener {
    private static final int SWIPE_MIN_DISTANCE = 20;
    private static final int SWIPE_THRESHOLD_VELOCITY = 5;
    private static final int MAX_PAGE = 1;

    private static final int SCROLL_TO_LEFT = 99;
    private static final int SCROLL_TO_RIGHT = 98;
    private static int scrollType = 0;
    private float X1_MOVE = 0;
    private float X2_UP = 0;
    private boolean iStartSwipe = false;


    private GestureDetector mGestureDetector;

    private int mActiveFeature = 1;
    LinearLayout ll1;

    Context context;
    OnClickListener listener;
    RelativeLayout day1_rl;
    RelativeLayout day2_rl;
    RelativeLayout day3_rl;
    RelativeLayout day4_rl;
    RelativeLayout day5_rl;
    RelativeLayout day6_rl;
    RelativeLayout day7_rl;
    RelativeLayout leftArrow_rl;
    RelativeLayout rightArrow_rl;

    TextView date_day1;
    TextView date_day2;
    TextView date_day3;
    TextView date_day4;
    TextView date_day5;
    TextView date_day6;
    TextView date_day7;

    TextView mealCount_day1;
    TextView mealCount_day2;
    TextView mealCount_day3;
    TextView mealCount_day4;
    TextView mealCount_day5;
    TextView mealCount_day6;
    TextView mealCount_day7;

    TextView commentCount_day1;
    TextView commentCount_day2;
    TextView commentCount_day3;
    TextView commentCount_day4;
    TextView commentCount_day5;
    TextView commentCount_day6;
    TextView commentCount_day7;

    ImageView mealImage_day1;
    ImageView mealImage_day2;
    ImageView mealImage_day3;
    ImageView mealImage_day4;
    ImageView mealImage_day5;
    ImageView mealImage_day6;
    ImageView mealImage_day7;

    ImageView commentImage_day1;
    ImageView commentImage_day2;
    ImageView commentImage_day3;
    ImageView commentImage_day4;
    ImageView commentImage_day5;
    ImageView commentImage_day6;
    ImageView commentImage_day7;

    HorizontalScrollView horizscroolView;
    TextView dateline_tv;


    LinearLayout internalWrapper;

    Hashtable<String, Integer> mealCountPerWeek = new Hashtable<String, Integer>();
    Hashtable<String, Integer> commentCountPerWeek = new Hashtable<String, Integer>();
    Hashtable<String, String> dateTable = new Hashtable<String, String>();

    Date currentSelectedDate;
    Date todaysDate;
    Date weekStart;//(sunday)
    Date weekEnd;//(Sat or depending on todays' Date)

    int currentSelectedDateIndex = 0;
    int previousSelectedDateIndex = 0;
    int totalDaysindex = 1;//use the indicate future dates

    DateChangeListener DateListener;


    public void setDateListener(DateChangeListener DateListener) {
        this.DateListener = DateListener;
    }

    @SuppressLint("NewApi")
    public DatePagerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public DatePagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public DatePagerLayout(Context context) {
        super(context);
        this.context = context;
    }

    private void initViews(LinearLayout ll) {

        day1_rl = null;
        day1_rl = (RelativeLayout) ll.findViewById(R.id.day1_container);
        day2_rl = (RelativeLayout) ll.findViewById(R.id.day2_container);
        day3_rl = (RelativeLayout) ll.findViewById(R.id.day3_container);
        day4_rl = (RelativeLayout) ll.findViewById(R.id.day4_container);
        day5_rl = (RelativeLayout) ll.findViewById(R.id.day5_container);
        day6_rl = (RelativeLayout) ll.findViewById(R.id.day6_container);
        day7_rl = (RelativeLayout) ll.findViewById(R.id.day7_container);

        leftArrow_rl = (RelativeLayout) ll.findViewById(R.id.left_arrow_container);
        rightArrow_rl = (RelativeLayout) ll.findViewById(R.id.right_arrow_container);

        day1_rl.setOnClickListener(this);
        day1_rl.setTag(0);

        day2_rl.setOnClickListener(this);
        day2_rl.setTag(1);

        day3_rl.setOnClickListener(this);
        day3_rl.setTag(2);

        day4_rl.setOnClickListener(this);
        day4_rl.setTag(3);

        day5_rl.setOnClickListener(this);
        day5_rl.setTag(4);

        day6_rl.setOnClickListener(this);
        day6_rl.setTag(5);

        day7_rl.setOnClickListener(this);
        day7_rl.setTag(6);

        leftArrow_rl.setOnClickListener(this);
        leftArrow_rl.setTag(77);

        rightArrow_rl.setOnClickListener(this);
        rightArrow_rl.setTag(88);

        date_day1 = (TextView) day1_rl.findViewById(R.id.day1);
        date_day2 = (TextView) day2_rl.findViewById(R.id.day2);
        date_day3 = (TextView) day3_rl.findViewById(R.id.day3);
        date_day4 = (TextView) day4_rl.findViewById(R.id.day4);
        date_day5 = (TextView) day5_rl.findViewById(R.id.day5);
        date_day6 = (TextView) day6_rl.findViewById(R.id.day6);
        date_day7 = (TextView) day7_rl.findViewById(R.id.day7);

        mealCount_day1 = (TextView) day1_rl.findViewById(R.id.date1);
        mealCount_day2 = (TextView) day2_rl.findViewById(R.id.date2);
        mealCount_day3 = (TextView) day3_rl.findViewById(R.id.date3);
        mealCount_day4 = (TextView) day4_rl.findViewById(R.id.date4);
        mealCount_day5 = (TextView) day5_rl.findViewById(R.id.date5);
        mealCount_day6 = (TextView) day6_rl.findViewById(R.id.date6);
        mealCount_day7 = (TextView) day7_rl.findViewById(R.id.date7);

        commentCount_day1 = (TextView) day1_rl.findViewById(R.id.commentCount1);
        commentCount_day2 = (TextView) day2_rl.findViewById(R.id.commentCount2);
        commentCount_day3 = (TextView) day3_rl.findViewById(R.id.commentCount3);
        commentCount_day4 = (TextView) day4_rl.findViewById(R.id.commentCount4);
        commentCount_day5 = (TextView) day5_rl.findViewById(R.id.commentCount5);
        commentCount_day6 = (TextView) day6_rl.findViewById(R.id.commentCount6);
        commentCount_day7 = (TextView) day7_rl.findViewById(R.id.commentCount7);

        mealImage_day1 = (ImageView) day1_rl.findViewById(R.id.day1_mealcount);
        mealImage_day2 = (ImageView) day2_rl.findViewById(R.id.day2_mealcount);
        mealImage_day3 = (ImageView) day3_rl.findViewById(R.id.day3_mealcount);
        mealImage_day4 = (ImageView) day4_rl.findViewById(R.id.day4_mealcount);
        mealImage_day5 = (ImageView) day5_rl.findViewById(R.id.day5_mealcount);
        mealImage_day6 = (ImageView) day6_rl.findViewById(R.id.day6_mealcount);
        mealImage_day7 = (ImageView) day7_rl.findViewById(R.id.day7_mealcount);

        commentImage_day1 = (ImageView) day1_rl.findViewById(R.id.commentCount1_iv);
        commentImage_day2 = (ImageView) day2_rl.findViewById(R.id.commentCount2_iv);
        commentImage_day3 = (ImageView) day3_rl.findViewById(R.id.commentCount3_iv);
        commentImage_day4 = (ImageView) day4_rl.findViewById(R.id.commentCount4_iv);
        commentImage_day5 = (ImageView) day5_rl.findViewById(R.id.commentCount5_iv);
        commentImage_day6 = (ImageView) day6_rl.findViewById(R.id.commentCount6_iv);
        commentImage_day7 = (ImageView) day7_rl.findViewById(R.id.commentCount7_iv);

        date_day1.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
        mealImage_day1.setVisibility(View.GONE);
        mealCount_day1.setVisibility(View.GONE);
        commentImage_day1.setVisibility(View.GONE);
        commentCount_day1.setVisibility(View.GONE);
        mealCount_day1.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

        date_day2.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
        mealImage_day2.setVisibility(View.GONE);
        mealCount_day2.setVisibility(View.GONE);
        commentImage_day2.setVisibility(View.GONE);
        commentCount_day2.setVisibility(View.GONE);
        mealCount_day2.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

        date_day3.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
        mealImage_day3.setVisibility(View.GONE);
        mealCount_day3.setVisibility(View.GONE);
        commentImage_day3.setVisibility(View.GONE);
        commentCount_day3.setVisibility(View.GONE);
        mealCount_day3.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

        date_day4.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
        mealImage_day4.setVisibility(View.GONE);
        mealCount_day4.setVisibility(View.GONE);
        commentImage_day4.setVisibility(View.GONE);
        commentCount_day4.setVisibility(View.GONE);
        mealCount_day4.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

        date_day5.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
        mealImage_day5.setVisibility(View.GONE);
        mealCount_day5.setVisibility(View.GONE);
        commentImage_day5.setVisibility(View.GONE);
        commentCount_day5.setVisibility(View.GONE);
        mealCount_day5.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

        date_day6.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
        mealImage_day6.setVisibility(View.GONE);
        mealCount_day6.setVisibility(View.GONE);
        commentImage_day6.setVisibility(View.GONE);
        commentCount_day6.setVisibility(View.GONE);
        mealCount_day6.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

        date_day7.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
        mealImage_day7.setVisibility(View.GONE);
        mealCount_day7.setVisibility(View.GONE);
        commentImage_day7.setVisibility(View.GONE);
        commentCount_day7.setVisibility(View.GONE);
        mealCount_day7.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

//        leftArrow_rl.setVisibility(View.VISIBLE);
//        rightArrow_rl.setVisibility(View.INVISIBLE);

        dateline_tv = (TextView) ll.findViewById(R.id.dateline);
    }

    public void setSelectedDate(int idx) {
        if(currentSelectedDate != null) {


            currentSelectedDate = AppUtil.updateDate(currentSelectedDate, idx - currentSelectedDateIndex);
            ApplicationData.getInstance().currentSelectedDate = currentSelectedDate;
            previousSelectedDateIndex = currentSelectedDateIndex;
            currentSelectedDateIndex = idx;

            setSelectedDateOnClick();
        }
    }

    public void setSelectedDateOnClick() {

        updateMealCount();

        dateline_tv.setText(AppUtil.getMonthDayYear(currentSelectedDate));
        dateline_tv.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray)); //unselected date

        DateListener.dateChange(currentSelectedDate, currentSelectedDateIndex);

        setPreviousTab();

        updateSelectedTab();
    }

    public void setSelectedDateOnSwipe(int idx) {
        //we will need to get the meals from get meals again
        Intent broadint = new Intent();
        broadint.setAction(context.getResources().getString(R.string.meallist_getmeal_week));
        context.sendBroadcast(broadint);
    }

    public void initDate(Date date, Hashtable<String, Integer> mealCountPerWeek, Hashtable<String, Integer> commentCountPerWeek, Hashtable<String, String> dateTable, int totalDays) {
        initDate(date, mealCountPerWeek, commentCountPerWeek, dateTable, totalDays, currentSelectedDateIndex);
    }

    public void initDate(Date date, Hashtable<String, Integer> mealCountPerWeek, Hashtable<String, Integer> commentCountPerWeek, Hashtable<String, String> dateTable, int totalDays, int selectedindex) {

        this.mealCountPerWeek = mealCountPerWeek;
        this.commentCountPerWeek = commentCountPerWeek;
        this.totalDaysindex = totalDays;
        this.dateTable = dateTable;

        currentSelectedDate = date;
        currentSelectedDateIndex = AppUtil.getWeekIndexOfDate(date);
        todaysDate = new Date();

        weekStart = ApplicationData.getInstance().fromDateCurrent;
        weekEnd = ApplicationData.getInstance().toDateCurrent;

        System.out.println("+++++++++++initDate+++++++++++++");
        System.out.println("MEALS: initDate@DatePagerLayout");
        System.out.println("MEALS: today'sDate = " + todaysDate);
        System.out.println("MEALS: currentSelectedDate = " + currentSelectedDate);
        System.out.println("MEALS: currentSelectedDateIndex = " + currentSelectedDateIndex);
        System.out.println("MEALS: WeekStart = " + weekStart);
        System.out.println("MEALS: WeekEnd = " + weekEnd);
        System.out.println("MEALS: totalDaysIndaWeek = " + totalDaysindex);
        System.out.println("MEALS: mealCountPerWeek = " + mealCountPerWeek);
        System.out.println("++++++++++++++++++++++++");

        //init all tabs to default; gray text; no icon; no label
        initViews(ll1);

        //update all tabs from 0  total days light gray text, with orang icon, label = 0
        updateOnClickDefault(totalDaysindex);

        //update all tabs from 0  total days with correct meal count
        updateMealCount();

        //update selected tab
        updateSelectedTab();
    }

    private void updateOnClickDefault(int totalDaysIndex) {

        //set all tab to unselected
        day1_rl.setSelected(false);
        day2_rl.setSelected(false);
        day3_rl.setSelected(false);
        day4_rl.setSelected(false);
        day5_rl.setSelected(false);
        day6_rl.setSelected(false);
        day7_rl.setSelected(false);

        for (int i = 0; i <= totalDaysIndex; i++)
            updateDateTabUI(i, 0, 0);

        dateline_tv.setText(AppUtil.getMonthDayYear(currentSelectedDate));
        dateline_tv.setTextColor(ContextCompat.getColor(context, R.color.text_gray));

    }

    private void updateDateTabUI(int i, int label, int commentCount) {
        setPreviousTab();

        if (i == 0) {
            date_day1.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));

            if (label > 0) {
                mealCount_day1.setText("" + label);
                mealCount_day1.setTextColor(ContextCompat.getColor(context, R.color.text_orange));
                mealCount_day1.setVisibility(View.VISIBLE);
                mealImage_day1.setImageResource(R.drawable.meal_tabicon_meals_orange);
                mealImage_day1.setVisibility(VISIBLE);
            } else {
                mealCount_day1.setVisibility(GONE);
                mealImage_day1.setVisibility(GONE);
            }

            if (commentCount > 0) {
                commentImage_day1.setVisibility(View.VISIBLE);
                commentCount_day1.setVisibility(View.VISIBLE);
                commentCount_day1.setText("" + commentCount);
                commentCount_day1.setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                (day1_rl.findViewById(R.id.comment1_rl)).setVisibility(View.VISIBLE);
                commentImage_day1.setImageResource(R.drawable.meal_commented_icon);
            } else {
                commentImage_day1.setVisibility(View.GONE);
                commentCount_day1.setVisibility(View.GONE);
            }

        } else if (i == 1) {
            date_day2.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));

            if (label > 0) {
                mealCount_day2.setText("" + label);
                mealCount_day2.setTextColor(ContextCompat.getColor(context, R.color.text_orange));
                mealCount_day2.setVisibility(View.VISIBLE);
                mealImage_day2.setImageResource(R.drawable.meal_tabicon_meals_orange);
                mealImage_day2.setVisibility(VISIBLE);
            } else {
                mealCount_day2.setVisibility(GONE);
                mealImage_day2.setVisibility(GONE);
            }


            if (commentCount > 0) {
                commentImage_day2.setVisibility(View.VISIBLE);
                commentCount_day2.setVisibility(View.VISIBLE);
                commentCount_day2.setText("" + commentCount);
                commentCount_day2.setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                (day2_rl.findViewById(R.id.comment2_rl)).setVisibility(View.VISIBLE);
                commentImage_day2.setImageResource(R.drawable.meal_commented_icon);
            } else {
                commentImage_day2.setVisibility(View.GONE);
                commentCount_day2.setVisibility(View.GONE);
            }

        } else if (i == 2) {
            date_day3.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));

            if (label > 0) {
                mealCount_day3.setText("" + label);
                mealCount_day3.setTextColor(ContextCompat.getColor(context, R.color.text_orange));
                mealCount_day3.setVisibility(View.VISIBLE);
                mealImage_day3.setImageResource(R.drawable.meal_tabicon_meals_orange);
                mealImage_day3.setVisibility(VISIBLE);
            } else {
                mealCount_day3.setVisibility(GONE);
                mealImage_day3.setVisibility(GONE);
            }

            if (commentCount > 0) {
                commentImage_day3.setVisibility(View.VISIBLE);
                commentCount_day3.setVisibility(View.VISIBLE);
                commentCount_day3.setText("" + commentCount);
                commentCount_day3.setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                (day3_rl.findViewById(R.id.comment3_rl)).setVisibility(View.VISIBLE);
                commentImage_day3.setImageResource(R.drawable.meal_commented_icon);
            } else {
                commentImage_day3.setVisibility(View.GONE);
                commentCount_day3.setVisibility(View.GONE);
            }


        } else if (i == 3) {
            date_day4.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            if (label > 0) {
                mealCount_day4.setText("" + label);
                mealCount_day4.setTextColor(ContextCompat.getColor(context, R.color.text_orange));
                mealCount_day4.setVisibility(View.VISIBLE);
                mealImage_day4.setImageResource(R.drawable.meal_tabicon_meals_orange);
                mealImage_day4.setVisibility(VISIBLE);
            } else {
                mealCount_day4.setVisibility(GONE);
                mealImage_day4.setVisibility(GONE);
            }

            if (commentCount > 0) {
                commentImage_day4.setVisibility(View.VISIBLE);
                commentCount_day4.setVisibility(View.VISIBLE);
                commentCount_day4.setText("" + commentCount);
                commentCount_day4.setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                (day4_rl.findViewById(R.id.comment4_rl)).setVisibility(View.VISIBLE);
                commentImage_day4.setImageResource(R.drawable.meal_commented_icon);
            } else {
                commentImage_day4.setVisibility(View.GONE);
                commentCount_day4.setVisibility(View.GONE);
            }

        } else if (i == 4) {
            date_day5.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            if (label>0) {
                mealCount_day5.setText("" + label);
                mealCount_day5.setTextColor(ContextCompat.getColor(context, R.color.text_orange));
                mealCount_day5.setVisibility(View.VISIBLE);
                mealImage_day5.setImageResource(R.drawable.meal_tabicon_meals_orange);
                mealImage_day5.setVisibility(VISIBLE);
            }else{
                mealCount_day5.setVisibility(GONE);
                mealImage_day5.setVisibility(GONE);
            }

            if (commentCount > 0) {
                commentImage_day5.setVisibility(View.VISIBLE);
                commentCount_day5.setVisibility(View.VISIBLE);
                commentCount_day5.setText("" + commentCount);
                commentCount_day5.setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                (day5_rl.findViewById(R.id.comment5_rl)).setVisibility(View.VISIBLE);
                commentImage_day5.setImageResource(R.drawable.meal_commented_icon);
            } else {
                commentImage_day5.setVisibility(View.GONE);
                commentCount_day5.setVisibility(View.GONE);
            }

        } else if (i == 5) {
            date_day6.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            if (label>0) {
                mealCount_day6.setText("" + label);
                mealCount_day6.setTextColor(ContextCompat.getColor(context, R.color.text_orange));
                mealCount_day6.setVisibility(View.VISIBLE);
                mealImage_day6.setImageResource(R.drawable.meal_tabicon_meals_orange);
                mealImage_day6.setVisibility(VISIBLE);
            }else{
                mealCount_day6.setVisibility(GONE);
                mealImage_day6.setVisibility(GONE);
            }

            if (commentCount > 0) {
                commentImage_day6.setVisibility(View.VISIBLE);
                commentCount_day6.setVisibility(View.VISIBLE);
                commentCount_day6.setText("" + commentCount);
                commentCount_day6.setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                (day6_rl.findViewById(R.id.comment6_rl)).setVisibility(View.VISIBLE);
                commentImage_day6.setImageResource(R.drawable.meal_commented_icon);
            } else {
                commentImage_day6.setVisibility(View.GONE);
                commentCount_day6.setVisibility(View.GONE);
            }

        } else if (i == 6) {
            date_day7.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
            if (label>0) {
                mealCount_day7.setText("" + label);
                mealCount_day7.setTextColor(ContextCompat.getColor(context, R.color.text_orange));
                mealCount_day7.setVisibility(View.VISIBLE);
                mealImage_day7.setImageResource(R.drawable.meal_tabicon_meals_orange);
                mealImage_day7.setVisibility(VISIBLE);
            }else{
                mealImage_day7.setVisibility(GONE);
                mealCount_day7.setVisibility(GONE);
            }


            if (commentCount > 0) {
                commentImage_day7.setVisibility(View.VISIBLE);
                commentCount_day7.setVisibility(View.VISIBLE);
                commentCount_day7.setText("" + commentCount);
                commentCount_day7.setTextColor(ContextCompat.getColor(context, R.color.text_blue_comment));
                (day7_rl.findViewById(R.id.comment7_rl)).setVisibility(View.VISIBLE);
                commentImage_day7.setImageResource(R.drawable.meal_commented_icon);
            } else {
                commentImage_day7.setVisibility(View.GONE);
                commentCount_day7.setVisibility(View.GONE);
            }
        }
    }

    private void updateMealCount() {


        for (int i = 0; i < 7; i++) {


            String keyDate;
            int label, labelComment;

            if (dateTable.get(i + "") == null) {
                keyDate = "" + 0;
                label = 0;
                labelComment = 0;
            } else {
                keyDate = (dateTable.get("" + i));
                if (mealCountPerWeek.get("" + keyDate) == null)
                    label = 0;
                else
                    label = mealCountPerWeek.get("" + keyDate);

                if (commentCountPerWeek.get("" + keyDate) == null)
                    labelComment = 0;
                else
                    labelComment = commentCountPerWeek.get("" + keyDate);

            }

            if (i <= totalDaysindex)
                updateDateTabUI(i, label, labelComment);

        }
    }

    private void setPreviousTab() {
        if (currentSelectedDateIndex == previousSelectedDateIndex && previousSelectedDateIndex <= totalDaysindex)
            return;

        switch (previousSelectedDateIndex) {
            case 0:
                day1_rl.setSelected(false);

                //set text color
                if (totalDaysindex >= previousSelectedDateIndex) {
                    date_day1.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    //set icon to orange
                    mealImage_day1.setImageResource(R.drawable.meal_tabicon_meals_orange);
//                    mealImage_day1.setVisibility(GONE);
                    commentImage_day1.setImageResource(R.drawable.meal_commented_icon);

                } else {
                    date_day1.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                    mealImage_day1.setVisibility(INVISIBLE);
                }
                mealCount_day1.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

                break;
            case 1:
                day2_rl.setSelected(false);
                //set text color
                if (totalDaysindex >= previousSelectedDateIndex) {
                    date_day2.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    //set icon to orange
                    mealImage_day2.setImageResource(R.drawable.meal_tabicon_meals_orange);
                    commentImage_day2.setImageResource(R.drawable.meal_commented_icon);

                } else {
                    date_day2.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                    mealImage_day2.setVisibility(INVISIBLE);
                }
                mealCount_day2.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

                break;
            case 2:
                day3_rl.setSelected(false);
                //set text color
                if (totalDaysindex >= previousSelectedDateIndex) {
                    date_day3.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    //set icon to orange
                    mealImage_day3.setImageResource(R.drawable.meal_tabicon_meals_orange);
                    commentImage_day3.setImageResource(R.drawable.meal_commented_icon);

                } else {
                    date_day3.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                    mealImage_day3.setVisibility(INVISIBLE);
                }
                mealCount_day3.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

                break;
            case 3:
                day4_rl.setSelected(false);
                //set text color
                if (totalDaysindex >= previousSelectedDateIndex) {
                    date_day4.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    //set icon to orange
                    mealImage_day4.setImageResource(R.drawable.meal_tabicon_meals_orange);
                    commentImage_day4.setImageResource(R.drawable.meal_commented_icon);

                } else {
                    date_day4.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                    mealImage_day4.setVisibility(INVISIBLE);
                }
                mealCount_day4.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

                break;
            case 4:
                day5_rl.setSelected(false);
                //set text color
                if (totalDaysindex >= previousSelectedDateIndex) {
                    date_day5.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    //set icon to orange
                    mealImage_day5.setImageResource(R.drawable.meal_tabicon_meals_orange);
                    commentImage_day5.setImageResource(R.drawable.meal_commented_icon);

                } else {
                    date_day5.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                    mealImage_day5.setVisibility(INVISIBLE);
                }
                mealCount_day5.setTextColor(ContextCompat.getColor(context, R.color.text_orange));


            case 5:
                day6_rl.setSelected(false);
                //set text color
                if (totalDaysindex >= previousSelectedDateIndex) {
                    date_day6.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    //set icon to orange
                    mealImage_day6.setImageResource(R.drawable.meal_tabicon_meals_orange);
                    commentImage_day6.setImageResource(R.drawable.meal_commented_icon);

                } else {
                    date_day6.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                    mealImage_day6.setVisibility(INVISIBLE);
                }
                mealCount_day6.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

                break;
            case 6:
                day7_rl.setSelected(false);
                //set text color
                if (totalDaysindex >= previousSelectedDateIndex) {
                    date_day7.setTextColor(ContextCompat.getColor(context, R.color.text_darkgray));
                    //set icon to orange
                    mealImage_day7.setImageResource(R.drawable.meal_tabicon_meals_orange);
                    commentImage_day7.setImageResource(R.drawable.meal_commented_icon);

                } else {
                    date_day7.setTextColor(ContextCompat.getColor(context, R.color.text_lightgray));
                    mealImage_day7.setVisibility(INVISIBLE);
                }
                mealCount_day7.setTextColor(ContextCompat.getColor(context, R.color.text_orange));

                break;
        }
    }


    private void updateSelectedTab() {

        switch (currentSelectedDateIndex) {
            case 0:
                day1_rl.setSelected(true);

                //set text color
                date_day1.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                mealCount_day1.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                commentCount_day1.setTextColor(ContextCompat.getColor(context, R.color.text_white));

                //set icon to white
                mealImage_day1.setImageResource(R.drawable.meal_tabicon_meals_white);
                commentImage_day1.setImageResource(R.drawable.meal_commented_icon_white);

                break;
            case 1:
                day2_rl.setSelected(true);

                //set text color
                date_day2.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                mealCount_day2.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                commentCount_day2.setTextColor(ContextCompat.getColor(context, R.color.text_white));

                //set icon to white
                mealImage_day2.setImageResource(R.drawable.meal_tabicon_meals_white);
                commentImage_day2.setImageResource(R.drawable.meal_commented_icon_white);

                break;
            case 2:
                day3_rl.setSelected(true);

                //set text color
                date_day3.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                mealCount_day3.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                commentCount_day3.setTextColor(ContextCompat.getColor(context, R.color.text_white));

                //set icon to white
                mealImage_day3.setImageResource(R.drawable.meal_tabicon_meals_white);
                commentImage_day3.setImageResource(R.drawable.meal_commented_icon_white);

                break;
            case 3:
                day4_rl.setSelected(true);
                //set text color
                date_day4.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                mealCount_day4.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                commentCount_day4.setTextColor(ContextCompat.getColor(context, R.color.text_white));

                //set icon to white
                mealImage_day4.setImageResource(R.drawable.meal_tabicon_meals_white);
                commentImage_day4.setImageResource(R.drawable.meal_commented_icon_white);

                break;
            case 4:
                day5_rl.setSelected(true);
                //set text color
                date_day5.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                mealCount_day5.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                commentCount_day5.setTextColor(ContextCompat.getColor(context, R.color.text_white));

                //set icon to white
                mealImage_day5.setImageResource(R.drawable.meal_tabicon_meals_white);
                commentImage_day5.setImageResource(R.drawable.meal_commented_icon_white);

                break;
            case 5:
                day6_rl.setSelected(true);
                //set text color
                date_day6.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                mealCount_day6.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                commentCount_day6.setTextColor(ContextCompat.getColor(context, R.color.text_white));

                //set icon to white
                mealImage_day6.setImageResource(R.drawable.meal_tabicon_meals_white);
                commentImage_day6.setImageResource(R.drawable.meal_commented_icon_white);

                break;
            case 6:
                day7_rl.setSelected(true);
                //set text color
                date_day7.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                mealCount_day7.setTextColor(ContextCompat.getColor(context, R.color.text_white));
                commentCount_day7.setTextColor(ContextCompat.getColor(context, R.color.text_white));

                //set icon to white
                mealImage_day7.setImageResource(R.drawable.meal_tabicon_meals_white);
                commentImage_day7.setImageResource(R.drawable.meal_commented_icon_white);

                break;
        }
    }

    public int getSelectedDate() {
        return currentSelectedDateIndex;
    }

    @Override
    public void onClick(View v) {

        // TODO Auto-generated method stub
        int tagInx = (Integer) v.getTag();

        if (tagInx == 77) {
            goToPreviousWeek();
        } else if (tagInx == 88) {
            goToNextWeek();
        } else if (tagInx <= totalDaysindex) {
            setSelectedDate(tagInx);
        }
    }

    private void updateFields() {
        if (scrollType == SCROLL_TO_LEFT) {// add 1 week

            //depending on the current date add one week or less than one week
            int offsetDays = AppUtil.getDayDifference(weekStart, new Date());

            if (totalDaysindex < 6) {
                rightArrow_rl.setVisibility(View.INVISIBLE);
                //current week
                return;
            }

            if (offsetDays > 7) {
                Calendar cal1 = Calendar.getInstance();
                if(weekStart != null) {
                    cal1.setTime(weekStart);
                    cal1.add(Calendar.DAY_OF_MONTH, +7);
                    weekStart = cal1.getTime();

                    int offsetEnd = AppUtil.getDayDifference(weekStart, new Date());
                    if (offsetEnd >= 6) {
                        offsetEnd = 6;
                        rightArrow_rl.setVisibility(View.VISIBLE);
                    } else {
                        rightArrow_rl.setVisibility(View.INVISIBLE);
                    }
                    currentSelectedDate = AppUtil.updateDate(currentSelectedDate, 7);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(weekStart);
                    cal.add(Calendar.DAY_OF_MONTH, +offsetEnd); //weekend = weekstart + 7 days
                    weekEnd = cal.getTime();

                    if (currentSelectedDate.after(weekEnd)) {

                        currentSelectedDate = weekEnd;
                        currentSelectedDateIndex = AppUtil.getWeekIndexOfDate(currentSelectedDate);
                    }
                }
            } else {
                rightArrow_rl.setVisibility(View.INVISIBLE);

                Calendar cal1 = Calendar.getInstance();
                if (weekStart != null) {


                    cal1.setTime(weekStart);
                    cal1.add(Calendar.DAY_OF_MONTH, offsetDays); //previous weekend + offset
                    weekStart = cal1.getTime();

                    int offsetEnd = AppUtil.getDayDifference(weekStart, new Date());
                    if (offsetEnd >= 6) {
                        offsetEnd = 6;
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(weekStart);
                    cal.add(Calendar.DAY_OF_MONTH, offsetEnd); //current week start + offset
                    weekEnd = cal.getTime();

                    currentSelectedDate = weekStart;
                    previousSelectedDateIndex = currentSelectedDateIndex;
                    currentSelectedDateIndex = AppUtil.getWeekIndexOfDate(currentSelectedDate);
                }
            }
        } else if (scrollType == SCROLL_TO_RIGHT) { //minus one week

            rightArrow_rl.setVisibility(View.VISIBLE);

            if(currentSelectedDate == null)
            {
                currentSelectedDate = new Date();
            }
            currentSelectedDate = AppUtil.updateDate(currentSelectedDate, -7);
            Calendar cal1 = Calendar.getInstance();
            if(weekStart != null)
            {
                cal1.setTime(weekStart);
                cal1.add(Calendar.DAY_OF_MONTH, -7);
                weekStart = cal1.getTime();

                Calendar cal = Calendar.getInstance();
                cal.setTime(weekStart);
                cal.add(Calendar.DAY_OF_MONTH, +6); //week end =week start + 6 days
                weekEnd = cal.getTime();
            }

        }

        ApplicationData.getInstance().currentSelectedDate = currentSelectedDate;
        ApplicationData.getInstance().fromDateCurrent = weekStart;
        ApplicationData.getInstance().toDateCurrent = weekEnd;

        if(weekStart != null && weekEnd != null)
        {
            setSelectedDateOnSwipe(currentSelectedDateIndex);
        }

    }


    @SuppressLint("NewApi")
    public void setDatePager() {

        ApplicationData.getInstance().getWindowDimension(getContext());

        //Create a linear layout to hold each screen in the scroll view

        internalWrapper = new LinearLayout(getContext());
        internalWrapper.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        internalWrapper.setOrientation(LinearLayout.HORIZONTAL);


        try {
            removeView(internalWrapper);
        } catch (Exception e) {
        }
        addView(internalWrapper);

        //LinearLayout ll = null;
        for (int i = 0; i < MAX_PAGE; i++) {
            View v = inflate(getContext(), R.layout.carnet_dateheader, null);

            if (i == 0) {
                v.setId(R.id.datepage1);
            }
            v.setLayoutParams(new LayoutParams(ApplicationData.getInstance().screenWidth, LayoutParams.WRAP_CONTENT));


            try {
                internalWrapper.removeView(v);

            } catch (Exception e) {
            }
            internalWrapper.addView(v);
        }

        ll1 = (LinearLayout) internalWrapper.findViewById(R.id.datepage1);
        initViews(ll1);
        //setScrollBarSize(1);

        setOnTouchListener(new OnTouchListener() {
                               @Override
                               public boolean onTouch(View v, MotionEvent event) {
                                   //If the user swipes

                                   if (event.getAction() == MotionEvent.ACTION_UP) {

                                       X2_UP = event.getX();
                                       if (X1_MOVE > X2_UP && X1_MOVE - X2_UP >= SWIPE_MIN_DISTANCE) {
                                           scrollType = SCROLL_TO_LEFT;
                                           X1_MOVE = 0;
                                           X2_UP = 0;
                                           iStartSwipe = false;
                                           updateFields();
                                       } else if (X2_UP > X1_MOVE && X2_UP - X1_MOVE >= SWIPE_MIN_DISTANCE) {
                                           scrollType = SCROLL_TO_RIGHT;
                                           X1_MOVE = 0;
                                           X2_UP = 0;
                                           iStartSwipe = false;
                                           updateFields();
                                       }

                                       return true;

                                   } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                       if (!iStartSwipe) {
                                           iStartSwipe = true;
                                           X1_MOVE = event.getX();
                                       }
                                   }
                                   return false;

                               }
                           }
        );
    }

    public void updateProgressText(boolean finished) {
        if (finished) {
            dateline_tv.setText(AppUtil.getMonthDayYear(currentSelectedDate));
        } else {
            dateline_tv.setText("Loading...");
        }
    }

    public void goToPreviousWeek() {
        scrollType = SCROLL_TO_RIGHT;
        updateFields();
    }


    public void goToNextWeek() {
        scrollType = SCROLL_TO_LEFT;
        updateFields();
    }
}