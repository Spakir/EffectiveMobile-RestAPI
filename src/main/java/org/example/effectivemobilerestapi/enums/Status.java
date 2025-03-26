package org.example.effectivemobilerestapi.enums;

public enum Status {
    WAITING("В ожидании"),
    PROCESSING("В процессе"),
    COMPLETED("Завершено");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}