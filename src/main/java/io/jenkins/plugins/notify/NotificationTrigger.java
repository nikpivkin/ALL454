package io.jenkins.plugins.notify;

import hudson.util.ListBoxModel;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum NotificationTrigger {
    ALL("All Events"),
    INITIALIZED("Job initialized"),
    STARTED("Job started"),
    COMPLETED("Job completed"),
    FINALIZED("Job finalized"),
    DELETED("Job deleted"),
    FAILED("Job failed");

    public static final NotificationTrigger DEFAULT = ALL;

    private final String name;

    NotificationTrigger(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return toString();
    }

    public ListBoxModel.Option toListBoxOption(NotificationTrigger currentTrigger) {
        return new ListBoxModel.Option(
                getName(),
                getValue(),
                this.equals(currentTrigger)
        );
    }

    public static ListBoxModel toListBoxModel(NotificationTrigger currentTrigger) {
        return new ListBoxModel(
                Stream.of(values())
                        .map(trigger -> trigger.toListBoxOption(currentTrigger))
                        .collect(Collectors.toList())
        );
    }
}
