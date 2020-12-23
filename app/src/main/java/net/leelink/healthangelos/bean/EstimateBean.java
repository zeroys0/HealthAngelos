package net.leelink.healthangelos.bean;

public class EstimateBean {


    private String id;
    private String questionTitle;
    private String questionRemark;
    private int questionType;
    private String questionContent;
    private Object questionAddress;
    private String updateTime;
    private String updateName;
    private int state;
    private int organId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getQuestionRemark() {
        return questionRemark;
    }

    public void setQuestionRemark(String questionRemark) {
        this.questionRemark = questionRemark;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public Object getQuestionAddress() {
        return questionAddress;
    }

    public void setQuestionAddress(Object questionAddress) {
        this.questionAddress = questionAddress;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getOrganId() {
        return organId;
    }

    public void setOrganId(int organId) {
        this.organId = organId;
    }
}
