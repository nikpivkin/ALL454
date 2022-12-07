package io.jenkins.plugins.notify.cause.determinant.impl;

import hudson.model.Cause;
import io.jenkins.plugins.notify.cause.BuildUser;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;

public class UserIdCauseDeterminant implements CauseDeterminant<Cause.UserIdCause> {
    @Override
    public BuildUser determine(Cause.UserIdCause cause) {
        BuildUser buildUser = new BuildUser();
        buildUser.setUserId(cause.getUserId());
        buildUser.setFullName(cause.getUserName());
        return buildUser;
    }

    @Override
    public Class<Cause.UserIdCause> getCauseClass() {
        return Cause.UserIdCause.class;
    }
}
