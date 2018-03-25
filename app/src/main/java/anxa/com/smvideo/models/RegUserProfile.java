package anxa.com.smvideo.models;

import android.graphics.Bitmap;

/**
 * Created by angelaanxa on 10/17/2017.
 */

public class RegUserProfile {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String gender;
    private String country;
    private float initialWeight;
    private float targetWeight;
    private String objective;
    private String motivation;
    private String minutesSpent;
    private String profession;
    private Float height;
    private boolean receiveNewsLetter;
    private int paymentId;
    public String username;
    private String bday;
    private int coaching;
    private int mealProfile;
    private String cuisiner;
    private boolean noCookingTrial;
    private String profileimageurl;

    public String getProfileimageurl() {
        return profileimageurl;
    }

    public void setProfileimageurl(String profileimageurl) {
        this.profileimageurl = profileimageurl;
    }
    
    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    private int sid;

    public boolean isReceiveNewsLetter() {
        return receiveNewsLetter;
    }

    public String getRegId() {
        return RegId;
    }

    public void setRegId(String regId) {
        RegId = regId;
    }

    public String getAJRegNo() {
        return AJRegNo;
    }

    public void setAJRegNo(String AJRegNo) {
        this.AJRegNo = AJRegNo;
    }

    private String RegId;
    private String AJRegNo;

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public boolean getReceiveNewsLetter() {
        return receiveNewsLetter;
    }

    public void setReceiveNewsLetter(boolean receiveNewsLetter) {
        this.receiveNewsLetter = receiveNewsLetter;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getInitialWeight() {
        return initialWeight;
    }

    public void setInitialWeight(float initialWeight) {
        this.initialWeight = initialWeight;
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float targetWeight) {
        this.targetWeight = targetWeight;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getMinutesSpent() {
        return minutesSpent;
    }

    public void setMinutesSpent(String minutesSpent) {
        this.minutesSpent = minutesSpent;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId =paymentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String minutesSpent) {
        this.username = username;
    }
    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getBday() {
        return bday;
    }

    public void setBday(String bday) {
        this.bday = bday;
    }
    public int getCoaching() {
        return coaching;
    }

    public void setCoaching(int coaching) {
        this.coaching =coaching;
    }
    public int getMealProfile() {
        return mealProfile;
    }

    public void setMealProfile(int mealProfile) {
        this.mealProfile =mealProfile;
    }
    public String getCuisiner() {
        return cuisiner;
    }



    public void setCuisiner(String cuisiner) {
        this.cuisiner = cuisiner;
    }
    public Boolean getNoCookingTrial() {
        return noCookingTrial;
    }
    public void setNoCookingTrial(Boolean noCookingTrial) {
        this.noCookingTrial = noCookingTrial;
    }

}
