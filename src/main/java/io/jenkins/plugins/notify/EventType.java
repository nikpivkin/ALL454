package io.jenkins.plugins.notify;

public enum EventType {
    INITIALIZED, STARTED, COMPLETED, FINALIZED, DELETED, NONE;

    public boolean isEqual(String other) {
        return other != null && this.toString().equals(other);
    }
}
