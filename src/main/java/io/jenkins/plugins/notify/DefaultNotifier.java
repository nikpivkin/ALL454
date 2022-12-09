package io.jenkins.plugins.notify;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import hudson.EnvVars;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.notify.model.JiraResponse;
import io.jenkins.plugins.notify.model.JobState;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("rawtypes")
public class DefaultNotifier implements Notifier {

    private static final Logger log = Logger.getLogger(DefaultNotifier.class.getName());
    private static final Gson gson = new Gson();

    private final static Notifier INSTANCE = new DefaultNotifier();

    private DefaultNotifier() {}

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
        EnvVars envVars = new EnvVars();
        try {
            envVars = run.getEnvironment(listener);
        } catch (IOException | InterruptedException ignored) {
        }

        JobState jobState = new JobState(run, eventType, envVars);
        String json = gson.toJson(jobState);

        // TODO add auth, timeout?
        HttpClient client = HttpClient.newBuilder()
                .build();

        String finalEndpoint = String.format("%s?apikey=%s", url, configuration.getApiKey());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(finalEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        String responseBody = "";

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            responseBody = response.body();
            JiraResponse jiraResponse = gson.fromJson(responseBody, JiraResponse.class);
            if (statusCode / 100 < 3) {
                if (jiraResponse.isSuccess()) {
                    listener.getLogger().println(Messages.Notifier_BuildStateSendSuccessfully());
                } else {
                    listener.getLogger().println(Messages.Notifier_FailedSendBuildState(url));
                }
            } else {
                listener.getLogger().println(Messages.Notifier_FailedSendBuildState(url));
            }

        } catch (IOException | InterruptedException e) {
            listener.getLogger().println(Messages.Notifier_FailedSendBuildState(url));
        } catch (JsonParseException jpe ) {
            log.log(Level.SEVERE, String.format("Failed to parse the response: %s", responseBody), jpe);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unexpected error", e);
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
