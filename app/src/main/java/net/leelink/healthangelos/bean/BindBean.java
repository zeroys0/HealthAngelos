package net.leelink.healthangelos.bean;

import java.io.Serializable;

public class BindBean implements Serializable {


    private int recordId;
    private int elderlyId;
    private Object committeeId;
    private int bindState;
    private int authState;
    private String bindTime;
    private String householdAddress;
    private String currentAddress;
    private String authTime;
    private String applyTime;
    private String elderlyName;
    private String idNumber;
    private String telephone;
    private String idNumberPositive;
    private String idNumberReverse;
    private String vertifyContent;
    private String committeeName;
    private int sex;
    private String sysSign;

    public String getSysSign() {
        return sysSign;
    }

    public void setSysSign(String sysSign) {
        this.sysSign = sysSign;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(int elderlyId) {
        this.elderlyId = elderlyId;
    }

    public Object getCommitteeId() {
        return committeeId;
    }

    public void setCommitteeId(Object committeeId) {
        this.committeeId = committeeId;
    }

    public int getBindState() {
        return bindState;
    }

    public void setBindState(int bindState) {
        this.bindState = bindState;
    }

    public int getAuthState() {
        return authState;
    }

    public void setAuthState(int authState) {
        this.authState = authState;
    }

    public String getBindTime() {
        return bindTime;
    }

    public void setBindTime(String bindTime) {
        this.bindTime = bindTime;
    }

    public String getHouseholdAddress() {
        return householdAddress;
    }

    public void setHouseholdAddress(String householdAddress) {
        this.householdAddress = householdAddress;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public String getAuthTime() {
        return authTime;
    }

    public void setAuthTime(String authTime) {
        this.authTime = authTime;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public String getElderlyName() {
        return elderlyName;
    }

    public void setElderlyName(String elderlyName) {
        this.elderlyName = elderlyName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getIdNumberPositive() {
        return idNumberPositive;
    }

    public void setIdNumberPositive(String idNumberPositive) {
        this.idNumberPositive = idNumberPositive;
    }

    public String getIdNumberReverse() {
        return idNumberReverse;
    }

    public void setIdNumberReverse(String idNumberReverse) {
        this.idNumberReverse = idNumberReverse;
    }

    public String getVertifyContent() {
        return vertifyContent;
    }

    public void setVertifyContent(String vertifyContent) {
        this.vertifyContent = vertifyContent;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
