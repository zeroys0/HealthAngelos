package net.leelink.healthangelos.activity.R60flRadar;

import java.util.List;

public class R60TimeBean {
    private int year;
    private int month;
    private int day;
    private List<R60Bean> list;

    public R60TimeBean(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public R60TimeBean(int year, int month, int day, List<R60Bean> list) {
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

    public List<R60Bean> getList() {
        return list;
    }

    public void setList(List<R60Bean> list) {
        this.list = list;
    }
}
