package com.medilabo.assessment_service.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RiskLevel {

    NONE("None"),
    BORDERLINE("Borderline"),
    IN_DANGER("InDanger"),
    EARLY_ONSET("EarlyOnset");

    private final String label;

    RiskLevel(String label) {
        this.label = label;
    }
}
