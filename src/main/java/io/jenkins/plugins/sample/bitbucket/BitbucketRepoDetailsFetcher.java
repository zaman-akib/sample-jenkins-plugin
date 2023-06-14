package io.jenkins.plugins.sample.bitbucket;

import com.cloudbees.jenkins.plugins.bitbucket.BitbucketSCMSource;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketApi;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketApiFactory;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketAuthenticator;
import com.cloudbees.jenkins.plugins.bitbucket.api.BitbucketRepository;
import hudson.model.TaskListener;
import java.io.IOException;
import jenkins.model.Jenkins;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;

/**
 * @author akib @Date 6/14/23
 */
public class BitbucketRepoDetailsFetcher {

    public static final String JOB_NAME = "sample-multibranch-pipeline";

    public void fetchBitbucketRepoDetails(TaskListener listener) throws IOException, InterruptedException {
        listener.getLogger().println("Getting bitbucket repository details");
        Jenkins jenkins = Jenkins.getInstanceOrNull();

        if (jenkins != null) {
//            EnvVars envVars = jenkins.getGlobalNodeProperties().get(EnvironmentVariablesNodeProperty.class).getEnvVars();
//            System.out.println(envVars);
            SCMSource scmSource = findSCMSource(jenkins, JOB_NAME);

            if (scmSource instanceof BitbucketSCMSource) {
                listener.getLogger().println("Getting repository details with BitbucketSCMSource");
                BitbucketSCMSource bitbucketSCMSource = (BitbucketSCMSource) scmSource;
                // Access the repository details with BitbucketSCMSource
                listener.getLogger().println("Repository Name: " + bitbucketSCMSource.getRepository());
                listener.getLogger().println("Repository Owner: " + bitbucketSCMSource.getRepoOwner());

                listener.getLogger().println("Getting repository details with BitbucketApi");
                BitbucketApi bitbucketApi = BitbucketApiFactory.newInstance(
                    bitbucketSCMSource.getServerUrl(),
                    (BitbucketAuthenticator) null,
                    bitbucketSCMSource.getRepoOwner(),
                    bitbucketSCMSource.getCredentialsId(),
                    bitbucketSCMSource.getRepository()
                );
                BitbucketRepository bitbucketRepository = bitbucketApi.getRepository();

                // Access the repository details with BitbucketApi
                listener.getLogger().println("Repository Name: " + bitbucketRepository.getRepositoryName());
                listener.getLogger().println("Repository Owner: " + bitbucketRepository.getOwnerName());
                listener.getLogger().println("Project Name: " + bitbucketRepository.getProject().getName());
            } else {
                listener.getLogger().println("SCM source is not a BitbucketSCMSource.");
            }
        } else {
            listener.getLogger().println("Jenkins instance not found.");
        }

    }

    private static SCMSource findSCMSource(Jenkins jenkins, String jobName) {
        SCMSourceOwner owner = jenkins.getItemByFullName(jobName, SCMSourceOwner.class);
        if (owner != null) {
            for (SCMSource scmSource : owner.getSCMSources()) {
                // Check if the SCM source belongs to the job
                if (owner.getSCMSource(scmSource.getId()) != null) {
                    return scmSource;
                }
            }
        }
        return null;
    }

}
