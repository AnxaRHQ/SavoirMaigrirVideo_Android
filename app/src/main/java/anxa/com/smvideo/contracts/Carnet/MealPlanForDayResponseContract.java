package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import anxa.com.smvideo.contracts.BaseContract;

/**
 * Created by angelaanxa on 1/19/2017.
 */

public class MealPlanForDayResponseContract extends BaseContract {
    @SerializedName("data")
    public MealPlanForDayContract Data;
}
