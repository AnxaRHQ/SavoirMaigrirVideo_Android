package anxa.com.smvideo.activities.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.connection.listener.DateChangeListener;
import anxa.com.smvideo.contracts.Carnet.ExerciseContract;
import anxa.com.smvideo.contracts.Carnet.GetCarnetSyncContract;
import anxa.com.smvideo.contracts.Carnet.GetCarnetSyncDataContract;
import anxa.com.smvideo.contracts.Carnet.MealContract;
import anxa.com.smvideo.contracts.Carnet.MoodContract;
import anxa.com.smvideo.contracts.Carnet.WaterContract;
import anxa.com.smvideo.contracts.Graph.GetStepDataResponseContract;
import anxa.com.smvideo.contracts.Graph.StepDataContract;
import anxa.com.smvideo.ui.CarnetList;
import anxa.com.smvideo.ui.CustomDialog;
import anxa.com.smvideo.ui.DatePagerLayout;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by aprilanxa on 29/03/2018.
 */

public class CarnetAccountFragment extends Fragment implements View.OnClickListener, DateChangeListener{

    private DatePagerLayout dateC;
    private int selectedDayIndex = 0;
    private int dayOfWeek;

    private CarnetList mealList;

    private Hashtable<String, Integer> mealCountPerWeek;
    private Hashtable<String, Integer> commentCountPerWeek;

    private Date toDate = AppUtil.getCurrentDateinDate();
    private Date fromDate;
    private CustomDialog dialog = null;

    private int currentDate_day = 0;

    private GetCarnetSyncContract newCarnetSyncContract;

    private List<MealContract> selectedDailyMealList = new ArrayList<>();
    private List<MoodContract> selectedDailyMoodList = new ArrayList<>();
    private List<ExerciseContract> selectedDailyWorkoutList = new ArrayList<>();
   /* private List<StepDataContract> selectedStepList  = new ArrayList<>();*/

    public Hashtable<String, MealContract> tempList = new Hashtable<String, MealContract>();
    public Hashtable<String, MoodContract> moodList = new Hashtable<String, MoodContract>();
    public Hashtable<String, ExerciseContract> exerciseList = new Hashtable<String, ExerciseContract>();
    public Hashtable<String, StepDataContract> stepList = new Hashtable<String, StepDataContract>();

    private Hashtable<String, List<MealContract>> weeklyMeal = new Hashtable<>();
    private Hashtable<String, List<MoodContract>> weeklyMood = new Hashtable<>();
    private Hashtable<String, List<ExerciseContract>> weeklyWorkout = new Hashtable<>();
    private Hashtable<String, List<StepDataContract>> weeklyStep = new Hashtable<>();
    // hast table f date ranges
    // example: {"1","5_28","2","5_29","3","5_30","4","5_1"};
    private Hashtable<String, String> dateTable = new Hashtable<>();
    Dialog freeDialog;
    AlertDialog.Builder alertBuilder;
    private GetStepDataResponseContract stepsResponse;
    FrameLayout progressBarHolder;
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;


    private Context context;
    protected ApiCaller caller;
    View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();
        mView = inflater.inflate(R.layout.carnet, null);

