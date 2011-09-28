package com.cvanes;

import hudson.FilePath;
import hudson.Util;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.types.FileSet;

/**
 * Executed on the slave to find which files should be
 * used for checking the delta in the test count.
 */
public class WorkspaceLister implements FilePath.FileCallable<String[]> {

    private static final long serialVersionUID = 2306949616575685812L;

    private final String includes;

    private final String excludes;

    public WorkspaceLister(String includes, String excludes) {
        this.includes = includes == null ? "**" : includes;
        this.excludes = excludes;
    }

    /**
     * Executed on the slave where the build is running.
     */
    public String[] invoke(File workspace, VirtualChannel channel) throws IOException, InterruptedException {
        FileSet fs = Util.createFileSet(workspace, includes, excludes);
        DirectoryScanner ds = fs.getDirectoryScanner();
        String[] files = ds.getIncludedFiles();
        return files;
    }

}
