package io.jenkins.plugins.notify.cause.determinant.impl;

import hudson.triggers.SCMTrigger;
import io.jenkins.plugins.notify.cause.UnifiedBuildCause;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;

public class SCMTriggerCauseDeterminant implements CauseDeterminant<SCMTrigger.SCMTriggerCause> {

    @Override
    public UnifiedBuildCause determine(SCMTrigger.SCMTriggerCause cause) {
        UnifiedBuildCause buildUser = new UnifiedBuildCause();
        buildUser.setId("SCM");
        return buildUser;
    }

    @Override
    public Class<SCMTrigger.SCMTriggerCause> getCauseClass() {
        return SCMTrigger.SCMTriggerCause.class;
    }
}
