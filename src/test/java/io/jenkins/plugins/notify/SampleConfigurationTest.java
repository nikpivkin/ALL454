package io.jenkins.plugins.notify;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.jvnet.hudson.test.JenkinsSessionRule;

@Ignore
public class SampleConfigurationTest {

    @Rule
    public JenkinsSessionRule sessions = new JenkinsSessionRule();

    /**
     * Tries to exercise enough code paths to catch common mistakes:
     * <ul>
     * <li>missing {@code load}
     * <li>missing {@code save}
     * <li>misnamed or absent getter/setter
     * <li>misnamed {@code textbox}
     * </ul>
     */
    @Test
    public void uiAndStorage() throws Throwable {
        sessions.then(r -> {
            assertNull("not set initially", NotifyGlobalConfiguration.get().getUrl());
            HtmlForm config = r.createWebClient().goTo("configure").getFormByName("config");
            HtmlTextInput textbox = config.getInputByName("_.label");
            textbox.setText("hello");
            r.submit(config);
            assertEquals("global config page let us edit it", "hello", NotifyGlobalConfiguration.get().getUrl());
        });
        sessions.then(r -> {
            assertEquals("still there after restart of Jenkins", "hello", NotifyGlobalConfiguration.get().getUrl());
        });
    }

}
