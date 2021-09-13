package com.conaxgames.libraries.nms.management;

import com.conaxgames.libraries.nms.management.utility.UtilityNMSManager;

public abstract class LibNMSManagers {

    protected UtilityNMSManager utilityManager;

    public LibNMSManagers(UtilityNMSManager utilityManager) {
        this.utilityManager = utilityManager;
    }

    public UtilityNMSManager getUtilityManager() {
        return this.utilityManager;
    }
}

