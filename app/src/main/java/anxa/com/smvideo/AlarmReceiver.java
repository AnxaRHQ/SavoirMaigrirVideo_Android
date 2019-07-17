package anxa.com.smvideo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import anxa.com.smvideo.activities.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String theMessage;
        String theTitle;
        String theTicker;
        int NotificationID;
        String CHANNEL_ID = "Notif" + BuildConfig.APPLICATION_ID;// The id of the channel.
        CharSequence name = BuildConfig.APPLICATION_ID;// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;




        Intent notificationIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent);

        theMessage = intent.getStringExtra("Message");
        theTitle = intent.getStringExtra("Title");
        theTicker = intent.getStringExtra("Ticker");
        NotificationID = intent.getIntExtra("NotificationID",1);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(NotificationID*(2*100), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
//        style.bigText(theMessage);

//        Notification notification = style.setContentTitle(theTitle)
//                .setContentText(theMessage)
//                .setTicker(theTicker)
//                .setSmallIcon(R.drawable.app_icon)
//                .setSmallIcon(getNotificationIcon())
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon))
//                .se
// tAutoCancel(true)
//                .setContentIntent(pendingIntent).build();

//        Notification notification = new Notification.Builder(context)

        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle(theTitle)
                .setContentText(theMessage)
                .setTicker(theTicker)
                .setSmallIcon(R.drawable.app_icon)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(theMessage))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setChannelId(CHANNEL_ID).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(NotificationID, notification);

//        Log.d("Notification", "Created notification #" + NotificationID);

    }

    private int getNotificationIcon() {

        return R.drawable.app_icon;
    }




}