package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 1/19/2017.
 */

public class PhotoUploadDataResponseContract extends BaseContract {
    @SerializedName("data")
    public PhotoUploadDataContract Data;
}
