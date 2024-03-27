package net.leelink.healthangelos.activity.NBdevice.adapter;

public class TiBean {

    private Integer id;
    private Integer elderlyId;
    private String imei;
    private String eventType;
    private String eventDec;
    private Integer eventValue;
    private String createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(Integer elderlyId) {
        this.elderlyId = elderlyId;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDec() {
        return eventDec;
    }

    public void setEventDec(String eventDec) {
        this.eventDec = eventDec;
    }

    public Integer getEventValue() {
        return eventValue;
    }

    public void setEventValue(Integer eventValue) {
        this.eventValue = eventValue;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
