package net.leelink.healthangelos.bean;

public class UserInfo {
    private String olderlyId;
    private int jwotchState;
    private String jwotchImei;

    private String organId="";

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public String getOlderlyId() {
        return olderlyId;
    }

    public void setOlderlyId(String olderlyId) {
        this.olderlyId = olderlyId;
    }

    public int getJwotchState() {
        return jwotchState;
    }

    public void setJwotchState(int jwotchState) {
        this.jwotchState = jwotchState;
    }

    public String getJwotchImei() {
        return jwotchImei;
    }

    public void setJwotchImei(String jwotchImei) {
        this.jwotchImei = jwotchImei;
    }
}
