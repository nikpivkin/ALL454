package io.jenkins.plugins.notify.cause.determinant.impl;

import hudson.Extension;
import hudson.model.Cause;
import io.jenkins.plugins.notify.cause.UnifiedBuildCause;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;

@Extension
public class RemoteCauseDeterminant implements CauseDeterminant<Cause.RemoteCause> {

    @Override
    public UnifiedBuildCause determine(hudson.model.Cause.RemoteCause cause) {
        UnifiedBuildCause buildUser = new UnifiedBuildCause();
        buildUser.setId("Remote");
        buildUser.setName(String.format("%s %s", cause.getAddr(), cause.getNote()));
        return buildUser;
    }

    @Override
    public Class<hudson.model.Cause.RemoteCause> getCauseClass() {
        return Cause.RemoteCause.class;
    }
}
