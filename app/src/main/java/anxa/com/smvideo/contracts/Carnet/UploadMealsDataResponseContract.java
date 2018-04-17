package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import anxa.com.smvideo.contracts.BaseContract;

/**
 * Created by angelaanxa on 1/17/2017.
 */

public class UploadMealsDataResponseContract extends BaseContract {
    @SerializedName("data")
    public UploadMealsDataContract Data;
}
