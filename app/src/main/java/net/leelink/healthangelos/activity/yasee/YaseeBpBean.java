package net.leelink.healthangelos.activity.yasee;

import com.google.gson.annotations.SerializedName;

public class YaseeBpBean {

    @SerializedName("id")
    private String id;
    @SerializedName("elderlyId")
    private String elderlyId;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("deviceImei")
    private String deviceImei;
    @SerializedName("testTime")
    private Integer testTime;
    @SerializedName("diastolic")
    private float diastolic;
    @SerializedName("systolic")
    private float systolic;
    @SerializedName("heartRate")
    private float heartRate;
    @SerializedName("arrhythmia")
    private String arrhythmia;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(String elderlyId) {
        this.elderlyId = elderlyId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDeviceImei() {
        return deviceImei;
    }

    public void setDeviceImei(String deviceImei) {
        this.deviceImei = deviceImei;
    }

    public Integer getTestTime() {
        return testTime;
    }

    public void setTestTime(Integer testTime) {
        this.testTime = testTime;
    }

    public float getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(float diastolic) {
        this.diastolic = diastolic;
    }

    public float getSystolic() {
        return systolic;
    }

    public void setSystolic(float systolic) {
        this.systolic = systolic;
    }

    public float getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(float heartRate) {
        this.heartRate = heartRate;
    }

    public String getArrhythmia() {
        return arrhythmia;
    }

    public void setArrhythmia(String arrhythmia) {
        this.arrhythmia = arrhythmia;
    }
}