        caller = new ApiCaller();

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_account_messages));
        ((TextView) (mView.findViewById(R.id.header_right_tv))).setVisibility(View.INVISIBLE);

        mealList = (CarnetList) mView.findViewById(R.id.meallist);

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.getString(R.string.meallist_getmeal_week));
        context.registerReceiver(the_receiver, filter);

        toDate = new Date();
        ApplicationData.getInstance().currentSelectedDate = toDate;

        if (ApplicationData.getInstance().currentSelectedDate != null) {

            Calendar c = Calendar.getInstance();
            c.setFirstDayOfWeek(Calendar.SUNDAY);

            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);

            Date sunday = c.getTime();

            Date nextSunday = new Date(sunday.getTime() + 7 * 24 * 60 * 60 * 1000);

            boolean isThisWeek = ApplicationData.getInstance().currentSelectedDate.after(sunday) && ApplicationData.getInstance().currentSelectedDate.before(nextSunday);

            if (isThisWeek) {
                // get the index of today's date
                dayOfWeek = AppUtil.getWeekIndexOfDate(toDate);

                selectedDayIndex = dayOfWeek; //set selected date today //AS OF JULY 7

                // get from date by adding today and index
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                c.setTime(toDate);

                currentDate_day = c.get(Calendar.DAY_OF_MONTH);
                c.add(Calendar.DATE, ((-1) * (dayOfWeek)));
                fromDate = c.getTime();

                // get date ranges in a hashtable
                // hashtable f date ranges
                // example: {"1",28,"2",29,"3",30,"4",1};
                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);

                for (int i = 0; i <= dayOfWeek; i++) {
                    dateTable.put("" + (i), (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DAY_OF_MONTH));
                    c.add(Calendar.DAY_OF_MONTH, 1);
                }

            } else {
                //else other week
                // get the index of currently selected date
                selectedDayIndex = AppUtil.getWeekIndexOfDate(ApplicationData.getInstance().currentSelectedDate);

                dayOfWeek = 7;

                fromDate = new Date(ApplicationData.getInstance().currentSelectedDate.getTime() - selectedDayIndex * 24 * 60 * 60 * 1000);
                toDate = new Date(fromDate.getTime() + 6 * 24 * 60 * 60 * 1000);

                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);

                for (int i = 0; i <= dayOfWeek; i++) {
                    dateTable.put("" + (i), (cal.get(Calendar.MONTH) + 1) + "_" + cal.get(Calendar.DAY_OF_MONTH));
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        }

        ApplicationData.getInstance().toDateCurrent = toDate;
        ApplicationData.getInstance().fromDateCurrent = fromDate;

        dateC = (DatePagerLayout) mView.findViewById(R.id.meallist_header);
        dateC.setDatePager();
        dateC.updateProgressText(true);

        getCarnetSync();

        mealList.initDate(selectedDailyMealList, context, this);

        return mView;

    }

    @Override
    public void onDestroy() {
//        unregisterReceiver(the_receiver);

        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
    }

    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == context.getResources().getString(R.string.meallist_getmeal_week)) {
                // this is the content
                System.out.println("broadcast: meallist_getmeal_week");
                reInitDate();
                getCarnetSync();
                dateC.updateProgressText(false);
            }
        }
    };

    public void goBackToMain(View view) {
//        finish();
    }


    /**
     * Delegate Methods
     **/
    @Override
    public void dateChange(Date date, int weekIndex) {
        // TODO Auto-generated method stub

        // date change get new currentdate
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        selectedDayIndex = weekIndex; //set selected date based on whats is passed by the DateUI //

        int currentDate_day = c.get(Calendar.DAY_OF_MONTH);
        int currentDate_month = c.get(Calendar.MONTH) + 1;

        selectedDailyMealList = weeklyMeal.get(currentDate_month + "_" + currentDate_day);
        selectedDailyMoodList = weeklyMood.get(currentDate_month + "_" + currentDate_day);
        mealList.updateData(selectedDailyMealList);
        mealList.updateMoodData(selectedDailyMoodList);

        /*Workout*/
        selectedDailyWorkoutList = weeklyWorkout.get(currentDate_month + "_" + currentDate_day);
        ArrayList<StepDataContract> temp = new ArrayList<StepDataContract>();
//        DaoImplementer implDao = new DaoImplementer(new GoogleFitDAO(this, null), this);
//        temp = implDao.getGoogleFitStepByUserPerDate(Integer.parseInt(ApplicationData.getInstance().userProfile.getAj_regno()), c.get(Calendar.YEAR) + "-" + currentDate_month + "-" + currentDate_day);


        for (StepDataContract stepContract : temp) {
            ExerciseContract eContract = new ExerciseContract();
            eContract.CreationDate = AppUtil.stringToDateWeight(stepContract.StepDate).getTime();
            eContract.Distance = stepContract.Distance;
            eContract.Steps = stepContract.Steps;
            eContract.Duration = stepContract.Duration / 60;
            eContract.Calories = stepContract.Calories;
            boolean toAdd = true;
            for (ExerciseContract ex : selectedDailyWorkoutList) {
                if (ex.ExerciseId == 0 && ex.ExerciseType == 0) {
                    toAdd = false;
                }
            }
            if (toAdd) {
                selectedDailyWorkoutList.add(eContract);
            }

            // selectedDailyWorkoutList.add(new ExerciseContra});
        }
        System.out.println("Debug WorkoutList: weeklyWorkout: " + weeklyWorkout.toString());
        System.out.println("Debug WorkoutList: currentDate_month: " + currentDate_month + "_" + currentDate_day);
        System.out.println("Debug WorkoutList: " + selectedDailyWorkoutList);
        mealList.updateExerciseData(selectedDailyWorkoutList);
    }

    @Override
    public void onClick(View v) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {

            if (v.getId() == R.id.mealinfo) {
                displayInfo((int) v.getTag());
            } else if (v.getTag() != null) {

                System.out.println("onClick: " + v.getTag());
                if (v.getTag() instanceof MealContract) {
                    viewMeal((MealContract) v.getTag());
                } else {
                    int mealType = (int) v.getTag();
                    addNewMeal(mealType);
                }
            } else {
                viewMeal((MealContract) v.getTag());
            }
        }
    }

    /**
     * Private Methods
     **/

    private void getCarnetSync() {

        caller.GetCarnetSync(new AsyncResponse() {
            @Override
            public void processFinish(final Object output) {

                GetCarnetSyncContract response = output != null ? (GetCarnetSyncContract) output : new GetCarnetSyncContract();
                //something went wrong finish so user will be forced to populate meals again
                if (output == null || (response != null && !response.Message.equalsIgnoreCase("Successful"))) {
                    displayToastMessage(getString(R.string.messages_error));

                    new java.util.Timer().schedule(
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    getActivity().finish();
                                }
                            },
                            2000
                    );


                }
              /*  try {
                    caller.GetAllSteps(new AsyncResponse() {
                        @Override
                        public void processFinish(Object output2) {
                            // ((ProgressBar) findViewById(R.id.steps_graph_progressBar)).setVisibility(View.GONE);

                            stepsResponse = output2 != null ? (GetStepDataResponseContract) output2 : new GetStepDataResponseContract();
                            if (stepsResponse != null & !stepsResponse.Message.equalsIgnoreCase("Failed")) {
                                ApplicationData.getInstance().stepsList = stepsResponse.Data;

                            }

                            GetCarnetSyncContract response = output != null ? (GetCarnetSyncContract) output : new GetCarnetSyncContract();
                            //something went wrong finish so user will be forced to populate meals again
                            if(output2 == null || (response != null && !response.Message.equalsIgnoreCase("Successful"))){
                                finish();
                            }
                            populateWeeklyList(response.Data);
                        }
                    }, Integer.parseInt(ApplicationData.getInstance().userProfile.getReg_id()), AppUtil.getDateStringGetSync(fromDate), AppUtil.getDateStringGetSync(AppUtil.getCurrentDateinDate()));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                populateWeeklyList(response.Data);


            }
        }, ApplicationData.getInstance().regId, AppUtil.getDateStringGetSync(fromDate), AppUtil.getDateStringGetSync(toDate));
    }

    private void getSteps() {

    }


    private void reInitDate() {
        // get the index of today's date
        toDate = ApplicationData.getInstance().toDateCurrent;

        dayOfWeek = AppUtil.getWeekIndexOfDate(ApplicationData.getInstance().toDateCurrent);

        // get from date by adding today and index
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Calendar c = Calendar.getInstance();
        c.setTime(toDate);

        currentDate_day = c.get(Calendar.DAY_OF_MONTH);
        c.add(Calendar.DATE, ((-1) * (dayOfWeek)));
        fromDate = c.getTime();

        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        for (int i = 0; i <= dayOfWeek; i++) {
            dateTable.put("" + (i), (c.get(Calendar.MONTH) + 1) + "_" + c.get(Calendar.DAY_OF_MONTH));
            c.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void updateDateHeaders(DatePagerLayout dateC) {
        dateC.setDateListener(this);
        dateC.initDate(ApplicationData.getInstance().currentSelectedDate, mealCountPerWeek, commentCountPerWeek, dateTable, dayOfWeek, selectedDayIndex);
    }

    private void displayInfo(int mealtype) {
        String message = AppUtil.getMealTip(context, dayOfWeek, mealtype);
        String title = AppUtil.getMealTitle(context, mealtype);
        dialog = new CustomDialog(context, null, null, null, true, message, title, this);
        dialog.show();
    }

    private void addNewMeal(int mealType) {
        Intent mainIntent;
        mainIntent = new Intent(context, MealAddActivity.class);
        mainIntent.putExtra("MEAL_TYPE", mealType);
        mainIntent.putExtra("MEAL_STATUS", "add");
        startActivity(mainIntent);
    }

    private void viewMeal(MealContract meal) {
        System.out.println("viewMeal: " + meal.Description);
        ApplicationData.getInstance().currentMealView = meal;
        Intent mainIntent;
        mainIntent = new Intent(context, MealViewActivity.class);
        mainIntent.putExtra("STACK_STATUS", "NORMAL");

        startActivityForResult(mainIntent, ApplicationData.REQUESTCODE_MEALVIEW);
    }

    private void populateWeeklyList(GetCarnetSyncDataContract contract) {
        if (contract != null) {


            List<MealContract> mealContractList = contract.Meals;
            List<MoodContract> moodContractList = contract.Mood;
            List<WaterContract> waterContractList = contract.Water;
            List<ExerciseContract> exerciseContractList = contract.Exercise;

            ApplicationData.getInstance().waterList = waterContractList;

            //mealId, MealContract
            tempList = new Hashtable<String, MealContract>();
            for (MealContract mealContract : mealContractList) {
                tempList.put(Integer.toString(mealContract.MealId), mealContract);
            }

            ApplicationData.getInstance().tempList.putAll(tempList);
            moodList = new Hashtable<String, MoodContract>();
            for (MoodContract moodContract : moodContractList) {
                moodList.put(Integer.toString(moodContract.MoodId), moodContract);
            }
            exerciseList = new Hashtable<String, ExerciseContract>();
            for (ExerciseContract exerciseContract : exerciseContractList) {
                exerciseList.put(Integer.toString(exerciseContract.ExerciseId), exerciseContract);
            }

            weeklyMeal = AppUtil.getMealsByDateRange(toDate, fromDate, tempList, dayOfWeek + 1);
            weeklyMood = AppUtil.getMoodByDateRange(toDate, fromDate, moodList, dayOfWeek + 1);
            weeklyWorkout = AppUtil.getWorkOutByDateRange(toDate, fromDate, exerciseList, dayOfWeek + 1);
            mealCountPerWeek = AppUtil.getMealCountPerWeek(weeklyMeal);
            commentCountPerWeek = AppUtil.getCommentCountPerWeek(weeklyMeal);


      /*  stepList = new Hashtable<String, StepDataContract>();
        for (StepDataContract stepContract : ApplicationData.getInstance().stepsList) {
            stepList.put(Integer.toString(stepContract.Id), stepContract);
        }
        weeklyStep = AppUtil.getStepsByDateRange(toDate, fromDate, stepList, dayOfWeek + 1, 90);*/


            selectedDailyMealList = weeklyMeal.get(AppUtil.getMonthonDate(ApplicationData.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationData.getInstance().currentSelectedDate));
            selectedDailyMoodList = weeklyMood.get(AppUtil.getMonthonDate(ApplicationData.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationData.getInstance().currentSelectedDate));
            selectedDailyWorkoutList = weeklyWorkout.get(AppUtil.getMonthonDate(ApplicationData.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationData.getInstance().currentSelectedDate));
           /* selectedStepList = weeklyStep.get(AppUtil.getMonthonDate(ApplicationData.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationData.getInstance().currentSelectedDate));
            for (StepDataContract stepContract : selectedStepList) {
                ExerciseContract eContract = new ExerciseContract();
                eContract.CreationDate = AppUtil.stringToDateWeight(stepContract.StepDate).getTime();
                eContract.Distance = stepContract.Distance;
                eContract.Steps = stepContract.Steps;
               eContract.Duration = stepContract.Duration/60;
                eContract.Calories = stepContract.Calories;

                boolean toAdd = true;
                for(ExerciseContract ex : selectedDailyWorkoutList)
                {
                    if(ex.ExerciseId == 0 && ex.ExerciseType == 0)
                    {
                        toAdd = false;
                    }
                }
                if(toAdd)
                {
                    selectedDailyWorkoutList.add(eContract);
                }
                // selectedDailyWorkoutList.add(new ExerciseContra});
            }*/
            //StepDataContract stepContract = ApplicationData.getInstance().googleFitTempList.get(AppUtil.getMonthonDate(ApplicationData.getInstance().currentSelectedDate) + "_" + AppUtil.getDayonDate(ApplicationData.getInstance().currentSelectedDate));
            ArrayList<StepDataContract> temp = new ArrayList<StepDataContract>();
//            DaoImplementer implDao = new DaoImplementer(new GoogleFitDAO(this, null), this);
            Calendar c = Calendar.getInstance();
            c.setTime(ApplicationData.getInstance().currentSelectedDate);

//            temp = implDao.getGoogleFitStepByUserPerDate(Integer.parseInt(ApplicationData.getInstance().userProfile.getAj_regno()), c.get(Calendar.YEAR) + "-" + AppUtil.getMonthonDate(ApplicationData.getInstance().currentSelectedDate) + "-" + AppUtil.getDayonDate(ApplicationData.getInstance().currentSelectedDate));


            for (StepDataContract stepContract : temp) {
                ExerciseContract eContract = new ExerciseContract();
                eContract.CreationDate = AppUtil.stringToDateWeight(stepContract.StepDate).getTime();
                eContract.Distance = stepContract.Distance;
                eContract.Steps = stepContract.Steps;
                eContract.Duration = stepContract.Duration / 60;
                eContract.Calories = stepContract.Calories;
                boolean toAdd = true;
                for (ExerciseContract ex : selectedDailyWorkoutList) {
                    if (ex.ExerciseId == 0 && ex.ExerciseType == 0) {
                        toAdd = false;
                    }
                }
                if (toAdd) {
                    selectedDailyWorkoutList.add(eContract);
                }

                // selectedDailyWorkoutList.add(new ExerciseContra});
            }
            System.out.println("populateWeeklyList: templist: " + tempList);
            System.out.println("populateWeeklyList: weeklyMeal: " + weeklyMeal);
            System.out.println("populateWeeklyList: mealCountPerWeek: " + mealCountPerWeek);
            System.out.println("populateWeeklyList: waterList: " + waterContractList);
            System.out.println("populateWeeklyList: exerciseList: " + exerciseList);

            mealList.updateData(selectedDailyMealList);
            mealList.updateMoodData(selectedDailyMoodList);
            mealList.updateExerciseData(selectedDailyWorkoutList);
            updateDateHeaders(dateC);
        }

    }

    public void displayToastMessage(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast m = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                m.show();

            }
        });
    }


}
