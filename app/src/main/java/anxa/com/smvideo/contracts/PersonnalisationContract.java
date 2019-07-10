package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

public class PersonnalisationContract extends BaseContract {
    @SerializedName("mealprofile")
    public int MealProfile;
    @SerializedName("coachingprofile")
    public int CoachingProfile;
    @SerializedName("calorielevel")
    public int CalorieLevel;
}

