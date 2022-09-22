package net.leelink.healthangelos.bean;

import java.util.List;

public class G7gBean {
    private String name;
    private Boolean is_show = false;
    private List<G7gBloodSugarBean> list = null;

    public List<G7gBloodSugarBean> getList() {
        return list;
    }

    public void setList(List<G7gBloodSugarBean> list) {
        this.list = list;
    }

    public G7gBean(String name) {
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
