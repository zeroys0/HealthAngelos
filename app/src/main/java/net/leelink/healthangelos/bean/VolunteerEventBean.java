package net.leelink.healthangelos.bean;

import java.io.Serializable;

public class VolunteerEventBean implements Serializable {


    private String id;
    private String volUserNo;
    private String elderlyNo;
    private String servContent;
    private String servTime;
    private String startTime;
    private String endTime;
    private String updateName;
    private String updateTime;
    private String servTelephone;
    private String servAddress;
    private String remark;
    private String servName;
    private String servStartTime;
    private String servEndTime;
    private int state;
    private String servTitle;
    private int type;
    private int num;
    private String cause;
    private int senderId;

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getServStartTime() {
        return servStartTime;
    }

    public void setServStartTime(String servStartTime) {
        this.servStartTime = servStartTime;
    }

    public String getServEndTime() {
        return servEndTime;
    }

    public void setServEndTime(String servEndTime) {
        this.servEndTime = servEndTime;
    }

    public String getServName() {
        return servName;
    }

    public void setServName(String servName) {
        this.servName = servName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getServTitle() {
        return servTitle;
    }

    public void setServTitle(String servTitle) {
        this.servTitle = servTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVolUserNo() {
        return volUserNo;
    }

    public void setVolUserNo(String volUserNo) {
        this.volUserNo = volUserNo;
    }

    public String getElderlyNo() {
        return elderlyNo;
    }

    public void setElderlyNo(String elderlyNo) {
        this.elderlyNo = elderlyNo;
    }

    public String getServContent() {
        return servContent;
    }

    public void setServContent(String servContent) {
        this.servContent = servContent;
    }

    public String getServTime() {
        return servTime;
    }

    public void setServTime(String servTime) {
        this.servTime = servTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getServTelephone() {
        return servTelephone;
    }

    public void setServTelephone(String servTelephone) {
        this.servTelephone = servTelephone;
    }

    public String getServAddress() {
        return servAddress;
    }

    public void setServAddress(String servAddress) {
        this.servAddress = servAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
