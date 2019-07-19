package anxa.com.smvideo.contracts.Notifications;

/**
 * Created by elaineanxa on 15/07/2019
 */

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class GetNotificationsDataContract
{
    public GetNotificationsDataContract()
    {
        Notifications = new ArrayList<NotificationsContract>();
    }

    @SerializedName("Response")
    public List<NotificationsContract> Notifications;
}
