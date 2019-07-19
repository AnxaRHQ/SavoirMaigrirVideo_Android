package anxa.com.smvideo.ui;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.contracts.Notifications.NotificationsContract;
import anxa.com.smvideo.util.AppUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by elaineanxa on 15/07/2019
 */

public class NotificationListAdapter extends ArrayAdapter<NotificationsContract> implements OnClickListener
{
    private final Context context;

    LayoutInflater layoutInflater;
    String inflater = Context.LAYOUT_INFLATER_SERVICE;

    OnClickListener listener;

    private List<NotificationsContract> items = new ArrayList<NotificationsContract>();
    private int Commented = 1;
    private int Validate = 2;
    private int Reminder = 3;
    private int Disapprove = 4;

    final int COLOG_BG_UNREAD_MES = Color.parseColor("#F5DEB3");
    final int COLOG_BG_READ_MES = Color.parseColor("#FFFFFF");

    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

    public NotificationListAdapter(Context context, List<NotificationsContract> items, OnClickListener listener)
    {
        super(context, R.layout.listitem_notifications, items);

        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void updateItems(List<NotificationsContract> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;

        View row = convertView;

        if (row == null)
        {
            LayoutInflater layoutInflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflator.inflate(R.layout.listitem_notifications, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (RoundedImageView) row.findViewById(R.id.imgCoachAvatar);
            viewHolder.chatMessage = ((TextView) row.findViewById(R.id.txtNotifMessage));
            viewHolder.chatMessage_icon = ((ImageView) row.findViewById(R.id.imgNotifIcon));
            viewHolder.chatDate = ((TextView) row.findViewById(R.id.txtTimestamp));
            row.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) row.getTag();
        }

        int itemCount = items.size() - position;

        if (getCount() > position && itemCount == 5) {
            System.out.println("getPosition");
        }

        NotificationsContract notification = (NotificationsContract) items.get(position);
        row.setTag(R.id.notif_refid, notification.tool_id);
        row.setTag(R.id.notif_pos, position);
        row.setTag(R.id.notif_id, notification.notification_id);
        row.setOnClickListener(this);

        viewHolder.imageView.setTag(notification.date_created_utc);

        if (notification.coach_profile_picture.equalsIgnoreCase("coachPicture"))
        {
            viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.coach_pic));
            viewHolder.chatMessage_icon.setImageResource(R.drawable.meal_commented_icon);

            //timestamp
            String time = AppUtil.getTimeOnly(AppUtil.toDate(notification.date_created_utc).getTime());

            String timeStamp = AppUtil.getDateFormatNotifications(AppUtil.toDate(notification.date_created_utc)) + " " + context.getString(R.string.NOTIFICATIONS_TIME_AT) + " " + time;
            viewHolder.chatDate.setText(timeStamp);
        }
        else {
            String coachIdToSave = notification.coach_profile_picture.substring(notification.coach_profile_picture.indexOf("users/") + 6, notification.coach_profile_picture.lastIndexOf("/"));

            Bitmap avatar = getAvatar(Integer.parseInt(coachIdToSave));

            //coach profile photo
            if (avatar == null) {
                new AdapterDownloadImageTask(viewHolder.imageView).execute(notification.coach_profile_picture);
            } else {
                viewHolder.imageView.setImageBitmap(avatar);
            }

            //notif icon
            if (Integer.parseInt(notification.toolaction_id) == Validate) {
                viewHolder.chatMessage_icon.setImageResource(R.drawable.meal_approved_icon);
            } else if (Integer.parseInt(notification.toolaction_id) == Commented) {
                viewHolder.chatMessage_icon.setImageResource(R.drawable.meal_commented_icon);
            }
            else if (Integer.parseInt(notification.toolaction_id) == Disapprove) {
                viewHolder.chatMessage_icon.setImageResource(R.drawable.meal_disapproved_icon);
            }
            else if (Integer.parseInt(notification.toolaction_id) == Reminder) {
                viewHolder.chatMessage_icon.setImageResource(R.drawable.meal_commented_icon);
            }

            //timestamp
            String time = AppUtil.getTimeOnly(AppUtil.toDate(notification.date_created_utc).getTime());

            String timeStamp = AppUtil.getDateFormatNotifications(AppUtil.toDate(notification.date_created_utc)) + " " + context.getString(R.string.NOTIFICATIONS_TIME_AT) + " " + time;
            viewHolder.chatDate.setText(timeStamp);
        }

        //notif state -- read unread
        if (notification.is_read == true) {
            row.setBackgroundColor(COLOG_BG_READ_MES);
        } else {
            row.setBackgroundColor(COLOG_BG_UNREAD_MES);
        }

        //display message
        viewHolder.chatMessage.setText(notification.notification_text);


        return row;
    }

    private void refreshUI() {
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        // TODO Auto-generated method stub
        //if an item is click , set its value to unread and pass the position to the main activity
        if (v != null)
        {
            if (items != null && items.size() > 0)
            {
                int pos = (Integer) v.getTag(R.id.notif_pos);
                refreshUI();
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        }
    }

    private class AdapterDownloadImageTask extends AsyncTask<String, Void, Bitmap>
    {
        private ImageView bmImage;
        private String path;

        public AdapterDownloadImageTask(ImageView bmImage)
        {
            this.bmImage = bmImage;
            this.path = bmImage.getTag().toString();
        }

        protected Bitmap doInBackground(String... urls)
        {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            String urldisplay = WebkitURL.domainURL + urls[0];
            String coachIdToSave = urldisplay.substring(urldisplay.indexOf("users/") + 6, urldisplay.lastIndexOf("/"));

            Bitmap mIcon11 = null;

            try
            {
                if (!ApplicationData.getInstance().coachCommentsPhotosList.containsKey(coachIdToSave))
                {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);

                    ApplicationData.getInstance().coachCommentsPhotosList.put(coachIdToSave, mIcon11);
                }
                else
                {
                    mIcon11 = getAvatar(Integer.parseInt(coachIdToSave));
                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);

            if (!bmImage.getTag().toString().equals(path)) {
                return;
            }

            if (bitmap != null && bmImage != null) {
                bmImage.setVisibility(View.VISIBLE);
                bmImage.setImageBitmap(bitmap);

            } else {
                bmImage.setVisibility(View.GONE);
            }
        }
    }

    private Bitmap getAvatar(int coachId)
    {
        Bitmap avatarBMP = null;

        if (coachId > 0)
        {
            avatarBMP = ApplicationData.getInstance().coachCommentsPhotosList.get(String.valueOf(coachId));

            return avatarBMP;
        }
        else { //user comment use his image instead
            if (ApplicationData.getInstance().getUserProfilePhoto() != null)
                avatarBMP = ApplicationData.getInstance().getUserProfilePhoto();
            else if (ApplicationData.getInstance().getUserProfilePhoto() == null)
                avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_default_avatar, ApplicationData.getInstance().options_Avatar);

            if (avatarBMP == null)
                avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_default_avatar, ApplicationData.getInstance().options_Avatar);
        }

        return avatarBMP;
    }

    private static class ViewHolder
    {
        RoundedImageView imageView;
        TextView chatMessage;
        ImageView chatMessage_icon;
        TextView chatDate;
    }
}
