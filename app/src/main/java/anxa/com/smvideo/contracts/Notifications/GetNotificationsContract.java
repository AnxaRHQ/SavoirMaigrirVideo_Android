package anxa.com.smvideo.contracts.Notifications;

/**
 * Created by elaineanxa on 15/07/2019
 */

import com.google.gson.annotations.SerializedName;
import anxa.com.smvideo.contracts.BaseContract;

public class GetNotificationsContract extends BaseContract
{
    @SerializedName("cursor")
    public NotificationsCursorContract Cursor;

    @SerializedName("data")
    public GetNotificationsDataContract Data ;
}