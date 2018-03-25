package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aprilanxa on 22/03/2018.
 */

public class PostMessagesContract {
    @SerializedName("messageChat")
    public String MessageChat;
    @SerializedName("createdDate")
    public long CreatedDate;
    @SerializedName("regId")
    public int RegId;
}
