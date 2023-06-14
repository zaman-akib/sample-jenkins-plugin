package io.jenkins.plugins.sample;

import hudson.Extension;
import hudson.model.RootAction;

/**
 * @author akib @Date 6/12/23
 */
@Extension
public class DemoAction implements RootAction {
    @Override
    public String getIconFileName() {
        return "clipboard.png";
    }

    @Override
    public String getDisplayName() {
        return "Demo Action";
    }

    @Override
    public String getUrlName() {
        return "https://google.com";
    }
}
