package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.contracts.PersonnalisationContract;


/**
 * Created by aprilanxa on 13/12/2018.
 */

public class RegPersonnalisation2Activity extends Activity implements View.OnClickListener {

    private int selectedCoaching;
    private int selectedMealProfile = 0;
    private CheckBox option1;
    private CheckBox option2;
    private CheckBox option3;
    private CheckBox option4;
    private CheckBox option5;
    private CheckBox option6;
    private CheckBox option7;
    private CheckBox option8;
    private CheckBox option9;
    private CheckBox option10;
    private CheckBox option11;
    private CheckBox option12;
    private CheckBox option13;
    private CheckBox option14;
    private CheckBox option15;

    private ProgressBar progressBar;

    ApiCaller caller;

    private PersonnalisationContract personnalisationContract;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.reg_personnalisation2);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedCoaching = extras.getInt("COACHING_NUM", 0);
        }

        ((ImageView) findViewById(R.id.backButton)).setVisibility(View.INVISIBLE);

        option1 = (CheckBox)findViewById(R.id.pers2_option1_cb);
        option2 = (CheckBox)findViewById(R.id.pers2_option2_cb);
        option3 = (CheckBox)findViewById(R.id.pers2_option3_cb);
        option4 = (CheckBox)findViewById(R.id.pers2_option4_cb);
        option5 = (CheckBox)findViewById(R.id.pers2_option5_cb);
        option6 = (CheckBox)findViewById(R.id.pers2_option6_cb);

            option7 = (CheckBox) findViewById(R.id.pers2_option7_cb);
            option8 = (CheckBox) findViewById(R.id.pers2_option8_cb);
            option9 = (CheckBox) findViewById(R.id.pers2_option9_cb);
            option10 = (CheckBox) findViewById(R.id.pers2_option10_cb);
            option11 = (CheckBox) findViewById(R.id.pers2_option11_cb);
            option12 = (CheckBox) findViewById(R.id.pers2_option12_cb);
            option13 = (CheckBox) findViewById(R.id.pers2_option13_cb);
            option14 = (CheckBox) findViewById(R.id.pers2_option14_cb);
            option15 = (CheckBox) findViewById(R.id.pers2_option15_cb);


//        progressBar = (ProgressBar)findViewById(R.id.progressBar_pers2);
//
//        personnalisationContract = new PersonnalisationContract();
//        personnalisationContract.CoachingProfile = selectedCoaching;
//        if(BuildConfig.FLAVOR == "JMC"){
//            personnalisationContract.CalorieLevel = 3;
//        }else{
//            personnalisationContract.CalorieLevel = 1;
//        }
//
//
//        caller = new ApiCaller();

    }

    @Override
    public void onClick(View v) {

    }

    public void submitPersonnalisation(View v) {
//        System.out.println("continue reg" );
//        personnalisationContract.MealProfile = selectedMealProfile;
//
//
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                progressBar.setVisibility(View.VISIBLE);
//            }
//        });
//        caller.PostPersonnalisation(new AsyncResponse() {
//            @Override
//            public void processFinish(Object output) {
//                Log.d("PostPersonnalisation", output.toString());
//                PersonnalisationContract responseContract = (PersonnalisationContract) output;
//
//                if (responseContract.Message.equalsIgnoreCase("Successful")){
//                    goToWelcomePage();
//                }
//            }
//        }, ApplicationData.getInstance().regUserProfile.getRegId(), personnalisationContract);
//
//        System.out.println("continue " );

        Intent personnalisation3 = new Intent(getApplicationContext(), RegCalorieLevelActivity.class);
        personnalisation3.putExtra("COACHING_PROFILE", selectedMealProfile);
        personnalisation3.putExtra("COACHING_NUM", selectedCoaching);
        startActivity(personnalisation3);
    }

    public void selectMealPlan(View v){
        System.out.println("select Meal Plan " + v.getTag());
        selectMealPlanBtnSM(Integer.parseInt(v.getTag().toString()));

    }



    public void goBackToPreviousPage(View view) {
        finish();
    }

    private void selectMealPlanBtn(int n){
        selectedMealProfile = n;

        option1.setChecked(false);
        option2.setChecked(false);
        option3.setChecked(false);
        option4.setChecked(false);
        option5.setChecked(false);
        option6.setChecked(false);


        switch (n){
            case 1:
                option1.setChecked(true);
                break;
            case 2:
                option5.setChecked(true);
                break;
            case 3:
                option6.setChecked(true);
                break;
            case 5:
                option2.setChecked(true);
                break;
            case 6:
                option4.setChecked(true);
                break;
            case 12:
                option3.setChecked(true);
                break;
            default: {
                selectedMealProfile = 1;
                option1.setChecked(true);
                break;
            }

        }

    }

    private void selectMealPlanBtnSM(int n){

        /*Classique - 1
        Menu Rapide et Economique : 10
        Végétarien : 7
    Sans petit déjeuner : 9
    Brunch et dîner : 12
    Sans dîner : 13
    Sans déjeuner : 15
    Sans porc : 6
    Spécial colopathie : 5
    Sans gluten : 8
    Sans lait de vache : 2
    Sans laitage mais avec fromage : 4
    Sans laitage et sans fromage : 3*/

        selectedMealProfile = n;

        option1.setChecked(false);
        option2.setChecked(false);
        option3.setChecked(false);
        option4.setChecked(false);
        option5.setChecked(false);
        option6.setChecked(false);
        option7.setChecked(false);
        option8.setChecked(false);
        option9.setChecked(false);
        option10.setChecked(false);
        option11.setChecked(false);
        option12.setChecked(false);
        option13.setChecked(false);
        option14.setChecked(false);
        option15.setChecked(false);

        switch (n){
            case 1:
                option1.setChecked(true);
                break;
            case 2:
                selectedMealProfile = 10;
                option2.setChecked(true);
                break;
            case 3:
                selectedMealProfile = 7;
                option3.setChecked(true);
                break;
            case 4:
                selectedMealProfile = 9;
                option4.setChecked(true);
                break;
            case 5:
                selectedMealProfile = 12;
                option5.setChecked(true);
                break;
            case 6:
                selectedMealProfile = 13;
                option6.setChecked(true);
                break;
            case 7:
                selectedMealProfile = 15;
                option7.setChecked(true);
                break;
            case 8:
                selectedMealProfile = 6;
                option8.setChecked(true);
                break;
            case 9:
                selectedMealProfile = 5;
                option9.setChecked(true);
                break;
            case 10:
                selectedMealProfile = 8;
                option10.setChecked(true);
                break;
            case 11:
                selectedMealProfile = 2;

                option11.setChecked(true);
                break;
            case 12:
                selectedMealProfile = 4;
                option12.setChecked(true);
                break;
            case 13:
                selectedMealProfile = 3;
                option13.setChecked(true);
                break;
            case 14:
                selectedMealProfile = 16;
                option14.setChecked(true);
                break;
            case 15:
                selectedMealProfile = 17;
                option15.setChecked(true);
                break;
            default:
                selectedMealProfile = 1;
                option1.setChecked(true);
                break;

        }

        System.out.println("select Meal Plan assigned " + selectedMealProfile);
    }
}
