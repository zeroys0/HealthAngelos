package net.leelink.healthangelos.bean;

import java.util.UUID;

public class DetailItem {
    public static int TYPE_SERVICE  = 1001;
    public static int TYPE_CHARACTER = 1002;
    int type;
    public java.util.UUID UUID ;
    public UUID Uuid;

    public DetailItem(int type ,java.util.UUID UUID, java.util.UUID uuid) {
        this. type = type;
        this.UUID = UUID;
        Uuid = uuid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public java.util.UUID getUUID() {
        return UUID;
    }

    public void setUUID(java.util.UUID UUID) {
        this.UUID = UUID;
    }

    public java.util.UUID getUuid() {
        return Uuid;
    }

    public void setUuid(java.util.UUID uuid) {
        Uuid = uuid;
    }
}
