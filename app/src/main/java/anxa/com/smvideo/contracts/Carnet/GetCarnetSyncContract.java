package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import anxa.com.smvideo.contracts.BaseContract;

/**
 * Created by angelaanxa on 1/6/2017.
 */

public class GetCarnetSyncContract extends BaseContract {
    @SerializedName("data")
    public GetCarnetSyncDataContract Data ;
}
