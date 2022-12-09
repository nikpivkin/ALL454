package io.jenkins.plugins.notify.model;

import hudson.EnvVars;
import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.notify.EventType;
import jenkins.model.Jenkins;

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
}
