package io.jenkins.plugins.notify.cause.determinant.impl;

import hudson.triggers.SCMTrigger;
import io.jenkins.plugins.notify.cause.BuildUser;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;

public class SCMTriggerCauseDeterminant implements CauseDeterminant<SCMTrigger.SCMTriggerCause> {
    @Override
    public BuildUser determine(SCMTrigger.SCMTriggerCause cause) {
        BuildUser buildUser = new BuildUser();
        buildUser.setUserId("SCM");
        return buildUser;
    }

    @Override
    public Class<SCMTrigger.SCMTriggerCause> getCauseClass() {
        return SCMTrigger.SCMTriggerCause.class;
    }
}
