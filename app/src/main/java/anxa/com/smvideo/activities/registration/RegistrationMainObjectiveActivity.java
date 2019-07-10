package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.models.RegUserProfile;

public class RegistrationMainObjectiveActivity extends Activity {
    Button loseWeightBtn, eatHealthierBtn, getMoreFitBtn;
    RegUserProfile userProfile;

    int LOSE_WEIGHT_GOALS_INDEX = 1;
    int EAT_HEALTHIER_GOALS_INDEX = 7;
    int GET_MORE_FIT_GOALS_INDEX = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.reg_main_objective);

        loseWeightBtn = (Button) findViewById(R.id.reg_goal_loseweight);
        eatHealthierBtn = (Button) findViewById(R.id.reg_goal_eathealthier);
        getMoreFitBtn = (Button) findViewById(R.id.reg_goal_getmorefit);

        //create new instance of UserProfile, to save the goals_index
        userProfile = new RegUserProfile();
    }

    @Override
    public void onResume() {
        super.onResume();

        //create new instance of UserProfile, to save the goals_index
        userProfile = new RegUserProfile();
    }


    public void setMainObjective(View view) {
        if (view == loseWeightBtn) {
            //set goal to lose weight, goals_index = 1
            userProfile.setObjective(Integer.toString(LOSE_WEIGHT_GOALS_INDEX));
            goToStartingWeight();
        } else if (view == eatHealthierBtn) {
            //set goal to eat healthier, goals_index = 7
            userProfile.setObjective(Integer.toString(EAT_HEALTHIER_GOALS_INDEX));
            goToMotivationScreen();
        } else if (view == getMoreFitBtn) {
            //set goal to get more Fit, goals_index = 8
            userProfile.setObjective(Integer.toString(GET_MORE_FIT_GOALS_INDEX));
            goToMotivationScreen();
        }
    }

    private void goToStartingWeight() {
        ApplicationData.getInstance().regUserProfile = userProfile;
        Intent mainIntent = new Intent(this, RegistrationStartWeightActivity.class);
        this.startActivity(mainIntent);
    }

    private void goToMotivationScreen() {
        ApplicationData.getInstance().regUserProfile = userProfile;
        Intent mainIntent = new Intent(this, RegistrationMotivationActivity.class);
        this.startActivity(mainIntent);
    }

    public void goBackToPreviousPage(View view) {
        finish();
    }
}
