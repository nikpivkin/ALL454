package io.jenkins.plugins.notify.cause.determinant.impl;

import hudson.model.Cause;
import io.jenkins.plugins.notify.cause.UnifiedBuildCause;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;

public class UserIdCauseDeterminant implements CauseDeterminant<Cause.UserIdCause> {

    @Override
    public UnifiedBuildCause determine(hudson.model.Cause.UserIdCause cause) {
        UnifiedBuildCause buildUser = new UnifiedBuildCause();
        buildUser.setId(cause.getUserId());
        buildUser.setName(cause.getUserName());
        return buildUser;
    }

    @Override
    public Class<hudson.model.Cause.UserIdCause> getCauseClass() {
        return Cause.UserIdCause.class;
    }
}
