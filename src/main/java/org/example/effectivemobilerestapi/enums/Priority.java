package org.example.effectivemobilerestapi.enums;

public enum Priority {
    HIGH(3),
    MEDIUM(2),
    LOW(1);

    private final int displayName;

    Priority(int displayName) {
        this.displayName = displayName;
    }

    public int getDisplayName() {
        return displayName;
    }
}
