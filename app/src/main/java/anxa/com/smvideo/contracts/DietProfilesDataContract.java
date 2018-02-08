package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aprilanxa on 29/06/2017.
 */

public class DietProfilesDataContract {

    @SerializedName("Dd")
    public int Id;

    @SerializedName("UserId")
    public int UserId;

    @SerializedName("Gender")
    public int Gender;

    @SerializedName("StartWeightInKg")
    public float StartWeightInKg;

    @SerializedName("CurrentWeightInKg")
    public float CurrentWeightInKg;

    @SerializedName("TargetWeightInKg")
    public float TargetWeightInKg;

    @SerializedName("HeightInMeter")
    public float HeightInMeter;

    @SerializedName("DietProfileAnswers")
    public String DietProfileAnswers;

    @SerializedName("Version")
    public int Version;

    @SerializedName("MealPlanType")
    public int MealPlanType;

    @SerializedName("CalorieType")
    public int CalorieType;

    @SerializedName("CoachingProfile")
    public int CoachingProfile;

    @SerializedName("CoachProgram")
    public int CoachProgram;

    @SerializedName("CoachingStartDate")
    public String CoachingStartDate;

    @SerializedName("CoachingEndDate")
    public String CoachingEndDate;

    @SerializedName("ApplicationId")
    public int ApplicationId;

    @SerializedName("Application")
    public ApplicationDataContract Application;

    @SerializedName("Deleted")
    public Boolean deleted;

    @SerializedName("ModifiedDate")
    public String modifiedDate;

    @SerializedName("CreatedDate")
    public String createdDate;

}
