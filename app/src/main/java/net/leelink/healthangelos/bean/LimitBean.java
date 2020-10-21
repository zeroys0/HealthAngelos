package net.leelink.healthangelos.bean;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;


public class LimitBean implements Parcelable {


    private String id;
    private String elderlyId;
    private String memberId;
    private String alias;
    private Double la1;
    private Double lo1;
    private Double la2;
    private Double lo2;
    private Object createBy;
    private String createTime;
    private Object updateBy;
    private String updateTime;
    private String aaddress;
    private String baddress;

    protected LimitBean(Parcel in) {
        id = in.readString();
        elderlyId = in.readString();
        memberId = in.readString();
        alias = in.readString();
        if (in.readByte() == 0) {
            la1 = null;
        } else {
            la1 = in.readDouble();
        }
        if (in.readByte() == 0) {
            lo1 = null;
        } else {
            lo1 = in.readDouble();
        }
        if (in.readByte() == 0) {
            la2 = null;
        } else {
            la2 = in.readDouble();
        }
        if (in.readByte() == 0) {
            lo2 = null;
        } else {
            lo2 = in.readDouble();
        }
        createTime = in.readString();
        updateTime = in.readString();
        aaddress = in.readString();
        baddress = in.readString();
    }

    public static final Creator<LimitBean> CREATOR = new Creator<LimitBean>() {
        @Override
        public LimitBean createFromParcel(Parcel in) {
            return new LimitBean(in);
        }

        @Override
        public LimitBean[] newArray(int size) {
            return new LimitBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(String elderlyId) {
        this.elderlyId = elderlyId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Double getLa1() {
        return la1;
    }

    public void setLa1(Double la1) {
        this.la1 = la1;
    }

    public Double getLo1() {
        return lo1;
    }

    public void setLo1(Double lo1) {
        this.lo1 = lo1;
    }

    public Double getLa2() {
        return la2;
    }

    public void setLa2(Double la2) {
        this.la2 = la2;
    }

    public Double getLo2() {
        return lo2;
    }

    public void setLo2(Double lo2) {
        this.lo2 = lo2;
    }

    public Object getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Object createBy) {
        this.createBy = createBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Object getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Object updateBy) {
        this.updateBy = updateBy;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAaddress() {
        return aaddress;
    }

    public void setAaddress(String aaddress) {
        this.aaddress = aaddress;
    }

    public String getBaddress() {
        return baddress;
    }

    public void setBaddress(String baddress) {
        this.baddress = baddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(elderlyId);
        dest.writeString(memberId);
        dest.writeString(alias);
        if (la1 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(la1);
        }
        if (lo1 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lo1);
        }
        if (la2 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(la2);
        }
        if (lo2 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(lo2);
        }
        dest.writeString(createTime);
        dest.writeString(updateTime);
        dest.writeString(aaddress);
        dest.writeString(baddress);
    }
}
