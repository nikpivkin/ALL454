package io.jenkins.plugins.notify;

import hudson.model.Run;
import hudson.model.TaskListener;

@SuppressWarnings("rawtypes")
public interface Notifier {
    void notify(Run run, TaskListener listener, EventType eventType);
}
