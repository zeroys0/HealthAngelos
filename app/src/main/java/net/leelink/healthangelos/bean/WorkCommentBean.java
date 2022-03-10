package net.leelink.healthangelos.bean;

public class WorkCommentBean {

    /**
     * id : 47
     * productionId : 29
     * commentContent : 此物只应天上有
     * elderlyId : 2
     * elderlyName : 朋克泡安利
     * elderlyImgName : null
     * replyElderlyId : null
     * replyElderlyName : null
     * replyElderlyImgName : null
     * commentTime : 2021-09-03 16:13:05
     */

    private int id;
    private int productionId;
    private String commentContent;
    private int elderlyId;
    private String elderlyName;
    private String elderlyImgName;
    private String replyElderlyId;
    private String replyElderlyName;
    private String replyElderlyImgName;
    private String commentTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductionId() {
        return productionId;
    }

    public void setProductionId(int productionId) {
        this.productionId = productionId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public int getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(int elderlyId) {
        this.elderlyId = elderlyId;
    }

    public String getElderlyName() {
        return elderlyName;
    }

    public void setElderlyName(String elderlyName) {
        this.elderlyName = elderlyName;
    }

    public Object getElderlyImgName() {
        return elderlyImgName;
    }

    public void setElderlyImgName(String elderlyImgName) {
        this.elderlyImgName = elderlyImgName;
    }

    public Object getReplyElderlyId() {
        return replyElderlyId;
    }

    public void setReplyElderlyId(String replyElderlyId) {
        this.replyElderlyId = replyElderlyId;
    }

    public Object getReplyElderlyName() {
        return replyElderlyName;
    }

    public void setReplyElderlyName(String replyElderlyName) {
        this.replyElderlyName = replyElderlyName;
    }

    public Object getReplyElderlyImgName() {
        return replyElderlyImgName;
    }

    public void setReplyElderlyImgName(String replyElderlyImgName) {
        this.replyElderlyImgName = replyElderlyImgName;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }
}
