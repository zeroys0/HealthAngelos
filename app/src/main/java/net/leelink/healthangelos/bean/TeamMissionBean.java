package net.leelink.healthangelos.bean;

import java.io.Serializable;

public class TeamMissionBean implements Serializable {


    private String id;
    private String servTime;
    private String startTime;
    private String endTime;
    private String updateName;
    private String updateTime;
    private String servTelephone;
    private String servAddress;
    private String remark;
    private int state;
    private String servName;
    private int num;
    private String content;
    private String servTitle;
    private int myState;
    private String servContent;
    private int senderId;
    private int volId;

    public int getVolId() {
        return volId;
    }

    public void setVolId(int volId) {
        this.volId = volId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getServContent() {
        return servContent;
    }

    public void setServContent(String servContent) {
        this.servContent = servContent;
    }

    public int getMyState() {
        return myState;
    }

    public void setMyState(int myState) {
        this.myState = myState;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getServName() {
        return servName;
    }

    public void setServName(String servName) {
        this.servName = servName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
