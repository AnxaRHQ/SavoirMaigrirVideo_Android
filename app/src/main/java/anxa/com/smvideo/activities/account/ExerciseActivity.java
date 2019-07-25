package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;


import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.NpnaOfferActivity;
import anxa.com.smvideo.common.CommonConstants;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.Carnet.ExerciseContract;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataContract;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataResponseContract;
import anxa.com.smvideo.models.Workout;
import anxa.com.smvideo.ui.CustomDialog;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by ivanaanxa on 1/13/2017.
 */

public class ExerciseActivity extends Activity implements View.OnClickListener, TimePicker.OnTimeChangedListener
{
    final int MAX_DESC = 250;

    ImageView backButton;

    RelativeLayout exerciseOption, exerciseTimeContainer;

    ImageView exerciseWalkButton, exerciseRunButton, exerciseBikeButton, exerciseSwimButton, exerciseWorkOutButton, exerciseOtherButton, exercisebackButton;
    TextView selectOtherExercise, exerciseTime, durationTime, exerciseDescCount, step_labelSteps;
    EditText distanceET, stepsET, exerciseDescription;
    LinearLayout timePickerContainer, lldescription;
    Button done_picker, saveButton;
    TimePicker timepicker;
    SeekBar durationSeekBar;

    private int descRemainCount = 250, indexType;
    private Workout.EXERCISE_TYPE selectedExerciseType;
    CustomDialog dialog;
    String[] exerciseOtherArray;

    private ApiCaller caller;

    ExerciseContract currentWorkOut;
    String exerciseStatus;
    Integer workoutid;

    int DATE_YEAR;
    int DATE_MONTH;
    int DATE_DAY;

