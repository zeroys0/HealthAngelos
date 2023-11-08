package net.leelink.healthangelos.activity.NBdevice;

import java.util.List;

public class NbMsgTimeBean {
    private int year;
    private int month;
    private int day;
    private List<NbMsgBean> list;

    public NbMsgTimeBean(int year, int month, int day, List<NbMsgBean> list) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.list = list;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public List<NbMsgBean> getList() {
        return list;
    }

    public void setList(List<NbMsgBean> list) {
        this.list = list;
    }
}
