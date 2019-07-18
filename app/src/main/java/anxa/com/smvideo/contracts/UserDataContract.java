package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by aprilanxa on 29/06/2017.
 */

public class UserDataContract extends BaseContract{
    @SerializedName("Id")
    public int Id;

    @SerializedName("Email")
    public String Email;

    @SerializedName("EmailConfirmed")
    public boolean EmailConfirmed;

    @SerializedName("PasswordHash")
    public String PasswordHash;

    @SerializedName("Phone")
    public String Phone;

    @SerializedName("PhoneConfirmed")
    public boolean PhoneConfirmed;

    @SerializedName("UserName")
    public String userName;

    @SerializedName("FirstName")
    public String FirstName;

    @SerializedName("MiddleName")
    public String MiddleName;

    @SerializedName("LastName")
    public String LastName;

    @SerializedName("PictureUrl")
    public String PictureUrl;

    @SerializedName("MembershipType")
    public String MembershipType;

    @SerializedName("AlertType")
    public String AlertType;

    @SerializedName("AlertContact")
    public String AlertContact;

    @SerializedName("Activities")
    public List<ActivitiesDataContract> Activities;

    @SerializedName("DietProfiles")
    public List<DietProfilesDataContract> DietProfiles;

    @SerializedName("Deleted")
    public Boolean deleted;

    @SerializedName("ModifiedDate")
    public String modifiedDate;

    @SerializedName("CreatedDate")
    public String createdDate;

    @SerializedName("IsAnyVip")
    public boolean IsAnyVip;

    @SerializedName("SubscriptionType")
    public byte SubscriptionType;

    @SerializedName("IsVip")
    public boolean IsVip;

    @SerializedName("DateRegistered")
    public String DateRegistered;

}
