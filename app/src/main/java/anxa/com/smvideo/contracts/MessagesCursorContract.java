package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aprilanxa on 22/03/2018.
 */

public class MessagesCursorContract {
    @SerializedName("before")
    public long before;
    @SerializedName("mesnextsages")
    public long next;
    @SerializedName("previous")
    public long previous;
}
