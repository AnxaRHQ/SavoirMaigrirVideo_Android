package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;


/**
 * Created by aprilanxa on 13/12/2018.
 */

public class RegPersonnalisation1Activity extends Activity implements View.OnClickListener {

    private CheckBox option1;
    private CheckBox option2;
    private CheckBox option3;
    private CheckBox option4;
    private CheckBox option5;
    private CheckBox option6;

    private int selectedCoaching = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.reg_personnalisation1);
        ((ImageView) findViewById(R.id.backButton)).setVisibility(View.INVISIBLE);

        option1 = (CheckBox)findViewById(R.id.pers_option1_cb);
        option2 = (CheckBox)findViewById(R.id.pers_option2_cb);
        option3 = (CheckBox)findViewById(R.id.pers_option3_cb);
        option4 = (CheckBox)findViewById(R.id.pers_option4_cb);
        option5 = (CheckBox)findViewById(R.id.pers_option5_cb);
        option6 = (CheckBox)findViewById(R.id.pers_option6_cb);

        if(ApplicationData.getInstance().regUserProfile.getGender() == "1")
        {
            ((LinearLayout) findViewById(R.id.pers_option1_ll)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.pers_option2_ll)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.pers_option4_ll)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.pers_option5_ll)).setVisibility(View.GONE);
            ((LinearLayout) findViewById(R.id.pers_option6_ll)).setVisibility(View.GONE);
        }else{

                ((LinearLayout) findViewById(R.id.pers_option3_ll)).setVisibility(View.GONE);

        }
    }

    @Override
    public void onClick(View v) {

    }

    public void continuePersonnalisation(View v) {
        System.out.println("continue " );

        Intent personnalisation2 = new Intent(getApplicationContext(), RegPersonnalisation2Activity.class);
        personnalisation2.putExtra("COACHING_NUM", selectedCoaching);
        startActivity(personnalisation2);
    }

    public void selectCoaching(View v){
        System.out.println("selectCoaching " + v.getTag());

            selectCoachingBtnSM(Integer.parseInt(v.getTag().toString()));

    }


    private void selectCoachingBtn(int n){

        selectedCoaching = n;

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
                option2.setChecked(true);
                break;
            case 4:
                option6.setChecked(true);
                break;
            case 5:
                option4.setChecked(true);
                break;
            case 6:
                option3.setChecked(true);
                break;
            default:
                option1.setChecked(true);
                break;

        }

    }

    private void selectCoachingBtnSM(int n){
/**Classique (la version originale de Savoir Maigrir adaptée au plus grand nombre) - 1
 Difficulté à maigrir (pour les personnes qui ont suivi plusieurs régimes) - 4
 Débordé(e) (pour les personnes qui manquent de temps) - 5
 Mobilité réduite (pour les personnes qui se déplacent très peu) - 6
 Ménopause (pour les femmes ménopausées) - 2
 Sous traitement médicamenteux (pour les personnes qui prennent des médicaments) - 3*/
        selectedCoaching = n;

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
                selectedCoaching = 4;
                option2.setChecked(true);
                break;
            case 3:
                selectedCoaching = 5;
                option3.setChecked(true);
                break;
            case 4:
                selectedCoaching = 6;
                option4.setChecked(true);
                break;
            case 5:
                selectedCoaching = 2;
                option5.setChecked(true);
                break;
            case 6:
                selectedCoaching = 3;
                option6.setChecked(true);
                break;
            default:
                option1.setChecked(true);
                break;

        }

    }
}