package io.jenkins.plugins.sample;

import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Node;
import hudson.model.TaskListener;
import io.jenkins.plugins.sample.bitbucket.BitbucketRepoDetailsFetcher;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author akib @Date 6/8/23
 */
public class CodeAnalysisStep extends Step implements Serializable {

    private String param1;
    private String param2;

    @DataBoundConstructor
    public CodeAnalysisStep(String param1, String param2) {
        this.param1 = param1;
        this.param2 = param2;
    }

    public String getParam1() {
        return param1;
    }

    public String getParam2() {
        return param2;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new BlockingExecution(context);
    }

    public class BlockingExecution extends SynchronousNonBlockingStepExecution<Integer> {
        private static final long serialVersionUID = -8137577274749324767L;
        private final transient TaskListener listener;
        private final transient EnvVars envVars;
        private final transient FilePath workspace;
        private final transient Launcher launcher;
        private final transient Node node;

        protected BlockingExecution(@Nonnull StepContext context) throws InterruptedException, IOException {
            super(context);
            listener = context.get(TaskListener.class);
            envVars = context.get(EnvVars.class);
            workspace = context.get(FilePath.class);
            launcher = context.get(Launcher.class);
            node = context.get(Node.class);
        }

        @Override
        protected Integer run() throws Exception {
            if (Objects.isNull(param1) || Objects.isNull(param2)) {
                throw new RuntimeException("Param values can't be null");
            }
            boolean isValidParams = true;
            if (!(param1.equals(AnalysisType.BLACK_DUCK.name()) || param1.equals(AnalysisType.POLARIS.name()))) {
                isValidParams = false;
                listener.getLogger().println("Invalid param1: " + param1);
                listener.getLogger().println("It should be: " + Arrays.toString(AnalysisType.values()));
            }
            if (!(param2.equals(ScanType.BLOCKING.name()) || param2.equals(ScanType.NON_BLOCKING.name()))) {
                isValidParams = false;
                listener.getLogger().println("Invalid param2: " + param2);
                listener.getLogger().println("It should be: " + Arrays.toString(ScanType.values()));
            }
            if (isValidParams) {
                Thread.sleep(2000);
                listener.getLogger().println("Analysing code");
            }
            BitbucketRepoDetailsFetcher bitbucketRepoDetailsFetcher = new BitbucketRepoDetailsFetcher();
            bitbucketRepoDetailsFetcher.fetchBitbucketRepoDetails(listener);
            return 0;
        }

    }

    @Symbol("code_analysis")
    @Extension
    public static final class DescriptorImpl extends StepDescriptor {
        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return new HashSet<>(Arrays.asList(TaskListener.class, EnvVars.class,
                FilePath.class, Launcher.class, Node.class));
        }

        @Override
        public String getFunctionName() {
            return "code_analysis";
        }

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Code Analysis";
        }

    }
    
    public enum AnalysisType {
        BLACK_DUCK, POLARIS,
    }

    public enum ScanType {
        BLOCKING, NON_BLOCKING,
    }

}
