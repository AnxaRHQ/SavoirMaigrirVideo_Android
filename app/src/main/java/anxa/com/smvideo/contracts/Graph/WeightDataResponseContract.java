package anxa.com.smvideo.contracts.Graph;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import anxa.com.smvideo.contracts.BaseContract;

/**
 * Created by angelaanxa on 2/3/2017.
 */

public class WeightDataResponseContract extends BaseContract {
    @SerializedName("data")
    public List<WeightContract> Data;
}