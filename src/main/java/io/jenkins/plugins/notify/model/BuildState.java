package io.jenkins.plugins.notify.model;

import hudson.model.Result;
import hudson.model.Run;
import io.jenkins.plugins.notify.EventType;
import io.jenkins.plugins.notify.cause.BuildUser;
import io.jenkins.plugins.notify.cause.BuildUserService;

public class BuildState {
    private final long timestamp;
    private final long duration;
    private final String id;
    private final long number;
    private final long queueId;
    private final String result;
    private final BuildUser cause;
    private final EventType eventType;

    public BuildState(Run run, EventType eventType) {
        this.timestamp = run.getTimeInMillis();
        this.duration = run.getDuration();
        this.id = run.getId();
        this.number = run.getNumber();
        this.queueId = run.getQueueId();
        Result result = run.getResult();
        this.result = result != null ? result.toExportedObject() : "";
        this.cause = BuildUserService.getInstance().build(run);
        this.eventType = eventType;
    }
}
