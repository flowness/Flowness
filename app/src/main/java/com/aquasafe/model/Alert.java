package com.aquasafe.model;

import java.util.Date;

public class Alert {
    private Date alertDate;
    private int alertType;
    private boolean alertApproved;
    private String alertId;

    public Alert(Date alertDate, int alertType, boolean alertApproved, String alertId) {
        this.alertDate = alertDate;
        this.alertType = alertType;
        this.alertApproved = alertApproved;
        this.alertId = alertId;
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

    public String getAlertId() {
        return alertId;
    }

    public void setAlertApproved() {
        this.alertApproved = true;
    }
}
