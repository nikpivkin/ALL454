package io.jenkins.plugins.notify.model;

import hudson.EnvVars;
import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.notify.EventType;
import io.jenkins.plugins.notify.cause.UnifiedBuildCause;
import io.jenkins.plugins.notify.cause.BuildCauseService;
import jenkins.model.Jenkins;

/**
 * A state of Jenkins Build. @see <a href="https://www.jenkins.io/doc/book/glossary/#build">build</a>
 */
public class BuildState {
    private final long timestamp;
    private final long duration;
    private final String id;
    private final long number;
    private final long queueId;
    private final String url;
    private final String fullUrl;
    private final String result;
    private final UnifiedBuildCause cause;
    private final EventType eventType;
    private final ScmState scmState;

    public BuildState(Run run, EventType eventType, EnvVars envVars) {
        this.timestamp = run.getTimeInMillis();
        this.duration = run.getDuration();
        this.id = run.getId();
        this.number = run.getNumber();
        this.queueId = run.getQueueId();
        this.url = run.getUrl();
        final String rootUrl = Jenkins.get().getRootUrl();
        this.fullUrl = rootUrl != null ? rootUrl + run.getUrl() : null ;
        this.result = run.getResult() != null ? run.getResult().toExportedObject() : "";
        this.cause = BuildCauseService.getInstance().build(run);
        this.eventType = eventType;
        this.scmState = new ScmState(envVars);
    }

    /**
     * The same as {@link Run#getTimeInMillis()}
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * The same as {@link Run#getDuration()}
     */
    public long getDuration() {
        return duration;
    }

    /**
     * The same as {@link Run#getId()}
     */
    public String getId() {
        return id;
    }

    /**
     * The same as {@link Run#getNumber()}
     */
    public long getNumber() {
        return number;
    }

    /**
     * The same as {@link Run#getQueueId()}
     */
    public long getQueueId() {
        return queueId;
    }

    /**
     * The same as {@link Run#getUrl()}
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the full URL of the job, consisting of the {@link Jenkins#getRootUrl() absolute path of Jenkins}
     * and the {@link Job#getUrl() relative path of the job}
     */
    public String getFullUrl() {
        return fullUrl;
    }

    /**
     * The same as {@link Run#getResult()}
     */
    public String getResult() {
        return result;
    }

    public UnifiedBuildCause getCause() {
        return cause;
    }

    public EventType getEventType() {
        return eventType;
    }

    public ScmState getScmState() {
        return scmState;
    }
}
