package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aprilanxa on 08/09/2017.
 */

public class RegisterUserResponseContract extends BaseContract {
    @SerializedName("data")
    public RegisterUserContract Data;
}
