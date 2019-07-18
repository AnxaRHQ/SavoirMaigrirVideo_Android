package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprilanxa on 22/03/2018.
 */

public class MessagesDataContract {
    public MessagesDataContract(){
        Messages = new ArrayList<MessagesContract>();
    }

    @SerializedName("messages")
    public List<MessagesContract> Messages;

    @SerializedName("isLastMessageArchivedByCoach")
    public boolean  IsLastMessageArchivedByCoach;

    @SerializedName("creditsUsedWeek")
    public int  CreditsUsedWeek;
}
