package io.jenkins.plugins.notify.cause.determinant.impl;

import hudson.Extension;
import hudson.model.Cause;
import io.jenkins.plugins.notify.cause.BuildUser;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;

@Extension
public class RemoteCauseDeterminant implements CauseDeterminant<Cause.RemoteCause> {
    @Override
    public BuildUser determine(Cause.RemoteCause cause) {
        BuildUser buildUser = new BuildUser();
        buildUser.setUserId("Remote");
        buildUser.setFullName(String.format("%s %s", cause.getAddr(), cause.getNote()));
        return buildUser;
    }

    @Override
    public Class<Cause.RemoteCause> getCauseClass() {
        return Cause.RemoteCause.class;
    }
}
