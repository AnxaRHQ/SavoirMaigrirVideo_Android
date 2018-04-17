package anxa.com.smvideo.contracts.Graph;

import com.google.gson.annotations.SerializedName;

import anxa.com.smvideo.contracts.BaseContract;


public class StepDataContract extends BaseContract {
    @SerializedName("id")
    public int Id;
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
    /**
     * yyyy-MM-dd format
     */
    @SerializedName("step_date")
    public String StepDate;
    @SerializedName("steps")
    public int Steps;
    @SerializedName("distance_meters")
    public int Distance;
    @SerializedName("duration_seconds")
    public int Duration;
    @SerializedName("calories")
    public int Calories;
}
