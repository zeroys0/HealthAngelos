package net.leelink.healthangelos.bean;

import java.util.List;

public class A6gBean {
    private String name;
    private Boolean is_show = false;
    private List<A6gBloodPressureBean> list = null;

    public List<A6gBloodPressureBean> getList() {
        return list;
    }

    public void setList(List<A6gBloodPressureBean> list) {
        this.list = list;
    }

    public A6gBean(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIs_show() {
        return is_show;
    }

    public void setIs_show(Boolean is_show) {
        this.is_show = is_show;
    }
}
