package anxa.com.smvideo.models;

import java.util.Date;

public class Workout
{
    public String coreData_id;
    public String activity_id;
    public String device_name;
    public int duration;
    public int steps;
    public int calories;
    public Date exercise_datetime;
    public String exercise_date;
    public Double distance;
    public String workout_desc;
    public String workout_image;
    public String command;
    public Boolean isChecked;
    public String dateString;

    public EXERCISE_STATE exercise_state;

    public static enum EXERCISE_STATE {

        ADD_ONGOING(1), UPDATE_ONGOING(2), DELETED(3), SYNC(4), FAILED(5), EMPTY(6);

        private final int value;

        private EXERCISE_STATE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static EXERCISE_STATE getSTATEValue(int value)
    {
        EXERCISE_STATE returnvalue = EXERCISE_STATE.ADD_ONGOING; // default return value
        for (final EXERCISE_STATE type : EXERCISE_STATE.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }

    public EXERCISE_TYPE exercise_type;

    public static enum EXERCISE_TYPE {

        ACTIVITY_IOS (-1),

        OTHER (0),

        RUNNING (1),

        CYCLING (2),

        MOUNTAIN_BIKING (3),

        WALKING (4),

        HIKING (5),

        DOWNHILL_SKIING (6),

        CROSSCOUNTRY_SKIING (7),

        SNOWBOARDING (8),

        SKATING (9),

        SWIMMING (10),

        WHEELCHAIR (11),

        ROWING (12),

        ELLIPTICAL (13),

        NO_EXERCISE (14),

        YOGA (15),

        PILATES (16),

        CROSSFIT (17),

        SPINNING (18),

        ZUMBA (19),

        BARRE (20),

        GROUP_WORKOUT (21),

        DANCE (22),

        BOOTCAMP (23),

        BOXING (24),

        MMA (25),

        MEDITATION (26),

        STRENGTH_TRAINING (27),

        CIRCUIT_TRAINING (28),

        CORE_STRENGTHENING (29),

        ARC_TRAINER (30),

        STAIR_MASTER (31),

        STEPWELL (32),

        NORDIC_WALKING (33),

        SPORTS (34),

        WORKOUT (35),

        AQUAGYM (36),

        GOLF (37),

        HOUSEWORK (38),

        GARDENING (39);

        private final int value;

        private int exerciseTypeIndex;

        private EXERCISE_TYPE(int value) {
            this.exerciseTypeIndex = value;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static EXERCISE_TYPE getExerciseType(int exerciseTypeIndex)
        {
            for (EXERCISE_TYPE l : EXERCISE_TYPE.values()) {
                if (l.exerciseTypeIndex == exerciseTypeIndex)
                    return l;
                else{

                }
            }
            throw new IllegalArgumentException("Exercise type not found.");
        }
    }

    public static EXERCISE_TYPE getEXERCISETYPEValue(int value) {
        EXERCISE_TYPE returnvalue = EXERCISE_TYPE.ACTIVITY_IOS; // default return value
        for (final EXERCISE_TYPE type : EXERCISE_TYPE.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }

/*
    public static String setDatetoString(Date d)
    {
        return AppUtil.getDateinUTC(d);
    }

    public static Date setStringtoDate(String str) {
        return AppUtil.stringToDateFormat2(str);
    }
  */


}