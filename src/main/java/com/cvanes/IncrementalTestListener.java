package com.cvanes;

import java.io.IOException;

import javax.servlet.ServletException;

import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

public class IncrementalTestListener extends Recorder {
	
	@DataBoundConstructor
	public IncrementalTestListener() {
		// no config as of yet
	}
	
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, 
			Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		
		ChangeLogSet<? extends Entry> changes = build.getChangeSet();
		
		if (changes.isEmptySet()) {
			return false;
		}
		return true;
	}
	
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        public String getDisplayName() {
            return "Fail Build When No New Tests Added";
        }
        
        @Override
        public String getHelpFile() {
            return "/plugin/incremental-test-plugin/help-enabled.html";
        }

		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

    }

}
