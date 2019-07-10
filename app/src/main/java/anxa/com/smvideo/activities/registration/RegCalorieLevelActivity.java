package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.PersonnalisationContract;


/**
 * Created by aprilanxa on 21/02/2019.
 */

public class RegCalorieLevelActivity extends Activity implements View.OnClickListener {

    private CheckBox option1;
    private CheckBox option2;
    private CheckBox option3;
    private CheckBox option4;
    private CheckBox option5;

    private int selectedMealProfile = 1;
    private int selectedCalorieLevel = 1;
    private int selectedCoaching;

    private ProgressBar progressBar;

    ApiCaller caller;

    private PersonnalisationContract personnalisationContract;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.reg_calorie_level);
        ((ImageView) findViewById(R.id.backButton)).setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedMealProfile = extras.getInt("COACHING_PROFILE", 0);
            selectedCoaching = extras.getInt("COACHING_NUM", 0);

        }

        option1 = (CheckBox)findViewById(R.id.pers_option1_cb);
        option2 = (CheckBox)findViewById(R.id.pers_option2_cb);
        option3 = (CheckBox)findViewById(R.id.pers_option3_cb);
        option4 = (CheckBox)findViewById(R.id.pers_option4_cb);
        option5 = (CheckBox)findViewById(R.id.pers_option5_cb);

        if(ApplicationData.getInstance().regUserProfile.getGender() == "1") {
            option4.setChecked(true);
            selectCalorieLevelBtn(4);
        }else{
            option3.setChecked(true);
            selectCalorieLevelBtn(3);
        }
        //sans diner
        if (selectedMealProfile==13 || selectedMealProfile==12 || selectedMealProfile == 15 || selectedMealProfile == 16){
            ((LinearLayout) findViewById(R.id.pers_option1_ll)).setVisibility(View.GONE);
        }else if (selectedMealProfile==17){
            //sans sucre
            ((LinearLayout) findViewById(R.id.pers_option1_ll)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.pers_option3_ll)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.pers_option5_ll)).setVisibility(View.GONE);
        }

        progressBar = (ProgressBar)findViewById(R.id.progressBar_pers3);

        personnalisationContract = new PersonnalisationContract();
        personnalisationContract.CoachingProfile = selectedCoaching;
        personnalisationContract.MealProfile = selectedMealProfile;


            personnalisationContract.CalorieLevel = 3;



        caller = new ApiCaller();
    }

    @Override
    public void onClick(View v) {

    }

    public void continueCalorieLevel(View v) {
        System.out.println("continue " );
        submitPersonnalisation();
    }

    public void selectCalorieLevel(View v){
        System.out.println("selectCoaching " + v.getTag());

            selectCalorieLevelBtn(Integer.parseInt(v.getTag().toString()));

    }

    private void selectCalorieLevelBtn(int n){

        selectedCalorieLevel = n;

        option1.setChecked(false);
        option2.setChecked(false);
        option3.setChecked(false);
        option4.setChecked(false);

        switch (n){
            case 1:
                option1.setChecked(true);
                break;
            case 2:
                option2.setChecked(true);
                break;
            case 3:
                option3.setChecked(true);
                break;
            case 4:
                option4.setChecked(true);
                break;

            default:
                option1.setChecked(true);
                break;

        }

    }

    private void submitPersonnalisation() {
        System.out.println("continue reg" );
        personnalisationContract.CalorieLevel = selectedCalorieLevel;


        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        caller.PostPersonnalisation(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                Log.d("PostPersonnalisation", output.toString());
                PersonnalisationContract responseContract = (PersonnalisationContract) output;

                if (responseContract.Message.equalsIgnoreCase("Successful")){
                    goToWelcomePage();
                }
            }
        }, ApplicationData.getInstance().regUserProfile.getRegId(), personnalisationContract);

    }

    private void goToWelcomePage() {
        Intent mainIntent = new Intent(this, RegWelcomeActivity.class);
        this.startActivity(mainIntent);
    }

}
