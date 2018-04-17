package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 1/9/2017.
 */

public class ExerciseContract {
    @SerializedName("command")
    public String Command;
    @SerializedName("status")
    public String Status;
    @SerializedName("exercise_id")
    public int ExerciseId ;
    @SerializedName("exercise_type")
    public int ExerciseType ;
    @SerializedName("timestamp_utc")
    public long TimestampUtc;
    @SerializedName("creation_date")
    public long CreationDate;
    @SerializedName("duration")
    public int Duration;
    @SerializedName("steps")
    public int Steps;
    @SerializedName("distance")
    public double Distance;
    @SerializedName("title")
    public String Title;
    @SerializedName("message")
    public String Message;
    @SerializedName("exercise_name")
    public String ExerciseName;

    public int Calories;
}
