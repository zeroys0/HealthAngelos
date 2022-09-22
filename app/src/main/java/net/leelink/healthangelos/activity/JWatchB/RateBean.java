package net.leelink.healthangelos.activity.JWatchB;

public class RateBean {
    int second;
    String time;

    public RateBean(int second, String time) {
        this.second = second;
        this.time = time;
    }

    public int getSecond() {
        return second;
    }

    public String getTime() {
        return time;
    }
}
