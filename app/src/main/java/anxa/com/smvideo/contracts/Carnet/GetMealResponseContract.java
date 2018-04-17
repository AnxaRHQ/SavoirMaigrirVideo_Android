package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import anxa.com.smvideo.contracts.BaseContract;

/**
 * Created by angelaanxa on 3/24/2017.
 */

public class GetMealResponseContract extends BaseContract {
    @SerializedName("data")
    public MealDataContract Data;
}
