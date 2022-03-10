package net.leelink.healthangelos.bean;

public class SkrHistoryBean {


    private String id;
    private int elderlyId;
    private String account;
    private String cid;
    private String defence;
    private String content;
    private String createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(int elderlyId) {
        this.elderlyId = elderlyId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDefence() {
        return defence;
    }

    public void setDefence(String defence) {
        this.defence = defence;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
