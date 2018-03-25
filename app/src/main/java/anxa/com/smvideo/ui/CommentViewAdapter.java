package anxa.com.smvideo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.MessagesAccountFragment;
import anxa.com.smvideo.connection.listener.MainActivityCallBack;
import anxa.com.smvideo.contracts.MessagesContract;
import anxa.com.smvideo.util.AppUtil;


public class CommentViewAdapter extends ArrayAdapter<MessagesContract> implements View.OnClickListener {

    private Context context;
    private List<MessagesContract> items;
    private LayoutInflater layoutInflater;
    private String inflater = Context.LAYOUT_INFLATER_SERVICE;
    private String currentDate;
    private MainActivityCallBack MainListener;

    private int coachId = 0;

    public CommentViewAdapter(Context context,
                              List<MessagesContract> items,
                              MainActivityCallBack MainListener) {
        super(context, R.layout.comment_item, items);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.MainListener = MainListener;
    }

    public void update(List<MessagesContract> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

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
            MessagesContract message = items.get(position);

            //download profile image
            String proFileImageURL = AppUtil.BuildProfilePicUrl(context.getResources().getString(R.string.profile_pic_url), Integer.toString(ApplicationData.getInstance().regId));

            //download profile pic
            new DownloadImageTask((ImageView) viewHolder.imageView)
                    .execute(proFileImageURL);

//            proFileImageURL = AppUtil.BuildProfilePicUrl(context.getResources().getString(R.string.profile_pic_url), Integer.toString(message.CoachId));

            Bitmap avatar = null;

            viewHolder.imageView.setTag(message.DateCreated);

            String date = AppUtil.getMonthDay(AppUtil.toDate(message.DateCreated));
            String time = AppUtil.getTimeOnly24(AppUtil.toDate(message.DateCreated).getTime());

            if (currentDate == null) {
                currentDate = date;
                row.findViewById(R.id.datecontainer).setVisibility(View.VISIBLE);
                viewHolder.chatDate.setText(date);

            } else if (currentDate.contentEquals(date)) {
                if (items.size() > 1 && position > 0) {
                    if (AppUtil.getMonthDay(AppUtil.toDate(items.get(position - 1).DateCreated)).equalsIgnoreCase(date)) {
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

            coachId = message.CoachRegNo;
            viewHolder.imageView.setTag(coachId);

            if (message.CoachId > 0) { //coach comment use coach comment here

                viewHolder.chatMessage_user.setVisibility(View.GONE);
                viewHolder.chatMessage.setVisibility(View.VISIBLE);

                avatar = getAvatar(message.CoachRegNo);
                proFileImageURL = AppUtil.BuildProfilePicUrl(context.getResources().getString(R.string.profile_pic_url), Integer.toString(message.CoachRegNo));

                LayoutParams params1 = (LayoutParams) (((RoundedImageView) row.findViewById(R.id.chat_avatar))).getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                LayoutParams params2 = (LayoutParams) row.findViewById(R.id.timestamp).getLayoutParams();
                params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                LayoutParams params3 = (LayoutParams) row.findViewById(R.id.chat_message_cont).getLayoutParams();
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

                viewHolder.chatMessage.setText(Html.fromHtml(message.MessageChat));
                viewHolder.chatMessage.setLinksClickable(true);
//                viewHolder.chatMessage.setMovementMethod(MovementCheck.getInstance(context));

            } else {
                avatar = getAvatar(0);
                viewHolder.chatMessage.setVisibility(View.GONE);
                viewHolder.chatMessage_user.setVisibility(View.VISIBLE);

                LayoutParams params1 = (LayoutParams) row.findViewById(R.id.chat_avatar).getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

                LayoutParams params2 = (LayoutParams) row.findViewById(R.id.timestamp).getLayoutParams();
                params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                LayoutParams params3 = (LayoutParams) row.findViewById(R.id.chat_message_cont).getLayoutParams();
                params3.addRule(RelativeLayout.RIGHT_OF, R.id.timestamp);
                params3.addRule(RelativeLayout.LEFT_OF, R.id.chat_avatar);

                row.findViewById(R.id.chat_avatar).setLayoutParams(params1);
                row.findViewById(R.id.timestamp).setLayoutParams(params2);
                row.findViewById(R.id.chat_message_cont).setLayoutParams(params3);
                row.findViewById(R.id.chat_message_user).setBackgroundResource(R.drawable.comment_bubble_user_inverted);
                viewHolder.imageView.setImageBitmap(avatar);

                viewHolder.chatMessage_user.setText(Html.fromHtml(message.MessageChat));
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

                if (!ApplicationData.getInstance().coachPhotosList.containsKey(coachIdToSave) && mIcon11 != null) {
                    ApplicationData.getInstance().coachPhotosList.put(coachIdToSave, mIcon11);
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
            avatarBMP = ApplicationData.getInstance().coachPhotosList.get(String.valueOf(coachId));

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
    }

    @Override
    public void onClick(View v) {
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            ApplicationData.getInstance().setUserProfilePhoto(mIcon11);

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}