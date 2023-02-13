package io.jenkins.plugins.notify.model;

import hudson.EnvVars;
import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.notify.EventType;
import jenkins.model.Jenkins;

/**
 * A state of Jenkins Job
 */
public class JobState {
    private final String name;
    private final String displayName;
    private final String url;
    private final String fullUrl;
    private final BuildState buildState;

    public JobState(final Run run, EventType eventType, EnvVars envVars) {
        final Job job = run.getParent();
        this.name = job.getName();
        this.displayName = job.getDisplayName();
        this.url = job.getUrl();
        String rootUrl = Jenkins.get().getRootUrl();
        this.fullUrl = rootUrl != null ? rootUrl + job.getUrl() : null ;
        this.buildState = new BuildState(run, eventType, envVars);
    }

    /**
     * The same as {@link Job#getName()}
     * @return Project name
     */
    public String getName() {
        return name;
    }

    /**
     * The same as {@link Job#getDisplayName()}
     * @return Display name of the Project
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * The same as {@link Job#getUrl()}
     * @return Project URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Returns the full URL of the job, consisting of the {@link Jenkins#getRootUrl() absolute path of Jenkins}
     * and the {@link Job#getUrl() relative path of the job}
     * @return Full URL of the Project
     */
    public String getFullUrl() {
        return fullUrl;
    }

    public BuildState getBuildState() {
        return buildState;
    }
}
