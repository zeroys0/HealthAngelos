package net.leelink.healthangelos.bean;

public class EatBean {

    private int recordTypeState;
    private int totalHeav;
    private int totalKcal;
    private String foodName;

    public int getRecordTypeState() {
        return recordTypeState;
    }

    public void setRecordTypeState(int recordTypeState) {
        this.recordTypeState = recordTypeState;
    }

    public int getTotalHeav() {
        return totalHeav;
    }

    public void setTotalHeav(int totalHeav) {
        this.totalHeav = totalHeav;
    }

    public int getTotalKcal() {
        return totalKcal;
    }

    public void setTotalKcal(int totalKcal) {
        this.totalKcal = totalKcal;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }
}
