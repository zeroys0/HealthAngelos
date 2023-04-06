package net.leelink.healthangelos.activity.ElectricMachine;

import java.io.Serializable;

public class ElectAlarmBean implements Serializable {
    String alarmId;
    String alarmName;
    String alarmRule;
    String alarmTime;
    String alarmType;
    String description;
    String deviceId;
    String familyId;
    String id;
    String restoreRule;
    String restoreTime;
    String status;

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getAlarmRule() {
        return alarmRule;
    }

    public void setAlarmRule(String alarmRule) {
        this.alarmRule = alarmRule;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestoreRule() {
        return restoreRule;
    }

    public void setRestoreRule(String restoreRule) {
        this.restoreRule = restoreRule;
    }

    public String getRestoreTime() {
        return restoreTime;
    }

    public void setRestoreTime(String restoreTime) {
        this.restoreTime = restoreTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
