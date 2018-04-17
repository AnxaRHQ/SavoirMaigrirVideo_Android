package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 1/10/2017.
 */

public class FoodGroupContract {

   /* Beverages = 1,
    Vegetables = 2,
    Sweets = 3,
    Fruits = 4,
    Cereals = 5,
    Proteins = 6,
    Dairies = 7,
    Fats = 8,*/
    @SerializedName("groupid")
    public int FoodGroupId;
}
