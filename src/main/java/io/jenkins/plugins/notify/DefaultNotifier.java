package io.jenkins.plugins.notify;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.notify.model.JobState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SuppressWarnings("rawtypes")
public class DefaultNotifier implements Notifier {

    private transient final Gson gson = new GsonBuilder()
//            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    private DefaultNotifier() {
    }

    private final static Notifier INSTANCE = new DefaultNotifier();

    public static Notifier getInstance() {
        return INSTANCE;
    }

    @Override
    public void notify(Run run, TaskListener listener, EventType eventType) {

        NotifyGlobalConfiguration configuration = NotifyGlobalConfiguration.get();

        if (!isNotify(eventType, run.getResult())) return;

        if (!configuration.isConfigured()) {
            listener.getLogger().println(Messages.Global_ConfigureNotifierInGlobalConfiguration());
            return;
        }

        String url = configuration.getUrl();

        JobState jobState = new JobState(run, eventType);
        String json = gson.toJson(jobState);

        // TODO add auth, timeout?
        HttpClient client = HttpClient.newBuilder()
//                .connectTimeout()
//                .authenticator()
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode / 100 < 3) {
                listener.getLogger().println(Messages.Notifier_BuildStateSendSuccessfully());
            } else {
                listener.getLogger().println(Messages.Notifier_FailedSendBuildState(url));
            }

        } catch (IOException | InterruptedException e) {
            listener.getLogger().println(Messages.Notifier_FailedSendBuildState(url));
        }
    }

    private boolean isNotify(EventType eventType, Result result) {
        NotifyGlobalConfiguration configuration = NotifyGlobalConfiguration.get();
        NotificationTrigger trigger = configuration.getTrigger();
        if (trigger == null) return false;
        switch (trigger) {
            case ALL: return true;
            case FAILED: {
                if (result == null) return false;
                if (result.equals(Result.FAILURE) && eventType.equals(EventType.FINALIZED)) return true;
            }
            default: return eventType.isEqual(trigger.getValue());
        }
    }
}
