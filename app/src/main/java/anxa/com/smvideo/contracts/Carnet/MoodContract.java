package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 1/6/2017.
 */

public class MoodContract {
    @SerializedName("mood_id")
    public int MoodId ;
    @SerializedName("timestamp_utc")
    public long TimestampUtc;
    @SerializedName("creation_date")
    public long CreationDate;
    @SerializedName("command")
    public String Command;
    @SerializedName("mood_type")
    //value is 1-5; 1 - happiest, 5 saddest
    public int MoodType;
    @SerializedName("is_hungry")
    public Boolean IsHungry;
    @SerializedName("message")
    public String Message;
    @SerializedName("commentgroup")
    public CommentGroupContract Comments;
}
