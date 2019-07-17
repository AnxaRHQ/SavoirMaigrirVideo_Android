package anxa.com.smvideo.activities.account;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;

public class BaseFragment extends Fragment {
    View mView;
    protected void removeFragment()
    {
        Fragment fragment = new LandingPageAccountActivity();

        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Home;
        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT")).commit();
        } else {
        }

        try {

            fragmentManager.beginTransaction().replace(R.id.mainContent, fragment, "CURRENT_FRAGMENT").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        ApplicationData.getInstance().currentLiveWebinar = "";

        RelativeLayout banner = (RelativeLayout) mView.findViewById(R.id.bannerWebinar);
        if (banner != null) {
            (banner).setVisibility(View.GONE);
           /* if ((TextView) mView.findViewById(R.id.textLive) != null) {
                ((TextView) mView.findViewById(R.id.textLive)).setVisibility(View.GONE);
                if((RelativeLayout)mView.findViewById(R.id.textLiveWrapper) != null){
                    ((RelativeLayout)mView.findViewById(R.id.textLiveWrapper)).setVisibility(View.GONE);

                }
            }*/
            TimeZone tz = TimeZone.getTimeZone("Europe/Paris");

            //8am consultation
            Calendar calendar1 = Calendar.getInstance(tz);
            calendar1.set(Calendar.HOUR_OF_DAY, 8);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);


            Calendar calendar2 = Calendar.getInstance(tz);
            calendar2.set(Calendar.HOUR_OF_DAY, 8);
            calendar2.set(Calendar.MINUTE, 30);
            calendar2.set(Calendar.SECOND, 0);

            Calendar calendar3 = Calendar.getInstance(tz);
            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                if (calendar3.get(Calendar.DAY_OF_WEEK) >= 2 && calendar3.get(Calendar.DAY_OF_WEEK) <= 6) {
                    (banner).setVisibility(View.VISIBLE);

                    ((TextView) ((RelativeLayout) mView.findViewById(R.id.bannerWebinar)).findViewById(R.id.banner_text)).setText(Html.fromHtml(getResources().getString(R.string.banner_webinar_lapause)));
                    switch (calendar3.get(Calendar.DAY_OF_WEEK)) {
                        case 2:
                            ApplicationData.getInstance().currentLiveWebinar = "LaPauseCafe";
                            break;
                        default:
                            ApplicationData.getInstance().currentLiveWebinar = "CommentUtiliserSonEspaceMinceur";
                    }

                  /*  if ((TextView) mView.findViewById(R.id.textLive) != null) {
                        ((TextView) mView.findViewById(R.id.textLive)).setVisibility(View.VISIBLE);
                        if((RelativeLayout)mView.findViewById(R.id.textLiveWrapper) != null){
                            ((RelativeLayout)mView.findViewById(R.id.textLiveWrapper)).setVisibility(View.VISIBLE);

                        }
                        Animation anim = new AlphaAnimation(0.0f, 1.0f);
                        anim.setDuration(500); //You can manage the blinking time with this parameter
                        anim.setStartOffset(20);
                        anim.setRepeatMode(Animation.REVERSE);
                        anim.setRepeatCount(Animation.INFINITE);
                        ((TextView) findViewById(R.id.textLive)).startAnimation(anim);
                    }
                }else{
                    ApplicationData.getInstance().currentLiveWebinar = "";
                }*/
                }
            }


                //1pm consultation
                Calendar calendar4 = Calendar.getInstance(tz);
                calendar4.set(Calendar.HOUR_OF_DAY, 13);
                calendar4.set(Calendar.MINUTE, 0);
                calendar4.set(Calendar.SECOND, 0);


                Calendar calendar5 = Calendar.getInstance(tz);
                calendar5.set(Calendar.HOUR_OF_DAY, 13);
                calendar5.set(Calendar.MINUTE, 30);
                calendar5.set(Calendar.SECOND, 0);


                if (x.after(calendar4.getTime()) && x.before(calendar5.getTime())) {
                    if (calendar3.get(Calendar.DAY_OF_WEEK) >= 2 && calendar3.get(Calendar.DAY_OF_WEEK) <= 6) {
                        (banner).setVisibility(View.VISIBLE);
                     /*   if ((TextView) findViewById(R.id.textLive) != null) {
                            ((TextView) findViewById(R.id.textLive)).setVisibility(View.VISIBLE);
                            if ((RelativeLayout) findViewById(R.id.textLiveWrapper) != null) {
                                ((RelativeLayout) findViewById(R.id.textLiveWrapper)).setVisibility(View.VISIBLE);
                            }
                            switch (calendar3.get(Calendar.DAY_OF_WEEK)) {
                                case 2:
                                    ApplicationData.getInstance().currentLiveWebinar = "JeanMichelCohenEnDirect";
                                    break;
                                case 3:
                                    ApplicationData.getInstance().currentLiveWebinar = "WebinarKilosEmotionnels";
                                    break;
                                default:
                                    ApplicationData.getInstance().currentLiveWebinar = "WebinarDietetique";
                            }

                            Animation anim = new AlphaAnimation(0.0f, 1.0f);
                            anim.setDuration(500); //You can manage the blinking time with this parameter
                            anim.setStartOffset(20);
                            anim.setRepeatMode(Animation.REVERSE);
                            anim.setRepeatCount(Animation.INFINITE);
                            ((TextView) findViewById(R.id.textLive)).startAnimation(anim);
                        } else {
                            ApplicationData.getInstance().currentLiveWebinar = "";
                        }*/

                        ((TextView) (banner).findViewById(R.id.banner_text)).setText(Html.fromHtml(getResources().getString(R.string.banner_webinar_consultationdietetique)));

                    }
                }

            }


        return super.onCreateView(inflater, container, savedInstanceState);
    }


}
