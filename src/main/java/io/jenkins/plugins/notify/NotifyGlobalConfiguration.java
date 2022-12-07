package io.jenkins.plugins.notify;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import java.net.URI;
import java.net.URISyntaxException;

@Extension
public class NotifyGlobalConfiguration extends GlobalConfiguration {

    private static final String DISPLAY_NAME = "Jira Notifier";

    /** @return the singleton instance */
    public static NotifyGlobalConfiguration get() {
        return ExtensionList.lookupSingleton(NotifyGlobalConfiguration.class);
    }

    private String url;
    private String event = "all";

    public NotifyGlobalConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    @NonNull
    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public String getUrl() {
        return url;
    }

    @DataBoundSetter
    public void setUrl(String url) {
        this.url = url;
        save();
    }

    public String getEvent() {
        return event;
    }

    @DataBoundSetter
    public void setEvent(String event) {
        this.event = event;
        save();
    }

    public FormValidation doCheckUrl(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning(Messages.GlobalConfiguration_UrlCannotBeEmpty());
        }
        try {
            URI uri = new URI(value);
            if (!uri.isAbsolute()) {
                return FormValidation.error(Messages.GlobalConfiguration_UrlIsNotValid());
            }
            String scheme = uri.getScheme();
            if (!scheme.startsWith("http")) {
                return FormValidation.error(Messages.GlobalConfiguration_HttpOrHttps());
            }
        } catch (URISyntaxException e) {
            return FormValidation.error(Messages.GlobalConfiguration_UrlIsNotValid());
        }
        return FormValidation.ok();
    }

    public boolean isConfigured() {
        return url != null && url.length() != 0;
    }

}
