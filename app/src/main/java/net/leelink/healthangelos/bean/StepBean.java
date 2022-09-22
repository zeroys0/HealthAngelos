package net.leelink.healthangelos.bean;

import com.google.gson.annotations.SerializedName;

public class StepBean {


    private String id;
    private String time;
    @SerializedName("MID")
    private String mID;
    @SerializedName("Step")
    private Integer step;
    @SerializedName("Roll")
    private String roll;
    @SerializedName("Day")
    private String day;
    private Integer pushType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public Integer getPushType() {
        return pushType;
    }

    public void setPushType(Integer pushType) {
        this.pushType = pushType;
    }
}
