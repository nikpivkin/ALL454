package io.jenkins.plugins.notify.model;

import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.notify.EventType;

public class JobState {
    private final String name;
    private final String displayName;
    private final String url;
    private final BuildState buildState;

    public JobState(final Run run, EventType eventType) {
        final Job job = run.getParent();
        this.name = job.getName();
        this.displayName = job.getDisplayName();
        this.url = job.getUrl();
        this.buildState = new BuildState(run, eventType);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUrl() {
        return url;
    }

    public BuildState getBuildState() {
        return buildState;
    }
}
