package com.flowness.model;

import java.util.Date;

public class Alert {
    Date alertDate;
    int alertType;

    public Alert(Date alertDate, int alertType) {
        this.alertDate = alertDate;
        this.alertType = alertType;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public int getAlertType() {
        return alertType;
    }
}
