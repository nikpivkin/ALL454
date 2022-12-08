package io.jenkins.plugins.notify.model;

import hudson.EnvVars;
import hudson.model.Result;
import hudson.model.Run;
import io.jenkins.plugins.notify.EventType;
import io.jenkins.plugins.notify.cause.BuildUser;
import io.jenkins.plugins.notify.cause.BuildUserService;
import jenkins.model.Jenkins;

public class BuildState {
    private final long timestamp;
    private final long duration;
    private final String id;
    private final long number;
    private final long queueId;
    private final String url;
    private String fullUrl;
    private final String result;
    private final BuildUser cause;
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
        if (rootUrl != null) {
            this.fullUrl = rootUrl + run.getUrl();
        }
        Result result = run.getResult();
        this.result = result != null ? result.toExportedObject() : "";
        this.cause = BuildUserService.getInstance().build(run);
        this.eventType = eventType;
        this.scmState = new ScmState(envVars);
    }
}
