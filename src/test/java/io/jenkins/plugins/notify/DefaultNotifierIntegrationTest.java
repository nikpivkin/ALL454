package io.jenkins.plugins.notify;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.model.Run;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockBuilder;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class DefaultNotifierIntegrationTest {
    @Rule public JenkinsRule r = new JenkinsRule();
    @Rule public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicPort());

    private final static String OK_BODY = "{\"success\": \"true\"}";

    @Before
    public void init() {
        NotifyGlobalConfiguration configuration = NotifyGlobalConfiguration.get();
        configuration.setUrl("http://localhost:" + wireMockRule.port());
        configuration.setApiKey("apiKey");
        configuration.setTrigger(NotificationTrigger.COMPLETED);
        stubFor(
                post(anyUrl())
                        .willReturn(
                                ok().withBody(OK_BODY)
                        )
        );
    }

    @Test
    public void whenBuildStateSendSuccessfully_ThenRunContainsSuccessInLog() throws Exception {
        FreeStyleProject project = r.createFreeStyleProject();
        project.getBuildersList().add(new MockBuilder(Result.SUCCESS));
        Run run = r.buildAndAssertSuccess(project);
        r.assertLogContains(Messages.Notifier_BuildStateSendSuccessfully(), run);
    }
}