package net.leelink.healthangelos.bean;

public class BloodSugarBean  {


    private int su_condition;
    private String elderly_id;
    private String detection_time;
    private String blood_sugar;
    private String bloodSugerId;
    private String blood_uric;

    public String getBlood_uric() {
        return blood_uric;
    }

    public void setBlood_uric(String blood_uric) {
        this.blood_uric = blood_uric;
    }

    public int getSu_condition() {
        return su_condition;
    }

    public void setSu_condition(int su_condition) {
        this.su_condition = su_condition;
    }

    public String getElderly_id() {
        return elderly_id;
    }

    public void setElderly_id(String elderly_id) {
        this.elderly_id = elderly_id;
    }

    public String getDetection_time() {
        return detection_time;
    }

    public void setDetection_time(String detection_time) {
        this.detection_time = detection_time;
    }

    public String getBlood_sugar() {
        return blood_sugar;
    }

    public void setBlood_sugar(String blood_sugar) {
        this.blood_sugar = blood_sugar;
    }

    public String getBloodSugerId() {
        return bloodSugerId;
    }

    public void setBloodSugerId(String bloodSugerId) {
        this.bloodSugerId = bloodSugerId;
    }
}
