package anxa.com.smvideo.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.common.WebkitURL;


/**
 * Created by aprilanxa on 16/12/2016.
 */

public class MovementCheck extends LinkMovementMethod {

    private static MovementCheck sInstance;
    public static String urlClicked;
    private Context context;

    public static MovementCheck getInstance(Context context) {
        if (sInstance == null)
            sInstance = new MovementCheck(context);
        return sInstance;
    }

    public MovementCheck(Context context) {
        this.context = context;
    }

    @Override
    public boolean onTouchEvent(TextView widget,
                                Spannable buffer, MotionEvent event) {
        try {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {
                    String url = link[0].getURL();
                    if (url.startsWith("https")) {
                        Log.d("Link", url);
                    } else if (url.startsWith("tel")) {
                        Log.d("Link", url);
                    } else if (url.startsWith("mailto")) {
                        Log.d("Link", url);
                    } else {
                        if (url.contains("http")){
                            urlClicked = url;
                        }else {
                            urlClicked = WebkitURL.domainURL + url;
                        }
                        ApplicationData.getInstance().urlClicked = urlClicked;
                        Log.d("Link", urlClicked);
                    }

                    Intent broadint = new Intent();
                    broadint.setAction("URL_LOAD");
                    this.context.sendBroadcast(broadint);
                    return true;
                }

                return true;
            }
            return super.onTouchEvent(widget, buffer, event);

        } catch (ActivityNotFoundException ex) {

            String errorString = ex.toString();
            if (errorString.contains("http")){
                urlClicked = errorString.substring(errorString.indexOf("dat=/") + 5, errorString.lastIndexOf("("));
            }else {
                urlClicked = WebkitURL.domainURL + errorString.substring(errorString.indexOf("dat=/") + 5, errorString.lastIndexOf("("));
            }

            ApplicationData.getInstance().urlClicked = urlClicked;

            Intent broadint = new Intent();
            broadint.setAction("URL_LOAD");
            this.context.sendBroadcast(broadint);

            return true;
        }
    }
}