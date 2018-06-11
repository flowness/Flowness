package com.aquasafe.model;

public enum ChartOptions {
    LAST_HOUR("Last Hour", "HH:mm"),
    LAST_DAY("Last Day", "HH:mm"),
    LAST_WEEK("Last Week", "E"),
    LAST_MONTH("Last Month", "MMM d"),
    LAST_YEAR("Last Year", "MMM y");

    private String friendlyName;
    private String xDomainLabelPattern;

    ChartOptions(String friendlyName, String labelPattern){
        this.friendlyName = friendlyName;
        this.xDomainLabelPattern = labelPattern;
    }

    public String getxDomainLabelPattern() {
        return xDomainLabelPattern;
    }

    @Override
    public String toString(){
        return friendlyName;
    }
}
