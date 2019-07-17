package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

public class AlertsContract {
    @SerializedName("alert_type")
    public int AlertType;
    @SerializedName("alert_item")
    public String AlertItem;
    @SerializedName("daysofweek")
    public String DaysOfWeek;
    @SerializedName("endtime")
    public String EndTime;
    @SerializedName("isremind")
    public boolean IsRemind;
    @SerializedName("message")
    public String Message;
    @SerializedName("starttime")
    public String starttime;



}
