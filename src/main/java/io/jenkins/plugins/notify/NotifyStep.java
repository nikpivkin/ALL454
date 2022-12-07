package io.jenkins.plugins.notify;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.workflow.steps.*;

import java.util.Set;

public class NotifyStep extends Step {

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new Execution(this, context);
    }

    @Extension(optional = true)
    public static class DescriptorImpl extends StepDescriptor{

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Set.of(TaskListener.class, Run.class);
        }

        @Override
        public String getFunctionName() {
            return "notify";
        }

        @Override
        @NonNull
        public String getDisplayName() {
            return "Push build state to JIRA";
        }
    }

    public static class Execution extends SynchronousNonBlockingStepExecution<Void> {
        private static final long serialVersionUID = 1L;

        protected final transient Step step;

        protected Execution(Step step, @NonNull StepContext context) {
            super(context);
            this.step = step;
        }

        @Override
        protected Void run() throws Exception {
            DefaultNotifier.getInstance().notify(
                    getContext().get(Run.class),
                    getContext().get(TaskListener.class),
                    EventType.NONE
            );
            return null;
        }
    }
}
