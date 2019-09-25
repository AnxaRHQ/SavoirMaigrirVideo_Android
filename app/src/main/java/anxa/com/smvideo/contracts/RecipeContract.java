package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 5/25/2017.
 */

public class RecipeContract {
    @SerializedName("id")
    public int Id;
    @SerializedName("type")
    public  int RecipeType;
    @SerializedName("title")
    public String Title;
    @SerializedName("ingredientsTitle")
    public String IngredientsTitle;
    @SerializedName("ingredientsHtml")
    public  String IngredientsHtml;
    @SerializedName("ingredientsText")
    public String IngredientsText;
    @SerializedName("preparationHtml")
    public String PreparationHtml;
    @SerializedName("preparationText")
    public String PreparationText;
    @SerializedName("imageUrl")
    public String ImageUrl;

    public  enum RecipeTypeEnum
    {
        Appetizer(1),
        Drink(2),
        Dessert(3),
        Entree(4),
        Plat(5),
        Salad(6),
        Sauce(7),
        Snack(8),
        Soup(9),
        Other(10),
        Simplissime(0),
        Thermomix(100);

        private int numVal;

        RecipeTypeEnum(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

}
