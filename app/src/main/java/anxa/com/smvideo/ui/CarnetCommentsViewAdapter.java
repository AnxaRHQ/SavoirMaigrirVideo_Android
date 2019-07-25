package anxa.com.smvideo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.listener.MainActivityCallBack;
import anxa.com.smvideo.contracts.Carnet.MealCommentContract;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.MovementCheck;

/**
 * Created by aprilanxa on 23/01/2017.
 */

public class CarnetCommentsViewAdapter extends ArrayAdapter<MealCommentContract> implements View.OnClickListener
{
    private Context context;
    private List<MealCommentContract> items;
    private LayoutInflater layoutInflater;
    private String inflater = Context.LAYOUT_INFLATER_SERVICE;
    private String currentDate;
    private MainActivityCallBack MainListener;

    private double timeElapsed = 0, finalTime = 0;
    private int coachId = 0;
    private String durationText = "";
    MealCommentContract selectedAudioMessage;

    Boolean isSoundOn = true;
    Boolean isMediaPlaying = false;
    Boolean isMediaPlaybackCompleted = false;
    private MediaPlayer mediaPlayer;
    String currentMediaURL;
    private Handler durationHandler = new Handler();

    public CarnetCommentsViewAdapter(Context context,
                                     List<MealCommentContract> items,
                                     MainActivityCallBack MainListener)
    {
        super(context, R.layout.comment_item, items);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.MainListener = MainListener;
    }

