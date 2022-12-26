package io.jenkins.plugins.notify;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import hudson.model.*;
import hudson.plugins.git.BranchSpec;
import hudson.plugins.git.GitSCM;
import hudson.tasks.BuildTrigger;
import hudson.triggers.SCMTrigger;
import hudson.triggers.TimerTrigger;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockBuilder;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

public class DefaultNotifierIntegrationTest {

    private static final String COMMIT = "60fbb1987f6178e1436a8362a441351efdb13cb3";
    private static final String REPO_URL = "https://github.com/nikpivkin/ALL454.git";
    private static final String BRANCH = "detached";
    private static final String CREDENTIALS_ID = "credentials";

    @Rule public JenkinsRule r = new JenkinsRule();
    @Rule public WireMockRule wm = new WireMockRule(
            wireMockConfig()
                    .extensions(new ResponseTemplateTransformer(true))
                    .dynamicPort()
    );

    @Before
    public void init() {
        NotifyGlobalConfiguration configuration = NotifyGlobalConfiguration.get();
        configuration.setUrl("http://localhost:" + wm.port());
        configuration.setApiKey("apiKey");
        configuration.setTrigger(NotificationTrigger.COMPLETED);
        stubFor(post(anyUrl())
                .willReturn(okJson("{\"success\": \"true\", \"value\": {{{request.body}}} }"))
        );
    }

    @Test
    public void whenBuildStateSendSuccessfully_ThenRunContainsSuccessInLog() throws Exception {
        FreeStyleProject project = r.createFreeStyleProject();
        project.getBuildersList().add(new MockBuilder(Result.SUCCESS));

        Run run = r.buildAndAssertSuccess(project);

        r.assertLogContains(Messages.Notifier_BuildStateSendSuccessfully(), run);
        verifyThatRequestBodyPathContains("$.name", "test0");
    }

    @Test
    public void whenCauseIsUpstream_ThenSendBuildUserFromParentBuild() throws Exception {
        // Child job
        FreeStyleProject childProject = r.createFreeStyleProject("child");
        List<AbstractProject<?, ?>> childProjects = new ArrayList<>(1);
        childProjects.add(childProject);

        // Parent job
        FreeStyleProject parentProject = r.createFreeStyleProject("parent");
        parentProject.getBuildersList().add(new MockBuilder(Result.SUCCESS));
        parentProject.getPublishersList().add(new BuildTrigger(childProjects, Result.SUCCESS));

        User user = User.getById("USER_ID", true);
        assertThat(user).isNotNull();
        user.setFullName("USER USER");

        r.assertBuildStatusSuccess(
                parentProject.scheduleBuild2(0, new Cause.UserIdCause(user.getId()))
        );
        verifyThatRequestBodyPathContains("$.name", "parent");
        verifyThatRequestBodyPathContains("$.buildState.cause.userId", "USER_ID");
        r.waitUntilNoActivity();

        verifyThatRequestBodyPathContains("$.name", "child");
        verifyThatRequestBodyPathContains("$.buildState.cause.userId", "USER_ID");

        assertThat(childProject.getLastBuild())
                .isNotNull()
                .extracting(build -> build.getCause(Cause.UpstreamCause.class))
                .isNotNull();

        verify(exactly(2), postRequestedFor(anyUrl()));

    }

    @Test
    public void whenCauseIsSCMTrigger_ThenBuildUserIsNotEmpty() throws Exception {
        buildSuccess(new SCMTrigger.SCMTriggerCause("scm"));

        verifyThatRequestBodyPathExist("$.buildState.cause.userId");
    }

    @Test
    public void whenCauseIsRemote_ThenBuildUserIsNotEmpty() throws Exception {
        buildSuccess( new Cause.RemoteCause("host", "addr"));

        verifyThatRequestBodyPathExist("$.buildState.cause.userId");
    }

    @Test
    public void whenCauseIsTimerTrigger_ThenBuildUserIsNotEmpty() throws Exception {
        buildSuccess(new TimerTrigger.TimerTriggerCause());

        verifyThatRequestBodyPathExist("$.buildState.cause.userId");
    }

    private void buildSuccess(Cause cause) throws Exception {
        FreeStyleProject project = r.createFreeStyleProject("project");
        project.getBuildersList().add(new MockBuilder(Result.SUCCESS));

        r.assertBuildStatusSuccess(project.scheduleBuild2(0, cause));
    }

    @Test
    public void whenBuildWorkflowJob_ThenRequestBodyContainsAllRequiredFields() throws Exception {
        WorkflowJob job = r.createProject(WorkflowJob.class, "workflow_test");
        job.setDefinition(new CpsFlowDefinition("",true));

        r.buildAndAssertSuccess(job);

        verify(exactly(1), postRequestedFor(anyUrl()));
        verifyThatRequestBodyPathContains("$.name", "workflow_test");
        verifyThatRequestBodyPathExist("$.url");
        verifyThatRequestBodyPathExist("$.buildState.timestamp");
        verifyThatRequestBodyPathExist("$.buildState.duration");
        verifyThatRequestBodyPathExist("$.buildState.id");
        verifyThatRequestBodyPathExist("$.buildState.url");
        verifyThatRequestBodyPathExist("$.buildState.result");

    }

    @Test
    public void whenUseGitSCM_ThenRequestBodyContainsScmState() throws Exception {

        FreeStyleProject project = r.createFreeStyleProject();
        project.getBuildersList().add(new MockBuilder(Result.SUCCESS));
        BranchSpec branchSpec = new BranchSpec(COMMIT);
        // TODO mock git repo
        GitSCM scm = new GitSCM(
                GitSCM.createRepoList(REPO_URL, CREDENTIALS_ID),
                List.of(branchSpec), null, null, List.of()
        );
        project.setScm(scm);

        r.buildAndAssertSuccess(project);

        verifyThatRequestBodyPathContains("$.buildState.scmState.url", REPO_URL);
        verifyThatRequestBodyPathContains("$.buildState.scmState.branch", BRANCH);
        verifyThatRequestBodyPathContains("$.buildState.scmState.commit", COMMIT);

    }

    private void verifyThatRequestBodyPathContains(String path, String value) {
        verify(postRequestedFor(anyUrl())
                .withRequestBody(matchingJsonPath(path, containing(value)))
        );
    }

    private void verifyThatRequestBodyPathExist(String path) {
        verify(postRequestedFor(anyUrl())
                .withRequestBody(matchingJsonPath(path))
        );
    }
}