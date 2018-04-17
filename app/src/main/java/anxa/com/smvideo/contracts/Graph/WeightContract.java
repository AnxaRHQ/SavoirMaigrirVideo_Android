package anxa.com.smvideo.contracts.Graph;

import com.google.gson.annotations.SerializedName;

import anxa.com.smvideo.contracts.BaseContract;


public class WeightContract extends BaseContract {
    @SerializedName("id")
    public int Id ;
    /***
     *
     Manual = 0,
     HapiTrack = 1,
     HapiBand = 2,
     HapiScale = 6,
     HapiScalePlus = 7,
     Fitbug = 11,
     Fitbit = 12,
     Withings = 14,
     RunKeeper = 50,
     SMIOS = 58,
     HapiIOS = 59,
     Actipod = 70,
     */
    @SerializedName("device_type")
    public int DeviceType;
    @SerializedName("reg_no")
    public int RegNo;
    @SerializedName("weight_kg")
    public Double Weight;
    @SerializedName("bmi")
    public double Bmi;
    @SerializedName("weight_date")
    public String WeightDate;


}
