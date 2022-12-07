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

public class BuildUserService {

    private final static BuildUserService INSTANCE = new BuildUserService();
    private final static Logger log = Logger.getLogger(BuildUserService.class.getName());
    private final static Map<Class<? extends Cause>, CauseDeterminant> causeDeterminants;

    static {
        causeDeterminants = Stream.of(
                new UserIdCauseDeterminant(),
                new SCMTriggerCauseDeterminant(),
                new TimerTriggerCauseCauseDeterminant(),
                new RemoteCauseDeterminant()
        ).collect(Collectors.toMap(CauseDeterminant::getCauseClass, Function.identity()));
    }

    public static BuildUserService getInstance() {
        return INSTANCE;
    }

    private BuildUserService() {

    }

    public BuildUser build(Run<?, ?> run) {
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
                            return BuildUser.EMPTY;
                        }));

    }
}
