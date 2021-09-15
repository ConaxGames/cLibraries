package com.conaxgames.libraries.nms;

public enum LibServerVersion {
    v1_8_R3,
    v1_12_R1,
    v1_16_R3,
    v1_17_R1;

    public boolean after(LibServerVersion serverVersion) {
        return this.ordinal() > serverVersion.ordinal();
    }

    public boolean before(LibServerVersion serverVersion) {
        return serverVersion.ordinal() > this.ordinal();
    }

}