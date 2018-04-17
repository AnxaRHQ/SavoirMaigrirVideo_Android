package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 1/19/2017.
 */

public class PhotoUploadDataContract extends BaseContract {
    @SerializedName("image_url")
    public String ImageUrl;

    @SerializedName("user_id")
    public int UserId ;
    @SerializedName("name")
    public String Name ;
}
