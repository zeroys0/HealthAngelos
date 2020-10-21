package net.leelink.healthangelos.bean;

public class AlarmBean {

    /**
     * id : 1
     * productName : 血压异常
     * price : 10
     * endTime : null
     * existState : 0
     */

    private String id;
    private String productName;
    private String price;
    private String endTime;
    private int existState;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getExistState() {
        return existState;
    }

    public void setExistState(int existState) {
        this.existState = existState;
    }
}