    private final String COMMAND_ADDED = "Added";
    private final String COMMAND_UPDATED = "Updated";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.exercise);

        ((TextView) findViewById(R.id.header_title_tv)).setText(R.string.EXERCISE);

        backButton = (ImageView) findViewById(R.id.header_menu_back);
        backButton.setOnClickListener(this);

        ((Button) findViewById(R.id.header_menu_iv)).setVisibility(View.GONE);

        UIInitialize();

        //SET DATE TIME
        String time = AppUtil.getTimeOnly12();//get current time
        exerciseTime.setText(time);
        setTimerPicker(false);

        caller = new ApiCaller();

        exerciseStatus = getIntent().getStringExtra("EXERCISE_STATUS");
        workoutid = getIntent().getIntExtra("EXERCISE_ACTIVITY_ID", 0);

        if (exerciseStatus == null)
            exerciseStatus = "Updated";

        if (exerciseStatus.equalsIgnoreCase(CommonConstants.COMMAND_ADDED)) {
            currentWorkOut = new ExerciseContract();
            currentWorkOut.CreationDate = AppUtil.dateToTimestamp(ApplicationData.getInstance().currentSelectedDate);

            //set date
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(AppUtil.dateToUnixTimestamp(ApplicationData.getInstance().currentSelectedDate) * 1000L);

            DATE_YEAR = c.get(Calendar.YEAR);
            DATE_MONTH = c.get(Calendar.MONTH) + 1;
            DATE_DAY = c.get(Calendar.DAY_OF_MONTH);

            currentWorkOut.Command = CommonConstants.COMMAND_ADDED;
        } else //COMMAND_UPDATED
        {
            if (workoutid != 0) {
                currentWorkOut = new ExerciseContract();
                currentWorkOut = ApplicationData.getInstance().currentWorkOut;
                updateUI();
            }
        }
    }

    private void updateUI()
    {
        String time = AppUtil.getTimeOnly12(currentWorkOut.CreationDate);

        switch (currentWorkOut.ExerciseType) {
            case 1: /*RUN*/
                selectedExerciseType = Workout.EXERCISE_TYPE.RUNNING;
                exerciseSelect(exerciseRunButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            case 2: /*BIKE/CYCLING*/
                selectedExerciseType = Workout.EXERCISE_TYPE.CYCLING;
                exerciseSelect(exerciseBikeButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            case 4: /*WALK*/
                selectedExerciseType = Workout.EXERCISE_TYPE.WALKING;
                exerciseSelect(exerciseWalkButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            case 10: /*"SWIM"*/
                selectedExerciseType = Workout.EXERCISE_TYPE.SWIMMING;
                exerciseSelect(exerciseSwimButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            case 35: /*"WORKOUT"*/
                selectedExerciseType = Workout.EXERCISE_TYPE.WORKOUT;
                exerciseSelect(exerciseWorkOutButton);
                exerciseOption.setVisibility(View.GONE);
                break;
            default:
                selectedExerciseType = Workout.EXERCISE_TYPE.OTHER;
                exerciseOtherButton.setSelected(true);
                exerciseOption.setVisibility(View.VISIBLE);
                break;
        }
        selectOtherExercise.setText(AppUtil.getExercise(this, currentWorkOut.ExerciseType));

        exerciseDescription.setText(currentWorkOut.Message);
        updateExerciseDescCount();
        exerciseDescritionTextChange();

        //set date
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(AppUtil.dateToUnixTimestamp(AppUtil.toDateGMT(currentWorkOut.CreationDate)) * 1000L);

        DATE_YEAR = c.get(Calendar.YEAR);
        DATE_MONTH = c.get(Calendar.MONTH) + 1;
        DATE_DAY = c.get(Calendar.DAY_OF_MONTH);

//        //TIME
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            timepicker.setMinute(AppUtil.getMinute(AppUtil.toDate(currentWorkOut.CreationDate)));
//            timepicker.setHour(AppUtil.getHour(AppUtil.toDate(currentWorkOut.CreationDate)));
//        } else {
//            timepicker.setCurrentMinute(AppUtil.getMinute(AppUtil.toDate(currentWorkOut.CreationDate)));
//            timepicker.setCurrentHour(AppUtil.getHour(AppUtil.toDate(currentWorkOut.CreationDate)));
//        }

        exerciseTime.setText(time);

        distanceET.setText(Double.toString(currentWorkOut.Distance));
        stepsET.setText(Integer.toString(currentWorkOut.Steps));
        durationTime.setText(" " + currentWorkOut.Duration + "mins");
        durationSeekBar.setProgress(currentWorkOut.Duration / 5);
    }

    private void UIInitialize()
    {
        //exercise button
        exerciseWalkButton = (ImageView) findViewById(R.id.exercise_walk_button);
        exerciseRunButton = (ImageView) findViewById(R.id.exercise_run_button);
        exerciseBikeButton = (ImageView) findViewById(R.id.exercise_bike_button);
        exerciseSwimButton = (ImageView) findViewById(R.id.exercise_swim_button);
        exerciseWorkOutButton = (ImageView) findViewById(R.id.exercise_workout_button);
        exerciseOtherButton = (ImageView) findViewById(R.id.exercise_other_button);
        //exercisebackButton = (ImageView) findViewById(R.id.backButton);

        exerciseWalkButton.setOnClickListener(this);
        exerciseRunButton.setOnClickListener(this);
        exerciseBikeButton.setOnClickListener(this);
        exerciseSwimButton.setOnClickListener(this);
        exerciseWorkOutButton.setOnClickListener(this);
        exerciseOtherButton.setOnClickListener(this);
        //exercisebackButton.setOnClickListener(this);

        //other
        exerciseOption = (RelativeLayout) findViewById(R.id.exercise_otheroption);
        selectOtherExercise = (TextView) findViewById(R.id.exercise_otheroption_text);
        exerciseOption.setVisibility(View.GONE);

        Resources res = getResources();
        exerciseOtherArray = res.getStringArray(R.array.otherexercise_array);


        //description
        lldescription = (LinearLayout) findViewById(R.id.exercise_description);
        exerciseDescription = (EditText) findViewById(R.id.exercise_desc);
        exerciseDescCount = (TextView) findViewById(R.id.exercise_desccount);
        updateExerciseDescCount();
        exerciseDescritionTextChange();

        //time picker
        timepicker = (TimePicker) findViewById(R.id.TimePicker);
        timePickerContainer = (LinearLayout) findViewById(R.id.timepickercontainer);
        exerciseTimeContainer = ((RelativeLayout) findViewById(R.id.exercise_time_container));
        exerciseTime = (TextView) findViewById(R.id.exercise_time);
        done_picker = (Button) findViewById(R.id.date_save_tv);
        done_picker.setBackgroundDrawable(null);

        done_picker.setOnClickListener(this);
        exerciseTimeContainer.setOnClickListener(this);

        timepicker.setIs24HourView(true);
        timepicker.setOnTimeChangedListener(this);
        setTimerPicker(false);

        //duration
        durationSeekBar = (SeekBar) findViewById(R.id.seekBar_duration);
        durationTime = (TextView) findViewById(R.id.duration_minutes);
        processTimeDuration();

        //distance and steps
        distanceET = (EditText) findViewById(R.id.distance_text);
        stepsET = (EditText) findViewById(R.id.step_text);
        step_labelSteps = (TextView) findViewById(R.id.step_labelSteps);
        step_labelSteps.setText(getString(R.string.STEPS));

        //save
        saveButton = (Button) findViewById(R.id.submitButton);
        saveButton.setOnClickListener(this);
    }

    //EXERCISE
    private void exerciseSelect(ImageView buttonSelect)
    {
        exerciseWalkButton.setSelected(false);
        exerciseRunButton.setSelected(false);
        exerciseBikeButton.setSelected(false);
        exerciseSwimButton.setSelected(false);
        exerciseWorkOutButton.setSelected(false);
        exerciseOtherButton.setSelected(false);
        exerciseOption.setVisibility(View.GONE);

        buttonSelect.setSelected(true);
        if (exerciseOtherButton.isSelected())
            exerciseOption.setVisibility(View.VISIBLE);
    }

    private void exerciseDeselect(ImageView buttonDeselect)
    {
        buttonDeselect.setSelected(false);
        exerciseOption.setVisibility(View.GONE);
        selectedExerciseType = null;
    }

    public void showOtherExerciseOptions(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Other");
        builder.setItems(exerciseOtherArray, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //Log.d(LOG_TAG, exerciseOtherArray[item]);
                selectOtherExercise.setText(exerciseOtherArray[item]);
            }
        });
        Dialog genericDialog = builder.create();
        genericDialog.show();
    }

    //DESCRIPTION
    private void updateExerciseDescCount()
    {
        exerciseDescCount.setText(descRemainCount + "/" + MAX_DESC);
    }

    private void exerciseDescritionTextChange()
    {
        exerciseDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                descRemainCount = MAX_DESC - s.length();
                updateExerciseDescCount();
            }

        });
    }

    //TIME PICKER
    private void setTimerPicker(Boolean isPickerShown)
    {
        if (isPickerShown) {
            //need to update with the current time on the time selected
            done_picker.setVisibility(View.VISIBLE);
            timePickerContainer.setVisibility(View.VISIBLE);

        } else { //hide picker
            done_picker.setVisibility(View.GONE);
            timePickerContainer.setVisibility(View.GONE);
        }
        //set time
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
    {
        Date date = AppUtil.formatDate(view, DATE_MONTH, DATE_DAY, DATE_YEAR);

        String time = AppUtil.getTimeOnlyMeals(date.getTime() / 1000);//get current time
        if (exerciseTime != null)
            exerciseTime.setText(time);

        //exerciseTime.setText(AppUtil.getExerciseTimeFromPicker(hourOfDay,minute));
        currentWorkOut.CreationDate = AppUtil.dateToTimestamp(date);

        System.out.println("exercise onTimeChanged date: " + date);
        System.out.println("exercise onTimeChanged DATE_MONTH: " + DATE_MONTH + DATE_DAY + DATE_YEAR);
        System.out.println("exercise onTimeChanged creationDate: " + currentWorkOut.CreationDate);
        System.out.println("exercise onTimeChanged time: " + time);
    }

    //DURATION
    private void processTimeDuration()
    {
        durationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                durationTime.setText(" " + String.valueOf(progress * 5) + " mins");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //SAVE
    private boolean isFormCompleted()
    {
        if (selectedExerciseType == null) {
            showCustomDialog();
            return false;
        } else if (selectedExerciseType == Workout.EXERCISE_TYPE.OTHER && selectOtherExercise.getText().toString().equalsIgnoreCase(getString(R.string.EXERCISE_OTHEROPTION))) {
            showCustomDialog();
            return false;
        } else {
            if (exerciseDescription.length() == 0) exerciseDescription.setText("");
            if (distanceET.length() == 0) distanceET.setText("0");
            if (stepsET.length() == 0) stepsET.setText("0");
        }
        return true;
    }

    private void showCustomDialog()
    {
        dialog = new CustomDialog(this, getResources().getString(R.string.btn_ok), null, null, false, getResources().getString(R.string.ALERTMESSAGE_EXERCISETYPE), null, this);
        dialog.show();
    }

    @Override
    public void onClick(View v)
    {
        if (v == backButton)
        {
            finish();
        }
        //walk
        else if (v.getId() == R.id.exercise_walk_button) {
            currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.WALKING.getValue();
            selectedExerciseType = Workout.EXERCISE_TYPE.WALKING;
            if (exerciseWalkButton.isSelected()) exerciseDeselect(exerciseWalkButton);
            else exerciseSelect(exerciseWalkButton);
        }
        //run
        else if (v.getId() == R.id.exercise_run_button) {
            currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.RUNNING.getValue();
            selectedExerciseType = Workout.EXERCISE_TYPE.RUNNING;
            if (exerciseRunButton.isSelected()) exerciseDeselect(exerciseRunButton);
            else exerciseSelect(exerciseRunButton);
        }
        //bike
        else if (v.getId() == R.id.exercise_bike_button) {
            currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.CYCLING.getValue();
            selectedExerciseType = Workout.EXERCISE_TYPE.CYCLING;
            if (exerciseBikeButton.isSelected()) exerciseDeselect(exerciseBikeButton);
            else exerciseSelect(exerciseBikeButton);
        }
        //swim
        else if (v.getId() == R.id.exercise_swim_button) {
            currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.SWIMMING.getValue();
            selectedExerciseType = Workout.EXERCISE_TYPE.SWIMMING;
            if (exerciseSwimButton.isSelected()) exerciseDeselect(exerciseSwimButton);
            else exerciseSelect(exerciseSwimButton);
        }
        //workout
        else if (v.getId() == R.id.exercise_workout_button) {
            currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.WORKOUT.getValue();
            selectedExerciseType = Workout.EXERCISE_TYPE.WORKOUT;
            if (exerciseWorkOutButton.isSelected()) exerciseDeselect(exerciseWorkOutButton);
            else exerciseSelect(exerciseWorkOutButton);
        }
        //other
        else if (v.getId() == R.id.exercise_other_button) {
            currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.OTHER.getValue();
            selectedExerciseType = Workout.EXERCISE_TYPE.OTHER;
            if (exerciseOtherButton.isSelected()) exerciseDeselect(exerciseOtherButton);
            else exerciseSelect(exerciseOtherButton);
        }
        //datepicker
        else if (v.getId() == R.id.date_save_tv) {
            setTimerPicker(false);
        } else if (v.getId() == R.id.exercise_time_container) {
            setTimerPicker(true);
        }
        //submit (Save) Button
        else if (v.getId() == R.id.submitButton) {
            Boolean isFormCompleted = isFormCompleted();
            System.out.println("Check Form: " + isFormCompleted);
            if (isFormCompleted) postWorkout(v);
            else System.out.println("Invalid Form!");

            System.out.println("SAVED!!!");
        } else if (v.getId() == R.id.backButton || v.getId() == R.id.navView) {
            onBackPressed();
        } else if (v.getId() == R.id.OtherButton) {
            if (dialog != null)
                dialog.dismiss();
        }

    }

    private void getValueOfWorkOut()
    {
        currentWorkOut.Message = exerciseDescription.getText().toString();
        //currentWorkOut.CreationDate = 0;
        currentWorkOut.Duration = Integer.valueOf(durationTime.getText().toString().replace("mins", "").replace(" ", ""));
        currentWorkOut.Distance = new Double(distanceET.getText().toString());
        currentWorkOut.Steps = Integer.valueOf(stepsET.getText().toString());

        if (selectedExerciseType == Workout.EXERCISE_TYPE.OTHER)
        {
            String answeSur = (String) (selectOtherExercise).getText().toString();
            indexType = Arrays.asList(exerciseOtherArray).indexOf(answeSur);
            switch (indexType) {
                case 0:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.AQUAGYM.getValue();
                    break;
                case 1:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.MOUNTAIN_BIKING.getValue();
                    break;
                case 2:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.HIKING.getValue();
                    break;
                case 3:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.DOWNHILL_SKIING.getValue();
                    break;
                case 4:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.CROSSCOUNTRY_SKIING.getValue();
                    break;
                case 5:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.SNOWBOARDING.getValue();
                    break;
                case 6:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.SKATING.getValue();
                    break;
                case 7:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.ROWING.getValue();
                    break;
                case 8:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.ELLIPTICAL.getValue();
                    break;
                case 9:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.YOGA.getValue();
                    break;
                case 10:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.PILATES.getValue();
                    break;
                case 11:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.ZUMBA.getValue();
                    break;
                case 12:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.BARRE.getValue();
                    break;
                case 13:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.GROUP_WORKOUT.getValue();
                    break;
                case 14:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.DANCE.getValue();
                    break;
                case 15:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.BOOTCAMP.getValue();
                    break;
                case 16:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.BOXING.getValue();
                    break;
                case 17:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.MMA.getValue();
                    break;
                case 18:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.MEDITATION.getValue();
                    break;
                case 19:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.CORE_STRENGTHENING.getValue();
                    break;
                case 20:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.ARC_TRAINER.getValue();
                    break;
                case 21:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.STAIR_MASTER.getValue();
                    break;
                case 22:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.NORDIC_WALKING.getValue();
                    break;
                case 23:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.SPORTS.getValue();
                    break;
                case 24:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.GOLF.getValue();
                    break;
                case 25:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.HOUSEWORK.getValue();
                    break;
                case 26:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.GARDENING.getValue();
                    break;
                default:
                    currentWorkOut.ExerciseType = Workout.EXERCISE_TYPE.OTHER.getValue();
                    break;
            }
        }
        System.out.println("currentWorkExerciseType: " + currentWorkOut.ExerciseType);
    }

    private void postWorkout(View v)
    {
        if (!CheckFreeUser(true))
        {
            ApplicationData.getInstance().currentWorkOut = currentWorkOut;
            //System.out.println("currentWorkOut: " + currentWorkOut);

            getValueOfWorkOut();

            if (exerciseStatus.equalsIgnoreCase(CommonConstants.COMMAND_ADDED))
            {
                if (currentWorkOut.CreationDate < 1) {
                    currentWorkOut.CreationDate = AppUtil.dateToTimestamp(ApplicationData.getInstance().currentSelectedDate);
                }
                saveExerciseToApi(COMMAND_ADDED);
            }//end add
            else
            { //update
                saveExerciseToApi(COMMAND_UPDATED);
            }//end update
        }
    }

    private void saveExerciseToApi(String command)
    {
        //showSavingLayout(true, false);
        UploadMealsDataContract contract = new UploadMealsDataContract();
        currentWorkOut.Command = command;
        contract.Exercise.add(currentWorkOut);

        caller.PostCarnetSync(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                //Log.d("PostAddExercise", output.toString());
                UploadMealsDataResponseContract response = output != null ? (UploadMealsDataResponseContract) output : new UploadMealsDataResponseContract();
                if (response.Message == null) {
                    finish();
                }
                currentWorkOut.ExerciseId = response.Data.Exercise.get(0).ExerciseId;

                //broadcast the update
                Intent broadInt = new Intent();
                broadInt.setAction(getResources().getString(R.string.meallist_getmeal_week));
                sendBroadcast(broadInt);

                finish();
            }
        }, ApplicationData.getInstance().userDataContract.Id, contract);
    }

    /* Free Users */

    public boolean CheckFreeUser(boolean withDialog)
    {
        if (ApplicationData.getInstance().userDataContract.MembershipType == 0 && ApplicationData.getInstance().userDataContract.WeekNumber > 1)
        {
            if (withDialog)
            {
                showFreeExpiredDialog();
            }

            return true;
        }

        return false;
    }

    private void showFreeExpiredDialog()
    {
        final Dialog freeExpiredDialog = new Dialog(this);
        freeExpiredDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        freeExpiredDialog.setContentView(R.layout.free_expired_dialog);
        freeExpiredDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((TextView) freeExpiredDialog.findViewById(R.id.dialog_content)).setText(getString(R.string.FREE_1WEEKTRIAL_EXPIRED));

        ((Button) freeExpiredDialog.findViewById(R.id.dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                freeExpiredDialog.dismiss();
            }
        });

        ((Button) freeExpiredDialog.findViewById(R.id.dialog_payment)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                freeExpiredDialog.dismiss();
                goToPremiumPayment();
            }
        });

        freeExpiredDialog.show();
    }

    private void goToPremiumPayment()
    {
        Intent mainContentBrowser = new Intent(this, NpnaOfferActivity.class);
        mainContentBrowser.putExtra("UPGRADE_PAYMENT", true);
        startActivity(mainContentBrowser);
    }
}
