package anxa.com.smvideo.models;


import android.provider.ContactsContract;

import java.util.Date;
import java.util.List;

public class Meal {
    public static final byte MEALSTATE_ADD = 20;
    public static final byte MEALSTATE_EDIT = 21;
    public static final byte MEALSTATE_DELETE = 22;
    public String meal_id;

    // coming from (nonatomic, retain)
    public String coreData_id;

    public Date timestamp; //date today, date the meal is added

    public Date meal_creation_date;  //date of the meal(date tab)

    /*
     * Fixed value String {"Breakfast","Morning Snack","Lunch",Afternoon
     * Snack","Dinner"}
     */
    public String title;

    /* max char = 450 */
    public String meal_description;

    /**
     * meal is approved by the coach
     **/
    public Boolean isApproved;

    /**
     * meal is commented by the coach
     **/
    public Boolean isCommented;

    /**
     * new updated included
     **/
    public Boolean hasUpdate = true;

    /**
     * all meal with rating
     **/
    public Boolean isAllMealWithRating;

    /**
     * all healthy meal with rating
     **/
    public Boolean isHealthyMealWithRating;

    /**
     * all just ok meal with rating
     **/
    public Boolean isJustOkMealWithRating;

    /**
     * all unhealthy meal with rating
     **/
    public Boolean isUnhealthyMealWithRating;

    /** is user subcribe when this meal is created **/
    /**
     * NEWLY ADDED FIELD - JFF as of June 3
     **/
    public Boolean haspaidSubcription = false; //default is false

    /* to removed later public List<Comment> comments;*/

    public int commentsCount;

    public int userRating;
    public int coachRating;

    public List<ContactsContract.Contacts.Photo> photos;


    public int photoUploadProgress;

    public static FOODGROUP getFGValue(int value) {
        FOODGROUP returnvalue = FOODGROUP.DAIRY; // default returnvalue
        for (final FOODGROUP type : FOODGROUP.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }

    public static STATE getSTATEValue(int value) {
        STATE returnvalue = STATE.ADD_ONGOING; // default returnvalue
        for (final STATE type : STATE.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }

    public static enum STATE {

        ADD_ONGOING(1), UPDATE_ONGOING(2), DELETED(3), SYNC(4), FAILED(5), EMPTY(6);

        private final int value;

        private STATE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    ;


    public static enum FOODGROUP {

        DRINKS(1), VEGETABLE(2), SWEETS(3), FRUITS(4), STARCHES(5), PROTEIN(6), DAIRY(7), FATS(8);

        private final int value;
        private int foodGroupIndex;


        public int getValue() {
            return value;
        }

        private FOODGROUP(int foodGroupIndex) {
            this.foodGroupIndex = foodGroupIndex;
            this.value = foodGroupIndex;
        }

        public static FOODGROUP getFoodGroup(int foodGroupIndex) {
            for (FOODGROUP l : FOODGROUP.values()) {
                if (l.foodGroupIndex == foodGroupIndex)
                    return l;
            }
            throw new IllegalArgumentException("Meal type not found.");
        }
    }

    ;


    public STATE state;

    public List<FOODGROUP> food_group;

    public static enum MEAL_TYPE {
        BREAKFAST(1), AM_SNACK(5), LUNCH(2), PM_SNACK(6), DINNER(3);

        private int mealTypeIndex;

        private final int value;

        private MEAL_TYPE(int mealTypeIndex) {
            this.mealTypeIndex = mealTypeIndex;
            this.value = mealTypeIndex;
        }

        public static int getMealType(int mealTypeIndex) {
            for (MEAL_TYPE l : MEAL_TYPE.values()) {
                if (l.mealTypeIndex == mealTypeIndex)
                    return l.getValue();
                else{

                }
            }
            throw new IllegalArgumentException("Meal type not found.");
        }

        public int getValue() {
            return value;
        }
    }

    public MEAL_TYPE meal_type;// added april 7

    public String meal_status;

    public Meal() {

    }

    /* to removed later
    public static String setDatetoString(Date d) {
        return AppUtil.getDateinUTC(d);
    }

    public static Date setStringtoDate(String str) {
        return AppUtil.stringToDateFormat(str);
    }
    */
}
