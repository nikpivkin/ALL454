package io.jenkins.plugins.notify.cause.determinant;

import hudson.model.Cause;
import io.jenkins.plugins.notify.cause.UnifiedBuildCause;

public interface CauseDeterminant<T extends Cause> {

    /**
     * Extracts useful information from Cause
     * @param cause {@link Cause}
     */
    UnifiedBuildCause determine(T cause);

    /**
     *
     * @return returns a class that the determinant can handle
     */
    Class<T> getCauseClass();
}
