package io.jenkins.plugins.notify;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

@Extension
@SuppressWarnings("rawtypes")
public class JobListener extends RunListener<Run> {

    private static final Notifier notifier = DefaultNotifier.getInstance();

    @Override
    public void onCompleted(Run run, @NonNull TaskListener listener) {
        notifier.notify(run, listener, EventType.COMPLETED);
    }

    @Override
    public void onFinalized(Run run) {
        notifier.notify(run, TaskListener.NULL, EventType.FINALIZED);
    }

    @Override
    public void onInitialize(Run run) {

        notifier.notify(run, TaskListener.NULL, EventType.INITIALIZED);
    }

    @Override
    public void onStarted(Run run, TaskListener listener) {
        notifier.notify(run, listener, EventType.STARTED);
    }

    @Override
    public void onDeleted(Run run) {
        notifier.notify(run, TaskListener.NULL, EventType.DELETED);
    }
}
