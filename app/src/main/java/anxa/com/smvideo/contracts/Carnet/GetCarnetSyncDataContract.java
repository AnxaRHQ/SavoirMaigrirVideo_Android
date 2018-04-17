package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelaanxa on 1/6/2017.
 */

public class GetCarnetSyncDataContract {
    public GetCarnetSyncDataContract(){
        Meals = new ArrayList<MealContract>();
        Water = new ArrayList<WaterContract>();
        Mood = new ArrayList<MoodContract>();
        Exercise = new ArrayList<ExerciseContract>();

    }

  @SerializedName("meal")
    public List<MealContract> Meals;

    @SerializedName("water")
    public List<WaterContract> Water;

    @SerializedName("mood")
    public List<MoodContract> Mood;

    @SerializedName("exercise")
    public List<ExerciseContract> Exercise;

    @SerializedName("membership")
    public String Membership;
}
