package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aprilanxa on 22/03/2018.
 */

public class MessagesResponseContract extends BaseContract {
    @SerializedName("cursor")
    public MessagesCursorContract Cursor;

    @SerializedName("data")
    public MessagesDataContract Data;
}
