package net.leelink.healthangelos.activity.ZW011;

public class SettingBean {
    boolean cb_check;
    String freq;
    int type;

    public SettingBean(boolean cb_check, String freq,int type) {
        this.cb_check = cb_check;
        this.freq = freq;
        this.type = type;
    }

    public boolean isCb_check() {
        return cb_check;
    }

    public String getFreq() {
        return freq;
    }

    public int getType() {
        return type;
    }
}
