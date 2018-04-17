package anxa.com.smvideo.contracts.Graph;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aprilanxa on 17/02/2017.
 */

public class WeightDataContract {
    public WeightDataContract(){
        WeightList = new ArrayList<WeightContract>();
    }

    @SerializedName("data")
    public List<WeightContract> WeightList;
}
