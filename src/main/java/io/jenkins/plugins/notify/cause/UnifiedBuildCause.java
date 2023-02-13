package io.jenkins.plugins.notify.cause;

/**
 * Unified cause for the build
 */
public class UnifiedBuildCause {

    public static final UnifiedBuildCause EMPTY = new UnifiedBuildCause();
    private String name;
    private String id;

    /**
     * @return Name of the cause. "Remote", "SCM", "TimerTrigger"
     * or another string value as the user id
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
