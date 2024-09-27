package net.leelink.healthangelos.activity.yasee;

import java.util.List;

public class YaseeBean {
    private String name;
    private Boolean is_show = false;
    private List<YaseeBpBean> list = null;
    private List<YaseeBsBean> list2 = null;

    public List<YaseeBpBean> getList() {
        return list;
    }

    public void setList(List<YaseeBpBean> list) {
        this.list = list;
    }

    public List<YaseeBsBean> getList2() {
        return list2;
    }

    public void setList2(List<YaseeBsBean> list2) {
        this.list2 = list2;
    }

    public YaseeBean(String name) {
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
