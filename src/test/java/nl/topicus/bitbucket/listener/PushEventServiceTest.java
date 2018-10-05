package nl.topicus.bitbucket.listener;

import com.google.common.collect.ImmutableList;
import nl.topicus.bitbucket.events.*;
import nl.topicus.bitbucket.model.repository.BitbucketServerRepository;
import nl.topicus.bitbucket.model.repository.BitbucketServerRepositoryOwner;
import nl.topicus.bitbucket.persistence.DummyWebHookConfiguration;
import nl.topicus.bitbucket.persistence.WebHookConfiguration;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PushEventServiceTest {

    @Test
    public void testIgnoreSingleBranch() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("master");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("master"));
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(false));
    }

    @Test
    public void testIgnoreMultipleBranches() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("feature/.*");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("feature/bar"));
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(false));
    }

    @Test
    public void testNegativeLookAheadIgnoreBranchesWithNoDevelopBranch() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("^(?!develop$).*");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("feature/bar"),
                createDummyChange("master"),
                createDummyChange("feature/develop"));
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(false));
    }

    @Test
    public void testNegativeLookAheadIgnoreBranchesWithDevelopBranch() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("^(?!develop$).*");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("feature/bar"),
                createDummyChange("master"),
                createDummyChange("develop"));
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testValidWhenAtLeastOneBranchIsValid() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("feature/*");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("master"),
                createDummyChange("feature/bar"));
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testNullChangeList() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        dummyEvent.getPush().setChanges(null);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testEmptyChangeList() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("feature\\/*");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        dummyEvent.getPush().setChanges(new ArrayList<>());

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testIgnoreAll() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("\\*");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("JIRA-123"),
                createDummyChange("master"),
                createDummyChange("develop"),
                createDummyChange("release"),
                createDummyChange("test"),
                createDummyChange("integration"),
                createDummyChange("foo"),
                createDummyChange("bar")
        );
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testNullConfig() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore(null);
        dummyConfiguration.setCommittersToIgnore(null);

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("bar")
        );
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testEmptyConfig() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setBranchesToIgnore("");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("bar")
        );
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testIgnoreCommitter() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setCommittersToIgnore("foo.mc.fooface");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        BitbucketServerRepositoryOwner owner = new BitbucketServerRepositoryOwner("foo.mc.fooface", "FOO");

        dummyEvent.setActor(owner);
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("bar")
        );
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(false));
    }

    @Test
    public void testNullAuthor() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        dummyConfiguration.setCommittersToIgnore("foo");

        PushEventService pushEventService = new PushEventService(dummyConfiguration);

        BitbucketPushEvent dummyEvent = createDummyEvent();
        dummyEvent.setActor(null);

        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("feature/foo"),
                createDummyChange("bar")
        );
        dummyEvent.getPush().setChanges(changes);

        assertThat(pushEventService.isValidEvent(dummyEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testPullRequestEvent() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        PushEventService pushEventService = new PushEventService(dummyConfiguration);
        BitbucketServerPullRequestEvent bitbucketServerPullRequestEvent = new BitbucketServerPullRequestEvent();

        assertThat(pushEventService.isValidEvent(bitbucketServerPullRequestEvent, dummyConfiguration), is(true));
    }

    @Test
    public void testBuildStatusEvent() {
        WebHookConfiguration dummyConfiguration = createDummyConfiguration();
        PushEventService pushEventService = new PushEventService(dummyConfiguration);
        BuildStatusEvent buildStatusEvent = new BuildStatusEvent();

        assertThat(pushEventService.isValidEvent(buildStatusEvent, dummyConfiguration), is(true));
    }

    private WebHookConfiguration createDummyConfiguration() {
        return new DummyWebHookConfiguration(0, "foo", "urlfoo", "bob", null, true,
                true, false, false, false, false,
                false, false, false, false, false,
                false, false);
    }

    private BitbucketPushEvent createDummyEvent() {
        BitbucketPushEvent bitbucketPushEvent = new BitbucketPushEvent();
        BitbucketServerRepositoryOwner owner = new BitbucketServerRepositoryOwner("foo", "FOO");
        bitbucketPushEvent.setActor(owner);
        BitbucketServerRepository bitbucketServerRepository = new BitbucketServerRepository();
        bitbucketServerRepository.setScmId("TEST");
        bitbucketServerRepository.setPublic(true);
        bitbucketServerRepository.setSlug("slugg mc slugface");
        bitbucketPushEvent.setRepository(bitbucketServerRepository);

        BitbucketPushDetail bitbucketPushDetail = new BitbucketPushDetail();
        ImmutableList<BitbucketPushChange> changes = ImmutableList.of(
                createDummyChange("master"),
                createDummyChange("develop"));

        bitbucketPushDetail.setChanges(changes);

        bitbucketPushEvent.setPush(bitbucketPushDetail);

        return bitbucketPushEvent;
    }

    private BitbucketPushChange createDummyChange(String branchName) {
        BitbucketPushChange bitbucketPushChange = new BitbucketPushChange();
        bitbucketPushChange.setClosed(true);
        bitbucketPushChange.setCreated(true);

        BitbucketPushChange.State _new = new BitbucketPushChange.State();
        _new.setType("branch");
        _new.setName(branchName);
        bitbucketPushChange.setNew(_new);

        BitbucketPushChange.State _old = new BitbucketPushChange.State();
        _old.setType("branch");
        _old.setName(branchName);
        bitbucketPushChange.setOld(_old);

        return bitbucketPushChange;
    }

}
