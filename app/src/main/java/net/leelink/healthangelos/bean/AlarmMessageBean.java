package net.leelink.healthangelos.bean;

public class AlarmMessageBean {

    private String id;
    private String elderlyId;
    private String memberId;
    private String testTime;
    private String type;
    private String source;
    private double currentData;
    private String refeRange;
    private int createBy;
    private String createTime;
    private int updateBy;
    private String updateTime;
    private int flag;

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

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getTestTime() {
        return testTime;
    }

    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getCurrentData() {
        return currentData;
    }

    public void setCurrentData(double currentData) {
        this.currentData = currentData;
    }

    public String getRefeRange() {
        return refeRange;
    }

    public void setRefeRange(String refeRange) {
        this.refeRange = refeRange;
    }

    public int getCreateBy() {
        return createBy;
    }

    public void setCreateBy(int createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(int updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
