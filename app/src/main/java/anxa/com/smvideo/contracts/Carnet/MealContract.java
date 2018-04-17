package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelaanxa on 1/6/2017.
 */

public class MealContract {
 public MealContract(){
    FoodGroups = new ArrayList<FoodGroupContract>();
    Comments = new CommentGroupContract();
    Album = new AlbumContract();
 }
    @SerializedName("meal_id")
    public int MealId ;
/*    Breakfast = 1,
    Lunch = 2,
    Snack = 3,
    Dinner = 4,
    MorningSnack = 5,
    AfternoonSnack = 6,*/
   @SerializedName("meal_type")
    public int MealType;
    @SerializedName("timestamp_utc")
    public long TimestampUtc;
    @SerializedName("meal_creation_date")
    public long MealCreationDate;
    @SerializedName("description")
    public String Description;
    @SerializedName("isapproved")
    public boolean IsApproved;
    @SerializedName("iscommented")
    public boolean IsCommented;
    @SerializedName("haspaidsubscription")
    public boolean HasPaidSubscription;
    @SerializedName("foodgroup")
    public List<FoodGroupContract> FoodGroups;
    @SerializedName("commentgroup")
    public CommentGroupContract Comments;
    @SerializedName("album")
    public AlbumContract Album;
    @SerializedName("command")
    /*added,updated,deleted*/
    public String Command;

    public transient String DefaultMealDescription;

}
