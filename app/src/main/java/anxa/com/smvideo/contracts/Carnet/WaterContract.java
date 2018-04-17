package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 1/6/2017.
 */

public class WaterContract {
    @SerializedName("water_id")
    public int WaterId ;
    @SerializedName("timestamp_utc")
    public long TimestampUtc;
    @SerializedName("creation_date")
    public long CreationDate;
    @SerializedName("number_of_glasses")
    public int NumberOfGlasses;
    @SerializedName("command")
    public String Command;
}
