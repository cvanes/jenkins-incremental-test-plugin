package com.cvanes;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.tasks.junit.TestResultAction;

import java.io.IOException;
import java.io.PrintStream;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This plugin assumes that the project has at least one unit test.
 */
public class IncrementalTestListener extends Recorder {

    private final String includes;

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

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

    }

    public String getIncludes() {
        return includes;
    }

    @DataBoundConstructor
    public IncrementalTestListener(String includes) {
        this.includes = includes;
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
        TestResultAction previousTestResults = (TestResultAction) build.getPreviousSuccessfulBuild().getTestResultAction();
        TestResultAction testResults = (TestResultAction) build.getTestResultAction();
        if (previousTestResults != null) {
            lastCount = previousTestResults.getTotalCount();
            logger.println("Total number of tests from last successful build : " + lastCount);
        }
        if (testResults != null) {
            count = testResults.getTotalCount();
            logger.println("Total number of tests : " + count);
        } else {
            logger.println("No unit tests found");
        }

        ChangeLogSet<? extends Entry> changes = build.getChangeSet();
        if (shouldCheckTestStatusFor(logger, changes)) {
            if (count - lastCount > 0) {
                // we have new tests!!!
                return true;
            }
            // we have no new tests
            return false;
        }

        return true;
    }

    /**
     * We only want to check for new tests if files have been added or modified,
     * not when they're deleted. We also want to check the includes and excludes
     * lists.
     *
     * @param logger
     * @param changes
     * @return
     */
    private boolean shouldCheckTestStatusFor(PrintStream logger, ChangeLogSet<? extends Entry> changes) {
        if (!changes.isEmptySet()) {
//            for (Entry entry : changes) {
//                for (AffectedFile file : entry.getAffectedFiles()) {
//                    String path = file.getPath();
//                    logger.println(path);
//                }
//            }
            return true;
        }
        return false;
    }

}
