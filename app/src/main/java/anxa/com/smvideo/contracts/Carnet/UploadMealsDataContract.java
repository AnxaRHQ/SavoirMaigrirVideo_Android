package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.contracts.BaseContract;

/**
 * Created by angelaanxa on 1/10/2017.
 */

public class UploadMealsDataContract extends BaseContract {
    public UploadMealsDataContract(){
        Meals = new ArrayList<MealContract>();
        Water = new ArrayList<WaterContract>();
        Mood = new ArrayList<MoodContract>();
        Exercise = new ArrayList<ExerciseContract>();

    }
    @SerializedName("upload_count")
    public int UploadCount;

    @SerializedName("meal")
    public List<MealContract> Meals;

    @SerializedName("water")
    public List<WaterContract> Water;

    @SerializedName("mood")
    public List<MoodContract> Mood;

    @SerializedName("exercise")
    public List<ExerciseContract> Exercise;
}
