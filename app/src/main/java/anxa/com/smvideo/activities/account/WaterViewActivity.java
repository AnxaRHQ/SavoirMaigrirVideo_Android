package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.Date;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.activities.NpnaOfferActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataContract;
import anxa.com.smvideo.contracts.Carnet.WaterContract;
import anxa.com.smvideo.util.AppUtil;

import anxa.com.smvideo.R;

/**
 * Created by aprilanxa on 20/09/2016.
 */

public class WaterViewActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener
{
    ImageView backButton;

    int FONT_SIZE = 18;
    String waterStatus;

    WaterContract currentWater;
    SeekBar glassSeekBar;
    SeekBar bubbleSeekBar;
    ProgressBar waterProgressBar;
    TextView waterDate;
    WaterContract waterEntryToUpload;

    private final String COMMAND_ADDED = "Added";
    private final String COMMAND_UPDATED = "Updated";
    private final String COMMAND_DELETED = "Deleted";

    protected ApiCaller caller;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.water_view);

        waterStatus = getIntent().getStringExtra("WATER_STATUS");

        caller = new ApiCaller();

        System.out.println("add water waterStatus: " + waterStatus);

        ((TextView) findViewById(R.id.header_title_tv)).setText(getString(R.string.MEALTYPE_WATER));
        backButton = (ImageView) findViewById(R.id.header_menu_back);
        backButton.setOnClickListener(this);

        ((Button) findViewById(R.id.header_menu_iv)).setVisibility(View.GONE);

        glassSeekBar = (SeekBar) findViewById(R.id.seekBar_water);
        glassSeekBar.setOnSeekBarChangeListener(this);
        bubbleSeekBar = (SeekBar) findViewById(R.id.seekBar_water_thumb);
        bubbleSeekBar.setOnSeekBarChangeListener(this);
        waterProgressBar = (ProgressBar) findViewById(R.id.waterProgressBar);

        waterDate = (TextView) findViewById(R.id.waterViewTitle);

        if (waterStatus.equalsIgnoreCase("view"))
        {
            currentWater = ApplicationData.getInstance().currentWater;
            refreshUI();
        }
        else
        {
            waterDate.setText(AppUtil.getDateinString(ApplicationData.getInstance().currentSelectedDate));
            glassSeekBar.setProgress(0);
            bubbleSeekBar.setProgress(0);
            updateWaterThumb(0);
        }
    }

    public void saveWaterEntry(View v)
    {
        if (!CheckFreeUser(true))
        {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    waterProgressBar.setVisibility(View.VISIBLE);
                }
            });

            if (waterStatus.equalsIgnoreCase("add")) {
                addWaterToAPI();
            } else {
                updateWaterToAPI();
            }
        }
    }

    public void onClick(View v)
    {
        if (v == backButton)
        {
            finish();
        }
    }

    /**
     * SeekBar Listener
     **/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser)
    {
        if (seekBar == bubbleSeekBar) {
            glassSeekBar.setProgress(progress);
        } else if (seekBar == glassSeekBar) {
            bubbleSeekBar.setProgress(progress);
        }

        updateWaterThumb(progress);
    }

    /**
     * Private Methods
     **/
    private void refreshUI()
    {
        int currentProgress = currentWater.NumberOfGlasses;
        glassSeekBar.setProgress(currentProgress);
        bubbleSeekBar.setProgress(currentProgress);

        updateWaterThumb(currentProgress);

        waterDate.setText(AppUtil.getDateinString(AppUtil.toDateGMT(currentWater.CreationDate)));
    }

    private void addWaterToAPI()
    {

        waterEntryToUpload = new WaterContract();
        waterEntryToUpload.NumberOfGlasses = glassSeekBar.getProgress();

        sendWaterToAPI(COMMAND_ADDED);
    }

    private void updateWaterToAPI()
    {
        waterEntryToUpload = currentWater;
        waterEntryToUpload.NumberOfGlasses = glassSeekBar.getProgress();

        int index = -1;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        for (WaterContract p : ApplicationData.getInstance().waterList) {
            Date waterDate = AppUtil.toDate(p.CreationDate);
            if (fmt.format(currentWater.CreationDate).equals(fmt.format(waterDate))) {
                index = ApplicationData.getInstance().waterList.indexOf(p);
                break;
            }
        }
        if (index > -1)
        {
            ApplicationData.getInstance().waterList.set(index, waterEntryToUpload);
        }

        sendWaterToAPI(COMMAND_UPDATED);
    }

    private void sendWaterToAPI(String command)
    {
        try
        {
            if (command.equalsIgnoreCase(COMMAND_ADDED))
                waterEntryToUpload.CreationDate = AppUtil.dateToUnixTimestamp(ApplicationData.getInstance().currentSelectedDate);

            //upload waterEntryToUpload
            UploadMealsDataContract contract = new UploadMealsDataContract();
            waterEntryToUpload.Command = command;
            contract.Water.add(waterEntryToUpload);

            caller.PostCarnetSync(new AsyncResponse()
            {
                @Override
                public void processFinish(Object output)
                {
                    Log.d("PostWater", output.toString());

                    //broadcast the update
                    Intent broadInt = new Intent();
                    broadInt.setAction(getResources().getString(R.string.meallist_getmeal_week));
                    sendBroadcast(broadInt);

                    finish();
                }
            }, ApplicationData.getInstance().regId, contract);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateWaterThumb(int waterProgress)
    {
        glassSeekBar.setProgress(waterProgress);

        Bitmap bitmap;

        if (waterProgress == 0) {
            glassSeekBar.setThumb(getResources().getDrawable(R.drawable.water_glass_gray));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_bubble_gray);
        } else {
            glassSeekBar.setThumb(getResources().getDrawable(R.drawable.water_glass_blue));
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_bubble_orange);
        }

        Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(bmp);

        Paint p = new Paint();
        p.setTypeface(Typeface.DEFAULT_BOLD);

        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                FONT_SIZE, getResources().getDisplayMetrics());

        p.setTextSize(pixel);
        p.setColor(getResources().getColor(R.color.text_white));
        String text = Integer.toString(waterProgress);
        int width = (int) p.measureText(text);
        int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
        c.drawText(text, (bmp.getWidth() - width) / 2, yPos - 2, p);

        bubbleSeekBar.setThumb(new BitmapDrawable(getResources(), bmp));

//        Double d = bubbleSeekBar.getMeasuredWidth() * 0.025;
//        int i = d.intValue();
//
//        bubbleSeekBar.setPadding(i, 0, i, 0);
//        glassSeekBar.setPadding(i, 0, i, 0);
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
