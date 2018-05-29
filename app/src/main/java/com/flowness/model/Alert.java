package com.flowness.model;

import java.util.Date;

public class Alert {
    private Date alertDate;
    private int alertType;
    private boolean alertApproved;

    public Alert(Date alertDate, int alertType, boolean alertApproved) {
        this.alertDate = alertDate;
        this.alertType = alertType;
        this.alertApproved = alertApproved;
    }

    public Date getAlertDate() {
        return alertDate;
    }

    public int getAlertType() {
        return alertType;
    }

    public boolean isAlertApproved() {
        return alertApproved;
    }
}
