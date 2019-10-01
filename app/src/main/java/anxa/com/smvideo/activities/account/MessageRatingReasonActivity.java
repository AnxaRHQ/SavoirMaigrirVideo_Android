package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.MainActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.MessageRatingContract;

/**
 * Created by angelaanxa on 3/28/2018.
 */

public class MessageRatingReasonActivity extends Activity {
    CheckBox cbRatingAutres;
    CheckBox cbRatingConseils;
    CheckBox cbRatingTemps;
    CheckBox cbRatingQualite;
    EditText etReason;
    TextView tvEvalIntro;
    int questionId = 0;

    private ApiCaller caller;

    public void onCreate(Bundle savedInstanceState)
    {
       super.onCreate(savedInstanceState);

        caller = new ApiCaller();

        setContentView(R.layout.messageratingreason);

        cbRatingAutres =  ((CheckBox)findViewById(R.id.cbRatingAutres));
        cbRatingConseils =  ((CheckBox)findViewById(R.id.cbRatingConseils));
        cbRatingTemps =  ((CheckBox)findViewById(R.id.cbRatingTemps));
        cbRatingQualite =  ((CheckBox)findViewById(R.id.cbRatingQualite));
        etReason =  ((EditText)findViewById(R.id.etReason));
        tvEvalIntro = ((TextView)findViewById(R.id.tvEvalIntro));

        tvEvalIntro.setText(Html.fromHtml(getResources().getString(R.string.messages_low_rating_intro)));


        questionId = getIntent().getIntExtra("QUESTIONID",0);
    }

    public void showReason(View view)
    {
        switch (view.getId()) {
            case R.id.cbRatingAutres:
                if (cbRatingAutres.isChecked()) {
                    etReason.setVisibility(View.VISIBLE);
                }else{
                    etReason.setVisibility(View.GONE);
                }
                break;

            default:
        }
    }

    public void dismissPage(View view)
    {
        if(questionId > 0)
        {
            String comment = "";
            if(cbRatingConseils.isChecked())
            {
                comment = comment + getResources().getString(R.string.message_rating_conseils) + "\r\n";
            }
            if(cbRatingTemps.isChecked())
            {
                comment = comment + getResources().getString(R.string.message_rating_temps) + "\r\n";
            }
            if(cbRatingQualite.isChecked())
            {
                comment = comment + getResources().getString(R.string.message_rating_qualite) + "\r\n";
            }
            if(tvEvalIntro.getText().length() > 0)
            {
                comment = comment + etReason.getText();
            }
            MessageRatingContract contract = new MessageRatingContract();
            contract.QuestionId = questionId;
            contract.RatingComment = comment;
            caller.PostRating(new AsyncResponse() {
                @Override
                public void processFinish(Object output) {

                    setResult(MessageRatingReasonActivity.RESULT_OK, getIntent());

                    finish();

                }
            }, Integer.toString(ApplicationData.getInstance().userDataContract.Id), contract);
        }

        //finish();
    }
}