    public void update(List<MealCommentContract> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View row = convertView;

        ViewHolder viewHolder = null;
        if (row == null) {
            LayoutInflater layoutInflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflator.inflate(R.layout.comment_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (RoundedImageView) row.findViewById(R.id.chat_avatar);
            viewHolder.chatMessage = ((TextView) row.findViewById(R.id.chat_message));
            viewHolder.chatMessage_user = ((TextView) row.findViewById(R.id.chat_message_user));
            viewHolder.chatDate = ((TextView) row.findViewById(R.id.date));
            viewHolder.chatTimestamp = ((TextView) row.findViewById(R.id.timestamp));
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        if (items != null && items.size() > 0 && getCount() > 0) {
            MealCommentContract message = items.get(position);

            String proFileImageURL = AppUtil.BuildProfilePicUrl(context.getResources().getString(R.string.profile_pic_url), Integer.toString(ApplicationData.getInstance().regId));

            Bitmap avatar = null;

            viewHolder.imageView.setTag(message.Timestamp);
            //viewHolder.audioMessage_layout.setVisibility(View.GONE);

            String date = AppUtil.getMonthDay(AppUtil.toDate(message.Timestamp));
            String time = AppUtil.getTimeOnly24Comments(AppUtil.toDate(message.Timestamp).getTime());

            if (currentDate == null) {
                currentDate = date;
                row.findViewById(R.id.datecontainer).setVisibility(View.VISIBLE);
                viewHolder.chatDate.setText(date);

            } else if (currentDate.contentEquals(date)) {
                if (items.size() > 0 && position >= 0) {
                    if (position > 0 && AppUtil.getMonthDay(AppUtil.toDate(items.get(position - 1).Timestamp)).equalsIgnoreCase(date)) {
                        row.findViewById(R.id.datecontainer).setVisibility(View.GONE);
                    } else {
                        row.findViewById(R.id.datecontainer).setVisibility(View.VISIBLE);
                        viewHolder.chatDate.setText(date);
                        currentDate = date;
                    }
                } else {
                    row.findViewById(R.id.datecontainer).setVisibility(View.GONE);
                }
            } else {
                row.findViewById(R.id.datecontainer).setVisibility(View.VISIBLE);
                viewHolder.chatDate.setText(date);
                currentDate = date;
            }

            try {
                coachId = Integer.parseInt(message.Coach.CoachProfileId);
                viewHolder.imageView.setTag(coachId);

                viewHolder.chatMessage_user.setVisibility(View.GONE);
                viewHolder.chatMessage.setVisibility(View.VISIBLE);

                avatar = getAvatar(Integer.parseInt(message.Coach.CoachProfileId));
                proFileImageURL = message.Coach.PrimaryPictureUrl;

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) (((RoundedImageView) row.findViewById(R.id.chat_avatar))).getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) row.findViewById(R.id.timestamp).getLayoutParams();
                params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) row.findViewById(R.id.chat_message_cont).getLayoutParams();
                params3.addRule(RelativeLayout.LEFT_OF, R.id.timestamp);
                params3.addRule(RelativeLayout.RIGHT_OF, R.id.chat_avatar);

                row.findViewById(R.id.chat_avatar).setLayoutParams(params1);
                row.findViewById(R.id.timestamp).setLayoutParams(params2);
                row.findViewById(R.id.chat_message_cont).setLayoutParams(params3);

                row.findViewById(R.id.chat_message).setBackgroundResource(R.drawable.comment_bubble_coach);

                if (avatar == null) {
                    new AdapterDownloadImageTask(viewHolder.imageView).execute(proFileImageURL);
                } else {
                    viewHolder.imageView.setImageBitmap(avatar);
                }

                if (message.Text != null) {
                    String str = message.Text;
                    Pattern pattern = Pattern.compile("src=\"(.*?)\"");
                    Matcher matcher = pattern.matcher(str);

                    viewHolder.audioMessage_layout.setVisibility(View.GONE);
                    viewHolder.chatMessage.setVisibility(View.VISIBLE);

                }

                Pattern pattern = Pattern.compile("<audio(.*?)</audio>");
                Matcher matcher = pattern.matcher(message.Text);
                boolean withAudio = false;
                String messageCoach = message.Text;
                while (matcher.find()) {
                    messageCoach = message.Text.replace("<audio" + matcher.group(1) + "</audio>", "");
                    withAudio = true;
                }


                viewHolder.chatMessage.setText(Html.fromHtml(message.Text));
                viewHolder.chatMessage.setLinksClickable(true);
                viewHolder.chatMessage.setMovementMethod(MovementCheck.getInstance(context));


            } catch (NullPointerException e) {

                avatar = getAvatar(0);
                viewHolder.chatMessage.setVisibility(View.GONE);
                viewHolder.chatMessage_user.setVisibility(View.VISIBLE);
                //viewHolder.audioMessage_layout.setVisibility(View.GONE);

                RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) row.findViewById(R.id.chat_avatar).getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) row.findViewById(R.id.timestamp).getLayoutParams();
                params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams) row.findViewById(R.id.chat_message_cont).getLayoutParams();
                params3.addRule(RelativeLayout.RIGHT_OF, R.id.timestamp);
                params3.addRule(RelativeLayout.LEFT_OF, R.id.chat_avatar);

                row.findViewById(R.id.chat_avatar).setLayoutParams(params1);
                row.findViewById(R.id.timestamp).setLayoutParams(params2);
                row.findViewById(R.id.chat_message_cont).setLayoutParams(params3);
                row.findViewById(R.id.chat_message_user).setBackgroundResource(R.drawable.comment_bubble_user_inverted);
                viewHolder.imageView.setImageBitmap(avatar);
                viewHolder.chatMessage_user.setText(Html.fromHtml(message.Text));
                viewHolder.chatMessage_user.setLinksClickable(true);

            }


            viewHolder.chatTimestamp.setText(time);

        }
        return row;
    }

    @Override
    public int getCount() {
        return items.size();// more than zero
    }

    @Override
    public void onClick(View v) {

    }

    private class AdapterDownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView bmImage;
        private String path;

        public AdapterDownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
            this.path = bmImage.getTag().toString();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            String coachIdToSave = urldisplay.substring(urldisplay.indexOf("users/") + 6, urldisplay.lastIndexOf("/"));

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

                if (!ApplicationData.getInstance().coachCommentsPhotosList.containsKey(coachIdToSave) && mIcon11 != null) {
                    ApplicationData.getInstance().coachCommentsPhotosList.put(coachIdToSave, mIcon11);
                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (!bmImage.getTag().toString().equals(path)) {
                return;
            }

            if (result != null && bmImage != null) {
                bmImage.setVisibility(View.VISIBLE);
                bmImage.setImageBitmap(result);

            } else {
                bmImage.setVisibility(View.GONE);
            }
        }
    }

    private Bitmap getAvatar(int coachId) {
        Bitmap avatarBMP = null;
        if (coachId > 0) {
            avatarBMP = ApplicationData.getInstance().coachCommentsPhotosList.get(String.valueOf(coachId));

            return avatarBMP;

        } else { //user comment use his image instead
            if (ApplicationData.getInstance().getUserProfilePhoto() != null)
                avatarBMP = ApplicationData.getInstance().getUserProfilePhoto();
            else if (ApplicationData.getInstance().getUserProfilePhoto() == null)
                avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_default_avatar, ApplicationData.getInstance().options_Avatar);

            if (avatarBMP == null)
                avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_default_avatar, ApplicationData.getInstance().options_Avatar);
        }

        return avatarBMP;

    }

    private static class ViewHolder {
        RoundedImageView imageView;
        TextView chatMessage;
        TextView chatMessage_user;
        TextView chatDate;
        TextView chatTimestamp;
        RelativeLayout audioMessage_layout;
    }

    //handler to change seekBarTime
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set time remaining
            double timeRemaining = finalTime - timeElapsed;

            //repeat yourself that again in 100 miliseconds
            String elapsedString = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes((long) timeElapsed), TimeUnit.MILLISECONDS.toSeconds((long) timeElapsed));
            String totalString = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()), TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()));
            durationText = elapsedString + "/" + totalString;

            notifyDataSetChanged();

            durationHandler.postDelayed(this, 100);
        }
    };

    private void playMedia(String mediaUrl) {

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();

            isMediaPlaybackCompleted = false;

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {

//                    Toast.makeText(context, "Media Completed", Toast.LENGTH_SHORT).show();
                    mediaPlayer.release();
                    mediaPlayer = null;

                    isMediaPlaying = false;
                    durationHandler.removeCallbacks(updateSeekBarTime);

                    timeElapsed = finalTime;

                    isMediaPlaybackCompleted = true;

                    notifyDataSetChanged();
                }
            });
        }


        try {
            mediaPlayer.setDataSource(mediaUrl);
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.toString());
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.toString());
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.toString());
        } catch (IOException e) {
            System.out.println("IOException: " + e.toString());
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.toString());
        }

        if (isMediaPlaying) {
            isMediaPlaying = false;

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                durationHandler.removeCallbacks(updateSeekBarTime);

            }
        } else {
            isMediaPlaying = true;

            //play the audio
            if (mediaPlayer != null) {
                mediaPlayer.start();
                durationText = "00:00";
                finalTime = mediaPlayer.getDuration();
                timeElapsed = mediaPlayer.getCurrentPosition();

                durationHandler.postDelayed(updateSeekBarTime, 100);
            }
            System.out.println("play music");
        }
    }

    private void muteMediaPlayer() {
        try {
            if (isSoundOn) {
                isSoundOn = false;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0, 0);
                }
            } else {
                isSoundOn = true;
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(1, 1);
                }
            }

        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.toString());
        } catch (SecurityException e) {
            System.out.println("SecurityException: " + e.toString());
        } catch (IllegalStateException e) {
            System.out.println("IllegalStateException: " + e.toString());
        }
    }

}
