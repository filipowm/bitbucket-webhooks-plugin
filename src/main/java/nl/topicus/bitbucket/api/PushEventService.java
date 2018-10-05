package nl.topicus.bitbucket.api;

import nl.topicus.bitbucket.events.Event;
import nl.topicus.bitbucket.events.Ignorable;
import nl.topicus.bitbucket.persistence.WebHookConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Service class to capsule operations on PushEvents
 */
public class PushEventService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestListener.class);


    private WebHookConfiguration configuration;

    public PushEventService(WebHookConfiguration configuration) {
        this.configuration = configuration;
    }

    public boolean isValidEvent(Event event, WebHookConfiguration configuration) {
        return !(event instanceof Ignorable) || !isIgnoredEvent((Ignorable) event, configuration);
    }

    private boolean isIgnoredEvent(Ignorable event, WebHookConfiguration configuration) {
        return ignoredByCommiters(event, configuration) || ignoredByBranch(event, configuration);
    }

    private boolean ignoredByCommiters(Ignorable event, WebHookConfiguration configuration) {
        if (configuration.getCommittersToIgnore() != null) {
            List<String> ignoredCommitersList = Arrays.asList(configuration.getCommittersToIgnore().split("\\s?,\\s?"));
            if (event.getUsername().isPresent()
                    && ignoredCommitersList.contains(event.getUsername().get())) {
                LOGGER.debug(
                        "[repo: {}]| The push event by user {} is ignored because the username is listed as a commit to ignore: [{}({})-committersToIgnore:{}]",
                        configuration.getRepositoryId(),
                        event.getUsername(),
                        configuration.getTitle(),
                        configuration.getURL(),
                        configuration.getCommittersToIgnore());
                return true;
            }
        }
        return false;
    }

    private boolean ignoredByBranch(Ignorable event, WebHookConfiguration configuration) {
        if (configuration.getBranchesToIgnore() != null && !event.getBranches().isEmpty()) {
            boolean allBranchesIgnored = event.getBranches()
                    .stream()
                    .allMatch(name -> name.matches(configuration.getBranchesToIgnore()));

            if (allBranchesIgnored) {
                LOGGER.debug(
                        "[repo: {}]| This push event contains changes on branches {} it will be ignored because all branches are listed as branches to ignore: [{}({})-branchesToIgnore:{}]",
                        configuration.getRepositoryId(),
                        event.getBranches(),
                        configuration.getTitle(),
                        configuration.getURL(),
                        configuration.getBranchesToIgnore());
            }
            return allBranchesIgnored;
        }
        return false;
    }
}
