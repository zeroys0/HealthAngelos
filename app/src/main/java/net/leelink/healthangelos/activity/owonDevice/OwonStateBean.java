package net.leelink.healthangelos.activity.owonDevice;

import com.google.gson.annotations.SerializedName;

public class OwonStateBean {

    @SerializedName("key")
    private String key;
    @SerializedName("value")
    private int value;
    @SerializedName("title")
    private String title;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
