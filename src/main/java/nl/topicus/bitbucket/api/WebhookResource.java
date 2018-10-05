package nl.topicus.bitbucket.api;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.rest.util.ResourcePatterns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(ResourcePatterns.REPOSITORY_URI + "/configurations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WebhookResource {

    private final WebhookService webhookService;

    @Autowired
    public WebhookResource(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @GET
    public List<WebHookConfigurationModel> getWebhooks(@Context Repository repo) {
        return webhookService.getAllForRepository(repo);
    }

    @PUT
    public WebHookConfigurationModel createWebhook(@Context Repository repo, WebHookConfigurationModel newWebhook) {
        return webhookService.createOrUpdateWebhook(repo, null, newWebhook);
    }

    @Path("/{configId}")
    @POST
    public WebHookConfigurationModel updateWebhook(@Context Repository repo, @PathParam("configId") String configId,
                                                   WebHookConfigurationModel updatedWebhook) {
        return webhookService.createOrUpdateWebhook(repo, configId, updatedWebhook);
    }

    @Path("/{configId}")
    @DELETE
    public void removeWebhook(@Context Repository repo, @PathParam("configId") String configId) {
        webhookService.deleteWebhook(repo, configId);
    }
}
