package net.leelink.healthangelos.activity.owonDevice;

import com.google.gson.annotations.SerializedName;

public class HisBean {

    @SerializedName("id")
    private Integer id;
    @SerializedName("deviceTypeId")
    private String deviceTypeId;
    @SerializedName("imei")
    private String imei;
    @SerializedName("elderlyId")
    private Integer elderlyId;
    @SerializedName("evt")
    private String evt;
    @SerializedName("nbErrNum")
    private String nbErrNum;
    @SerializedName("batVol")
    private String batVol;
    @SerializedName("bat2Vol")
    private Object bat2Vol;
    @SerializedName("batValue")
    private String batValue;
    @SerializedName("temperature")
    private String temperature;
    @SerializedName("smokeValue")
    private Double smokeValue;
    @SerializedName("gasLel")
    private Object gasLel;
    @SerializedName("csq")
    private String csq;
    @SerializedName("heartTime")
    private String heartTime;
    @SerializedName("byte1")
    private String byte1;
    @SerializedName("byte0")
    private String byte0;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("evtName")
    private String evtName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(String deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Integer getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(Integer elderlyId) {
        this.elderlyId = elderlyId;
    }

    public String getEvt() {
        return evt;
    }

    public void setEvt(String evt) {
        this.evt = evt;
    }

    public String getNbErrNum() {
        return nbErrNum;
    }

    public void setNbErrNum(String nbErrNum) {
        this.nbErrNum = nbErrNum;
    }

    public String getBatVol() {
        return batVol;
    }

    public void setBatVol(String batVol) {
        this.batVol = batVol;
    }

    public Object getBat2Vol() {
        return bat2Vol;
    }

    public void setBat2Vol(Object bat2Vol) {
        this.bat2Vol = bat2Vol;
    }

    public String getBatValue() {
        return batValue;
    }

    public void setBatValue(String batValue) {
        this.batValue = batValue;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public Double getSmokeValue() {
        return smokeValue;
    }

    public void setSmokeValue(Double smokeValue) {
        this.smokeValue = smokeValue;
    }

    public Object getGasLel() {
        return gasLel;
    }

    public void setGasLel(Object gasLel) {
        this.gasLel = gasLel;
    }

    public String getCsq() {
        return csq;
    }

    public void setCsq(String csq) {
        this.csq = csq;
    }

    public String getHeartTime() {
        return heartTime;
    }

    public void setHeartTime(String heartTime) {
        this.heartTime = heartTime;
    }

    public String getByte1() {
        return byte1;
    }

    public void setByte1(String byte1) {
        this.byte1 = byte1;
    }

    public String getByte0() {
        return byte0;
    }

    public void setByte0(String byte0) {
        this.byte0 = byte0;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEvtName() {
        return evtName;
    }

    public void setEvtName(String evtName) {
        this.evtName = evtName;
    }
}
