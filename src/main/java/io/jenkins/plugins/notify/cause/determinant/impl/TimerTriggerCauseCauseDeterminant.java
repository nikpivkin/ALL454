package io.jenkins.plugins.notify.cause.determinant.impl;

import hudson.triggers.TimerTrigger;
import io.jenkins.plugins.notify.cause.UnifiedBuildCause;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;

public class TimerTriggerCauseCauseDeterminant implements CauseDeterminant<TimerTrigger.TimerTriggerCause> {

    @Override
    public UnifiedBuildCause determine(TimerTrigger.TimerTriggerCause cause) {
        UnifiedBuildCause buildUser = new UnifiedBuildCause();
        buildUser.setId("TimerTrigger");
        return buildUser;
    }

    @Override
    public Class<TimerTrigger.TimerTriggerCause> getCauseClass() {
        return TimerTrigger.TimerTriggerCause.class;
    }


}
