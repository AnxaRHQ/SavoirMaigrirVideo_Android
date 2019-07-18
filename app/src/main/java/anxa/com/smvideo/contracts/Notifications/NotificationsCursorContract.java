package anxa.com.smvideo.contracts.Notifications;

/**
 * Created by elaineanxa on 15/07/2019
 */

import com.google.gson.annotations.SerializedName;
import anxa.com.smvideo.contracts.BaseContract;

public class NotificationsCursorContract extends BaseContract
{
    @SerializedName("before")
    public long before;

    @SerializedName("next")
    public long next;

    @SerializedName("previous")
    public long previous;
}
