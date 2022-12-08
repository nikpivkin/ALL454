package io.jenkins.plugins.notify.cause;

import hudson.model.Cause;
import hudson.model.Job;
import hudson.model.Run;
import hudson.triggers.SCMTrigger;
import hudson.triggers.TimerTrigger;
import jenkins.model.Jenkins;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class BuildUserServiceTest {
    private BuildUserService buildUserService;

    @Before
    public void init() {
        buildUserService = spy(BuildUserService.getInstance());
    }

    @Test
    public void returnEmptyBuildUser_WhenNotCauseToBuild() {
        Run run = mock(Run.class);

        BuildUser buildUser = buildUserService.build(run);

        assertThat(buildUser).isEqualTo(BuildUser.EMPTY);
    }

    @Test
    public void determineBuildUserFromUserIdCause_WhenCauseIsUserId() {
        final String userId = "userId";
        final String userName = "userName";
        Run run = mock(Run.class);
        Cause.UserIdCause cause = mock(Cause.UserIdCause.class);
        when(cause.getUserId()).thenReturn(userId);
        when(cause.getUserName()).thenReturn(userName);
        when(run.getCauses()).thenReturn(List.of(cause));

        BuildUser buildUser = buildUserService.build(run);

        BuildUser expectedUser = new BuildUser();
        expectedUser.setUserId(userId);
        expectedUser.setFullName(userName);

        assertThat(buildUser)
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    public void determineBuildUserFromRemoteCause_WhenCauseIsRemote() {
        Run run = mock(Run.class);
        Cause.RemoteCause cause = mock(Cause.RemoteCause.class);
        when(run.getCauses()).thenReturn(List.of(cause));

        BuildUser buildUser = buildUserService.build(run);

        assertThat(buildUser.getUserId())
                .isNotNull();
    }

    @Test
    public void determineBuildUserFromTimeTrigger_WhenCauseIsTimeTrigger() {
        Run run = mock(Run.class);
        TimerTrigger.TimerTriggerCause cause = mock(TimerTrigger.TimerTriggerCause.class);
        when(run.getCauses()).thenReturn(List.of(cause));

        BuildUser buildUser = buildUserService.build(run);

        assertThat(buildUser.getUserId())
                .isNotNull();
    }

    @Test
    public void determineBuildUserFromSCMTrigger_WhenCauseIsSCMTrigger() {
        Run run = mock(Run.class);
        SCMTrigger.SCMTriggerCause cause = mock(SCMTrigger.SCMTriggerCause.class);
        when(run.getCauses()).thenReturn(List.of(cause));

        BuildUser buildUser = buildUserService.build(run);

        assertThat(buildUser.getUserId())
                .isNotNull();
    }

    @Test
    public void whenCauseIsUpStream_ThenGetBuildFromUpstreamAndReCall() {

        final String upstreamProject = "PROJ";
        final int upstreamBuild = 90;

        Run run = mock(Run.class);
        Job upstreamJob = mock(Job.class);
        when(upstreamJob.getBuildByNumber(upstreamBuild)).thenReturn(run);

        Cause.UpstreamCause cause = mock(Cause.UpstreamCause.class);
        when(cause.getUpstreamProject()).thenReturn(upstreamProject);
        when(cause.getUpstreamBuild()).thenReturn(upstreamBuild);

        Run runWithUpstreamCause = mock(Run.class);
        when(runWithUpstreamCause.getCause(Cause.UpstreamCause.class)).thenReturn(cause);

        Jenkins jenkins = mock(Jenkins.class);
        when(jenkins.getItemByFullName(upstreamProject, Job.class)).thenReturn(upstreamJob);

        try (MockedStatic<Jenkins> mocked = mockStatic(Jenkins.class)) {
            mocked.when(Jenkins::get).thenReturn(jenkins);
            buildUserService.build(runWithUpstreamCause);
        }

        verify(buildUserService, times(2)).build(any(Run.class));
    }

}