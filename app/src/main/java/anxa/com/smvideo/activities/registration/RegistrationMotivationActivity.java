package anxa.com.smvideo.activities.registration;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.models.RegUserProfile;

public class RegistrationMotivationActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
    /*Init the variables*/
    private TextView questionLabel;

    private final int MIN_MOTIVATION_VALUE = 0;
    private final int DEFAULT_MOTIVATION_VALUE = 5;
    private final int FONT_SIZE = 50;

    private int motivationValue;

    private SeekBar seekBarTop;
    private SeekBar seekBarBottom;
    private RegUserProfile userProfile;

    private Button footerButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_motivation);

        userProfile = ApplicationData.getInstance().regUserProfile;

        footerButton = (Button) findViewById(R.id.footerbutton);
        footerButton.setText(getString(R.string.REG_GOAL_CONTINUE_BTN));
        footerButton.setOnClickListener(this);
        questionLabel = (TextView) findViewById(R.id.reg_questionLabel);

        if (userProfile.getInitialWeight() <=0) {
            String labelText = "1/2 " + getResources().getString(R.string.REG_QUESTION_MOTIVATION_LEVEL);
            questionLabel.setText(labelText);
        } else {
            String labelText = "3/4 " + getResources().getString(R.string.REG_QUESTION_MOTIVATION_LEVEL);
            questionLabel.setText(labelText);
        }

        seekBarTop = (SeekBar) findViewById(R.id.seekBarTop);
        seekBarBottom = (SeekBar) findViewById(R.id.seekBarBottom);

        seekBarBottom.setOnSeekBarChangeListener(this);
        seekBarTop.setOnSeekBarChangeListener(this);

        seekBarTop.setProgress(DEFAULT_MOTIVATION_VALUE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.footerbutton) {
            SharedPreferences sharedPreferences = getSharedPreferences("com.anxacoaching", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("motivation_level", Integer.toString(motivationValue));
            editor.commit();

            userProfile.setMotivation(Integer.toString(motivationValue));
            ApplicationData.getInstance().regUserProfile = userProfile;

            Intent i = new Intent(getApplicationContext(), RegistrationEngagementActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub

        if (seekBar == seekBarTop) {
            seekBarBottom.setProgress(progress);
        } else {
            seekBarTop.setProgress(progress);
        }

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slider_value);
        Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas c = new Canvas(bmp);

        Paint p = new Paint();
        p.setTypeface(Typeface.DEFAULT_BOLD);

        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                FONT_SIZE, getResources().getDisplayMetrics());

        p.setTextSize(pixel);
        p.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_orange));
        String text = Integer.toString(seekBar.getProgress() + MIN_MOTIVATION_VALUE);
        int width = (int) p.measureText(text);
        int yPos = (int) ((c.getHeight() / 2) - ((p.descent() + p.ascent()) / 2));
        c.drawText(text, (bmp.getWidth() - width) / 2, yPos - 5, p);

        motivationValue = seekBar.getProgress() + MIN_MOTIVATION_VALUE;
        seekBarTop.setThumb(new BitmapDrawable(getResources(), bmp));

        Double d = seekBarTop.getMeasuredWidth() * 0.105;
        int i = d.intValue();

        seekBarTop.setPadding(i, 0, i, 0);
    }


    public void goBackToPreviousPage(View view) {
        finish();
    }

}