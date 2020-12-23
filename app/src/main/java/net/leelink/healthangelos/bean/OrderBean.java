package net.leelink.healthangelos.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderBean implements Parcelable {


    private String orderId;
    private String name;
    private String imgPath;
    private String duties;
    private String department;
    private String hospital;
    private String remark;
    private double actPayPrice;
    private int state;
    private String clientId;
    private String createTime;

    protected OrderBean(Parcel in) {
        orderId = in.readString();
        name = in.readString();
        imgPath = in.readString();
        duties = in.readString();
        department = in.readString();
        hospital = in.readString();
        remark = in.readString();
        actPayPrice = in.readDouble();
        state = in.readInt();
        clientId = in.readString();
        createTime = in.readString();
    }

    public static final Creator<OrderBean> CREATOR = new Creator<OrderBean>() {
        @Override
        public OrderBean createFromParcel(Parcel in) {
            return new OrderBean(in);
        }

        @Override
        public OrderBean[] newArray(int size) {
            return new OrderBean[size];
        }
    };

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getDuties() {
        return duties;
    }

    public void setDuties(String duties) {
        this.duties = duties;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public double getActPayPrice() {
        return actPayPrice;
    }

    public void setActPayPrice(double actPayPrice) {
        this.actPayPrice = actPayPrice;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(name);
        dest.writeString(imgPath);
        dest.writeString(duties);
        dest.writeString(department);
        dest.writeString(hospital);
        dest.writeString(remark);
        dest.writeDouble(actPayPrice);
        dest.writeInt(state);
        dest.writeString(clientId);
        dest.writeString(createTime);
    }
}
