package io.jenkins.plugins.notify.model;

import hudson.EnvVars;

/**
 * A state of the SCM
 */
public class ScmState {

    private final String url;
    private final String branch;
    private final String commit;

    public ScmState(EnvVars envVars) {
        url = envVars.get("GIT_URL");
        branch = envVars.get("GIT_BRANCH");
        commit = envVars.get("GIT_COMMIT");
    }

    /**
     * @return the base name of the remote GIT repository
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the name of the current Git branch the Jenkins Git plugin is operating upon
     */
    public String getBranch() {
        return branch;
    }

    /**
     * @return a reference to the current Git commit’s secure hash algorithm (SHA)
     */
    public String getCommit() {
        return commit;
    }
}
