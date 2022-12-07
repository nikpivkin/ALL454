package io.jenkins.plugins.notify;

public enum EventType {
    INITIALIZED, STARTED, COMPLETED, FINALIZED, DELETED, NONE;

    public boolean isEqualInLower(String other) {
        return other != null && this.toString().toLowerCase().equals(other);
    }
}
