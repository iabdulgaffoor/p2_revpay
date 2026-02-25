package com.revpay.domain.enums;

public enum BankName {

    STATE_BANK_OF_INDIA("State Bank of India"),
    HDFC_BANK("HDFC Bank"),
    ICICI_BANK("ICICI Bank"),
    AXIS_BANK("Axis Bank");

    private final String displayName;

    BankName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}