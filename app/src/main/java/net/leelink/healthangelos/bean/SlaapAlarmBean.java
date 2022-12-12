package net.leelink.healthangelos.bean;

public class SlaapAlarmBean {


    private Integer id;
    private Integer elderlyId;
    private String sn;
    private Object breathe;
    private Object heart;
    private Integer type;
    private String message;
    private String createTime;
    private Long timestamp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
