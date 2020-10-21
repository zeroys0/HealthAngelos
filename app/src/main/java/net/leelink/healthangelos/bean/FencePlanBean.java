package net.leelink.healthangelos.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class FencePlanBean implements Parcelable {


    private String id;
    private String elderlyId;
    private String memberId;
    private String scopeId;
    private String startTime;
    private String stopTime;
    private String monitorDate;
    private String timeInterval;
    private int cycleType;
    private String weeks;
    private int alarmWay;
    private String cellphoneNumber;
    private String mailAddress;
    private Object createBy;
    private String createTime;
    private Object updateBy;
    private String updateTime;
    private String scopeName;
    private Double la1;
    private Double lo1;
    private Double la2;
    private Double lo2;

    protected FencePlanBean(Parcel in) {
        id = in.readString();
        elderlyId = in.readString();
        memberId = in.readString();
        scopeId = in.readString();
        startTime = in.readString();
        stopTime = in.readString();
        monitorDate = in.readString();
        timeInterval = in.readString();
        cycleType = in.readInt();
        weeks = in.readString();
        alarmWay = in.readInt();
        cellphoneNumber = in.readString();
        mailAddress = in.readString();
        createTime = in.readString();
        updateTime = in.readString();
        scopeName = in.readString();
        la1 = in.readDouble();
        lo1 = in.readDouble();
        la2 = in.readDouble();
        lo2 = in.readDouble();
    }

    public static final Creator<FencePlanBean> CREATOR = new Creator<FencePlanBean>() {
        @Override
        public FencePlanBean createFromParcel(Parcel in) {
            return new FencePlanBean(in);
        }

        @Override
        public FencePlanBean[] newArray(int size) {
            return new FencePlanBean[size];
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

    public String getScopeId() {
        return scopeId;
    }

    public void setScopeId(String scopeId) {
        this.scopeId = scopeId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getMonitorDate() {
        return monitorDate;
    }

    public void setMonitorDate(String monitorDate) {
        this.monitorDate = monitorDate;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }

    public int getCycleType() {
        return cycleType;
    }

    public void setCycleType(int cycleType) {
        this.cycleType = cycleType;
    }

    public String getWeeks() {
        return weeks;
    }

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

    public int getAlarmWay() {
        return alarmWay;
    }

    public void setAlarmWay(int alarmWay) {
        this.alarmWay = alarmWay;
    }

    public String getCellphoneNumber() {
        return cellphoneNumber;
    }

    public void setCellphoneNumber(String cellphoneNumber) {
        this.cellphoneNumber = cellphoneNumber;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
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

    public String getScopeName() {
        return scopeName;
    }

    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(elderlyId);
        dest.writeString(memberId);
        dest.writeString(scopeId);
        dest.writeString(startTime);
        dest.writeString(stopTime);
        dest.writeString(monitorDate);
        dest.writeString(timeInterval);
        dest.writeInt(cycleType);
        dest.writeString(weeks);
        dest.writeInt(alarmWay);
        dest.writeString(cellphoneNumber);
        dest.writeString(mailAddress);
        dest.writeString(createTime);
        dest.writeString(updateTime);
        dest.writeString(scopeName);
        dest.writeDouble(la1);
        dest.writeDouble(lo1);
        dest.writeDouble(la2);
        dest.writeDouble(lo2);
    }
}
