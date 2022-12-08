package io.jenkins.plugins.notify.model;

import hudson.EnvVars;

public class ScmState {

    private final String url;
    private final String branch;
    private final String commit;

    public ScmState(EnvVars envVars) {
        url = envVars.get( "GIT_URL" );
        branch = envVars.get("GIT_BRANCH");
        commit = envVars.get("GIT_COMMIT");
    }
}
