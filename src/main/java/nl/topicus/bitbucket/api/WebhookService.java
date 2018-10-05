package nl.topicus.bitbucket.api;

import com.atlassian.bitbucket.repository.Repository;
import nl.topicus.bitbucket.persistence.WebHookConfiguration;
import nl.topicus.bitbucket.persistence.WebHookConfigurationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status;

@Service
class WebhookService {

    private final WebHookConfigurationDao webhookDao;

    @Autowired
    WebhookService(WebHookConfigurationDao webhookDao) {
        this.webhookDao = webhookDao;
    }

    List<WebHookConfigurationModel> getAllForRepository(Repository repository) {
        WebHookConfiguration[] configurations = webhookDao.getWebHookConfigurations(repository);
        return Arrays.stream(configurations)
                .map(WebHookConfigurationModel::new)
                .collect(Collectors.toList());
    }

    void deleteWebhook(Repository repo, String configId) {
        WebHookConfiguration webhook = webhookDao.getWebHookConfiguration(configId);
        if (webhook == null) {

            throw new WebApplicationException(Response.status(Status.NOT_FOUND)
                    .entity("Webhook not found")
                    .build());
        }
        if (webhook.getRepositoryId().equals(repo.getId())) {
            webhookDao.deleteWebhookConfiguration(webhook);
        }
    }

    WebHookConfigurationModel createOrUpdateWebhook(Repository repository, String configId, WebHookConfigurationModel webhookModel) {
        WebHookConfiguration createdWebhook = webhookDao.createOrUpdateWebHookConfiguration(
                repository, configId,
                webhookModel.getTitle(), webhookModel.getUrl(),
                webhookModel.getCommittersToIgnore(), webhookModel.getBranchesToIgnore(),
                webhookModel.isEnabled());

        return new WebHookConfigurationModel(createdWebhook);
    }
}
