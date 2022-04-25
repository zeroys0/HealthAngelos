package net.leelink.healthangelos.activity.ssk;

import igs.android.protocol.bluetooth.bean.shared.SensorBean;

public class SSKDeviceBean {
    SensorBean sensorBean;
    boolean online;
    boolean is_bind;

    public SSKDeviceBean(SensorBean sensorBean, boolean online,boolean is_bind) {
        this.sensorBean = sensorBean;
        this.online = online;
        this.is_bind = is_bind;
    }



    public SensorBean getSensorBean() {
        return sensorBean;
    }

    public void setSensorBean(SensorBean sensorBean) {
        this.sensorBean = sensorBean;
    }

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
