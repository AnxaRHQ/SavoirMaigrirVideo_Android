package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelaanxa on 1/10/2017.
 */

public class AlbumContract {
    public AlbumContract()
    {
        Photos = new ArrayList<PhotoContract>();
    }
    @SerializedName("photo_count")
    public int PhotoCount;
    @SerializedName("photo")
    public List<PhotoContract> Photos ;
}
