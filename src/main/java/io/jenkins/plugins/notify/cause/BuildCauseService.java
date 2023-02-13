package io.jenkins.plugins.notify.cause;

import hudson.model.Cause;
import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.notify.Messages;
import io.jenkins.plugins.notify.cause.determinant.CauseDeterminant;
import io.jenkins.plugins.notify.cause.determinant.impl.RemoteCauseDeterminant;
import io.jenkins.plugins.notify.cause.determinant.impl.SCMTriggerCauseDeterminant;
import io.jenkins.plugins.notify.cause.determinant.impl.TimerTriggerCauseCauseDeterminant;
import io.jenkins.plugins.notify.cause.determinant.impl.UserIdCauseDeterminant;
import jenkins.model.Jenkins;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BuildCauseService {

    private static final BuildCauseService INSTANCE = new BuildCauseService();
    private static final Logger log = Logger.getLogger(BuildCauseService.class.getName());
    private static final Map<Class<? extends Cause>, CauseDeterminant> causeDeterminants;

    static {
        causeDeterminants = Stream.of(
                new UserIdCauseDeterminant(),
                new SCMTriggerCauseDeterminant(),
                new TimerTriggerCauseCauseDeterminant(),
                new RemoteCauseDeterminant()
        ).collect(Collectors.toMap(CauseDeterminant::getCauseClass, Function.identity()));
    }

    public static BuildCauseService getInstance() {
        return INSTANCE;
    }

    private BuildCauseService() {

    }

    public UnifiedBuildCause build(Run<?, ?> run) {
        Optional<Cause.UpstreamCause> upstreamCause = Optional.ofNullable(
                run.getCause(Cause.UpstreamCause.class)
        );

        return upstreamCause
                .map(Cause.UpstreamCause::getUpstreamProject)
                .map(project -> Jenkins.get().getItemByFullName(project, Job.class))
                .flatMap(job -> upstreamCause
                        .map(Cause.UpstreamCause::getUpstreamBuild)
                        .map(job::getBuildByNumber)
                )
                .map(this::build)
                .orElseGet(() -> run.getCauses().stream()
                        .filter(cause -> causeDeterminants.get(cause.getClass()) != null)
                        .findFirst()
                        .map(cause -> causeDeterminants.get(cause.getClass()).determine(cause))
                        .orElseGet(() -> {
                            log.warning(() ->
                                    Messages.BuildUserService_UnsupportedCauseType(
                                            Arrays.toString(run.getCauses().toArray())
                                    )
                            );
                            return UnifiedBuildCause.EMPTY;
                        }));

    }
}
