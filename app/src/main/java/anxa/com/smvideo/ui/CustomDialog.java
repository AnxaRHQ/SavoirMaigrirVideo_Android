package anxa.com.smvideo.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import anxa.com.smvideo.R;


/**
 * Created by aprilanxa on 29/11/2016.
 */

/** Implement onClickListener to dismiss dialog when OK Button is pressed */
public class CustomDialog extends Dialog {

    TextView otherButton;
    TextView yesButton;
    TextView noButton;

    public CustomDialog(Context context,
                        String otherButtonText,
                        String yesButtonText,
                        String noButtonText,
                        Boolean isClose,
                        String message,
                        String title,
                        android.view.View.OnClickListener listener) {
        super(context);
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        /** Design the dialog in main.xml file */

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setContentView(R.layout.dialog);

        if (title == null)
            findViewById(R.id.title_container).setVisibility(View.GONE);
        else {
            findViewById(R.id.title_container).setVisibility(View.VISIBLE);
        }

        ((TextView) findViewById(R.id.title)).setText(title);
        ((TextView) findViewById(R.id.messagedetail)).setText(message);

        if (!isClose) { //hide close button
            findViewById(R.id.CloseButton).setVisibility(View.GONE);
        } else {
            findViewById(R.id.CloseButton).setVisibility(View.VISIBLE);
            findViewById(R.id.CloseButton).setOnClickListener(listener);
        }

        otherButton = (TextView) findViewById(R.id.OtherButton);
        yesButton = (TextView) findViewById(R.id.YesButton);
        noButton = (TextView) findViewById(R.id.NoButton);


        if (otherButtonText != null) {
            otherButton.setOnClickListener(listener);
            otherButton.setText(otherButtonText);
            otherButton.setVisibility(View.VISIBLE);

            yesButton.setVisibility(View.GONE);
            noButton.setVisibility(View.GONE);
            return;
        }


        otherButton.setVisibility(View.GONE);

        if (yesButtonText == null || yesButtonText.length() == 0) {
            yesButton.setVisibility(View.GONE);
        } else {
            yesButton.setOnClickListener(listener);
            yesButton.setText(yesButtonText);
        }


        if (noButtonText == null || noButtonText.length() == 0) {
            noButton.setVisibility(View.GONE);
        } else {
            noButton.setOnClickListener(listener);
            noButton.setText(noButtonText);
        }
    }
}
