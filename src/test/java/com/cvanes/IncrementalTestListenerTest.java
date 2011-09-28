package com.cvanes;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.junit.TestResultAction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class IncrementalTestListenerTest {

    private final TestResultAction testResults = Mockito.mock(TestResultAction.class);

    private final AbstractBuild<?, ?> build = Mockito.mock(AbstractBuild.class);

    private final AbstractBuild previousBuild = Mockito.mock(AbstractBuild.class);

    private final Launcher launcher = Mockito.mock(Launcher.class);

    private final BuildListener listener = Mockito.mock(BuildListener.class);

    private IncrementalTestListener testListener;

    @Before
    public void setUp() throws Exception {
        testListener = new IncrementalTestListener(null, null);
    }

    @Test
    public void shouldTestSomething() throws Exception {
//        Mockito.when(build.getPreviousSuccessfulBuild()).thenReturn(previousBuild);
//        Mockito.when(build.getTestResultAction()).thenReturn(testResults);
//        testListener.perform(build, launcher, listener);
    }

    @Test
    public void shouldTest() throws Exception {

    }
}
