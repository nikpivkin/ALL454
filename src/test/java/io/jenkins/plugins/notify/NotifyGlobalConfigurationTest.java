package io.jenkins.plugins.notify;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsSessionRule;

@Ignore
public class NotifyGlobalConfigurationTest {

    @Rule
    public JenkinsSessionRule sessions = new JenkinsSessionRule();

    @Test
    public void uiAndStorage() throws Throwable {
        sessions.then(r -> {
            assertNull("not set initially", NotifyGlobalConfiguration.get().getUrl());
            HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
            HtmlTextInput textbox = config.getInputByName("_.url");
            textbox.setText("http://localhost");
            r.submit(config);
            assertEquals("global config page let us edit it", "http://localhost", NotifyGlobalConfiguration.get().getUrl());
        });
        sessions.then(r -> {
            assertEquals("still there after restart of Jenkins", "hello", NotifyGlobalConfiguration.get().getUrl());
        });
    }

}
