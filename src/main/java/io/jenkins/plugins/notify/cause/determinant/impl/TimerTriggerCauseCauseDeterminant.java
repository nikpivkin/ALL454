package io.jenkins.plugins.notify.cause.determinant.impl;

import hudson.triggers.TimerTrigger;
import io.jenkins.plugins.notify.cause.BuildUser;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;

public class TimerTriggerCauseCauseDeterminant implements CauseDeterminant<TimerTrigger.TimerTriggerCause> {
    @Override
    public BuildUser determine(TimerTrigger.TimerTriggerCause cause) {
        BuildUser buildUser = new BuildUser();
        buildUser.setUserId("TimerTrigger");
        return buildUser;
    }

    @Override
    public Class<TimerTrigger.TimerTriggerCause> getCauseClass() {
        return TimerTrigger.TimerTriggerCause.class;
    }


}
