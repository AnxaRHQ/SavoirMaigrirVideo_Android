package anxa.com.smvideo.contracts.Carnet;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 1/10/2017.
 */

public class PhotoContract {
    @SerializedName("photo_id")
    public int PhotoId ;

    @SerializedName("url")
    public String UrlLarge ;

    public transient Bitmap image;

    public static PHOTO_STATUS getPHOTOSTATUSvalue(int value) {
        PHOTO_STATUS returnvalue = PHOTO_STATUS.ONGOING_UPLOADPHOTO; // default returnvalue
        for (final PHOTO_STATUS type : PHOTO_STATUS.values()) {
            if (type.value == value) {
                returnvalue = type;
                break;
            }
        }
        return returnvalue;
    }
    public static enum PHOTO_STATUS {

        ONGOING_UPLOADPHOTO(1), SYNC_UPLOADPHOTO(2), FAILED_UPLOADPHOTO(3);

        private final int value;

        private PHOTO_STATUS(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


    };


    public transient PHOTO_STATUS state = PHOTO_STATUS.SYNC_UPLOADPHOTO ;//default value is sync
}
