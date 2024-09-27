package net.leelink.healthangelos.activity.yasee;

import com.google.gson.annotations.SerializedName;

public class YaseeBsBean {

    @SerializedName("id")
    private String id;
    @SerializedName("elderlyId")
    private String elderlyId;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("deviceImei")
    private String deviceImei;
    @SerializedName("testTime")
    private long testTime;
    @SerializedName("result")
    private float result;
    @SerializedName("period")
    private String period;
    @SerializedName("temp")
    private String temp;
    @SerializedName("paperCode")
    private String paperCode;

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

    public long getTestTime() {
        return testTime;
    }

    public void setTestTime(long testTime) {
        this.testTime = testTime;
    }

    public float getResult() {
        return result;
    }

    public void setResult(float result) {
        this.result = result;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getPaperCode() {
        return paperCode;
    }

    public void setPaperCode(String paperCode) {
        this.paperCode = paperCode;
    }
}
