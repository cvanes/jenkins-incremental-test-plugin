package com.cvanes;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.AffectedFile;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.tasks.test.AbstractTestResultAction;

import java.io.IOException;
import java.io.PrintStream;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * A simple plugin to fail the build if no new unit tests
 * have been added since the last successful build.
 */
public class IncrementalTestListener extends Recorder {

    private final String includes;

    private final String excludes;

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public String getDisplayName() {
            return "Fail build when no new unit tests added";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/incremental-test-plugin/help.html";
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public IncrementalTestListener newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return req.bindJSON(IncrementalTestListener.class,formData);
        }
    }

    /*
     * Data bound from configuration screen.
     */

    @DataBoundConstructor
    public IncrementalTestListener(String includes, String excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    /*
     * Getters needed to show persisted data in job config.
     */

    public String getIncludes() {
        return includes;
    }

    public String getExcludes() {
        return excludes;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build,
                           Launcher launcher,
                           BuildListener listener) throws InterruptedException, IOException {

        PrintStream logger = listener.getLogger();

        int lastCount = 0;
        int count = 0;

        AbstractBuild<?, ?> previousBuild = build.getPreviousSuccessfulBuild();
        if (previousBuild != null) {
            AbstractTestResultAction<?> previousTestResults = build.getPreviousSuccessfulBuild().getTestResultAction();
            AbstractTestResultAction<?> testResults = build.getTestResultAction();
            if (previousTestResults != null) {
                lastCount = previousTestResults.getTotalCount();
            }
            if (testResults != null) {
                count = testResults.getTotalCount();
            }

            if (shouldCheckTestStatusFor(build)) {
                if (count - lastCount > 0) {
                    // we have new tests!!!
                    return true;
                }
                // we have no new tests
                logger.println("No new unit tests added since last successful build and changes" +
                		" have been detected from SCM, failing build");
                return false;
            }
        }

        return true;
    }

    /**
     * We only want to check for new tests if files have been added or modified,
     * not when they're deleted. We also want to check the includes and excludes
     * lists.
     *
     * @param build
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    private boolean shouldCheckTestStatusFor(AbstractBuild<?, ?> build) throws IOException, InterruptedException {
        ChangeLogSet<? extends Entry> commits = build.getChangeSet();
        String[] workspaceFiles = build.getWorkspace().act(new WorkspaceLister(includes, excludes));
        if (!commits.isEmptySet()) {
            for (Entry commit : commits) {
                if (shouldCheckTestStatusFor(commit, workspaceFiles)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean shouldCheckTestStatusFor(Entry commit, String[] workspaceFiles) {
        for (AffectedFile affectedFile : commit.getAffectedFiles()) {
            String affectedFilePath = affectedFile.getPath();
            for (String workspaceFilePath : workspaceFiles) {
                if (affectedFilePath.endsWith(workspaceFilePath)) {
                    return true;
                }
            }
        }
        return false;
    }

}
