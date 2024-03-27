package net.leelink.healthangelos.activity.NBdevice.adapter;

import java.util.List;

public class TiTimeBean {
    private int year;
    private int month;
    private int day;
    private List<TiBean> list;

    public TiTimeBean(int year, int month, int day, List<TiBean> list) {
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

    public List<TiBean> getList() {
        return list;
    }

    public void setList(List<TiBean> list) {
        this.list = list;
    }
}
