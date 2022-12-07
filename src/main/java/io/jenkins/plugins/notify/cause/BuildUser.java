package io.jenkins.plugins.notify.cause;

public class BuildUser {

    public final static BuildUser EMPTY = new BuildUser();
    private String fullName;
    private String userId;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
