package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.activities.NpnaOfferActivity;
import anxa.com.smvideo.common.CommonConstants;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.Carnet.MoodContract;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataContract;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataResponseContract;
import anxa.com.smvideo.R;
import anxa.com.smvideo.util.AppUtil;

public class MoodAddActivity extends Activity implements View.OnClickListener
{
    ImageView backButton;

    Button isHungryYes;
    Button isHungryNo;
    TextView descriptionCount;
    EditText desc;
    Button submitButton;

    List<ImageButton> moodList;
    MoodContract currentMood;

    int descRemainCount = 250;
    final int MAX_DESC = 250;
    String moodStatus;

    private ApiCaller apiCaller;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        System.out.println("MoodAddActivity onCreate:");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.mood_add);

        ((TextView) findViewById(R.id.header_title_tv)).setText(String.format(getString(R.string.HAPIMOMENT_TITLE), AppUtil.getFRMonthDay(ApplicationData.getInstance().currentSelectedDate)));
        backButton = (ImageView) findViewById(R.id.header_menu_back);
        backButton.setOnClickListener(this);

        ((Button) findViewById(R.id.header_menu_iv)).setVisibility(View.GONE);

        apiCaller = new ApiCaller();

        moodList = new ArrayList<ImageButton>();
        moodList.add((ImageButton) findViewById(R.id.mood1));
        moodList.add((ImageButton) findViewById(R.id.mood2));
        moodList.add((ImageButton) findViewById(R.id.mood3));
        moodList.add((ImageButton) findViewById(R.id.mood4));
        moodList.add((ImageButton) findViewById(R.id.mood5));

        ((ImageButton) findViewById(R.id.mood1)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.mood2)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.mood3)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.mood4)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.mood5)).setOnClickListener(this);

        moodList.get(0).setSelected(true);
        moodList.get(0).setBackgroundResource(R.drawable.rounded_button_orange_withborder);

        isHungryYes = (Button) findViewById(R.id.hungryYes);
        isHungryNo = (Button) findViewById(R.id.hungryNo);

        isHungryNo.setOnClickListener(this);
        isHungryYes.setOnClickListener(this);

        desc = ((EditText) findViewById(R.id.desc));
        descriptionCount = ((TextView) findViewById(R.id.descCount));
        updateMoodDescCount();

        desc.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after)
            {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                // TODO Auto-generated method stub
                descRemainCount = MAX_DESC - s.length();
                updateMoodDescCount();
            }
        });

        submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);

        moodStatus = getIntent().getStringExtra("MOOD_STATUS");

        if (moodStatus.equalsIgnoreCase(CommonConstants.COMMAND_ADDED))
        {
            currentMood = new MoodContract();
            currentMood.CreationDate = ApplicationData.getInstance().currentSelectedDate.getTime() / 1000;
            currentMood.MoodType = 1;
            currentMood.Command = CommonConstants.COMMAND_ADDED;

            if (currentMood.IsHungry == null)
            {
                currentMood.IsHungry = false;
            }
        }

        if (moodStatus.equalsIgnoreCase(CommonConstants.COMMAND_UPDATED))
        {
            currentMood = new MoodContract();
            currentMood = ApplicationData.getInstance().currentMood;
            currentMood.Command = CommonConstants.COMMAND_UPDATED;
            updateMood(currentMood.MoodType);
            updateIsHungryButton(currentMood.IsHungry != null ? currentMood.IsHungry : false);
            desc.setText(currentMood.Message);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == backButton)
        {
            finish();
        }
        else if (v.getId() == R.id.mood1)
        {
            updateMood(1);
        }
        else if (v.getId() == R.id.mood2)
        {
            updateMood(2);
        }
        else if (v.getId() == R.id.mood3)
        {
            updateMood(3);
        }
        else if (v.getId() == R.id.mood4)
        {
            updateMood(4);
        }
        else if (v.getId() == R.id.mood5)
        {
            updateMood(5);
        }
        else if (v.getId() == R.id.hungryYes)
        {
            updateIsHungryButton(true);
        }
        else if (v.getId() == R.id.hungryNo)
        {
            updateIsHungryButton(false);
        }
        else if (v == submitButton)
        {
            if (!CheckFreeUser(true))
            {
                //submit button
                processSubmit();
            }
        }
    }

    public void updateMood(int moodValue)
    {
        for (int i = 1; i <= 5; i++)
        {
            if (i == moodValue)
            {
                moodList.get(i - 1).setSelected(true);
                moodList.get(i - 1).setBackgroundResource(R.drawable.rounded_button_orange_withborder);
                currentMood.MoodType = moodValue;
            }
            else
            {
                moodList.get(i - 1).setSelected(false);
                moodList.get(i - 1).setBackgroundResource(0);
                currentMood.MoodType = moodValue;
            }
        }
    }

    public void updateIsHungryButton(boolean isHungry)
    {
        if (isHungry)
        {
            isHungryYes.setBackgroundResource(R.drawable.rounded_button_orange_withborder);
            isHungryNo.setBackgroundResource(R.drawable.rounded_button_white_orangeborder);
            currentMood.IsHungry = true;
        }
        else
        {
            isHungryNo.setBackgroundResource(R.drawable.rounded_button_orange_withborder);
            isHungryYes.setBackgroundResource(R.drawable.rounded_button_white_orangeborder);
            currentMood.IsHungry = false;
        }
    }

    private void processSubmit()
    {
        if (desc.getText() != null)
        {
            currentMood.Message = desc.getText().toString();
        }

        ApplicationData.getInstance().currentMood = currentMood;
        UploadMealsDataContract contract = new UploadMealsDataContract();
        contract.Mood.add(currentMood);

        apiCaller.PostCarnetSync(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                Log.d("PostAddMood", output.toString());
                UploadMealsDataResponseContract response = output != null ? (UploadMealsDataResponseContract) output : new UploadMealsDataResponseContract();
                if (response.Message == null) {
                    finish();
                }
                currentMood.MoodId = response.Data.Mood.get(0).MoodId;
                //broadcast the update
                Intent broadInt = new Intent();
                broadInt.setAction(getResources().getString(R.string.meallist_getmeal_week));
                sendBroadcast(broadInt);
                finish();
            }
        }, ApplicationData.getInstance().regId, contract);
    }

    private void updateMoodDescCount()
    {
        descriptionCount.setText(descRemainCount + "/" + MAX_DESC);
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
