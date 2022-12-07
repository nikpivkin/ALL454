package io.jenkins.plugins.notify.cause.determinant;

import hudson.model.Cause;
import io.jenkins.plugins.notify.cause.BuildUser;

public interface CauseDeterminant<T extends Cause> {
    BuildUser determine(T cause);

    Class<T> getCauseClass();
}
