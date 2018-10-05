package nl.topicus.bitbucket.api;

import com.atlassian.bitbucket.repository.Repository;
import com.google.common.collect.ImmutableMap;
import nl.topicus.bitbucket.persistence.DummyWebHookConfiguration;
import nl.topicus.bitbucket.persistence.WebHookConfiguration;
import nl.topicus.bitbucket.persistence.WebHookConfigurationDao;
import nl.topicus.bitbucket.utils.RequestReader;
import nl.topicus.bitbucket.utils.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
class RepositoryConfigServletHandler {

    private static final String EDIT_TEMPLATE = "nl.topicus.templates.edit";
    private static final String REPOSITORY_SETTINGS_TEMPLATE = "nl.topicus.templates.repositorySettings";

    private final SoyTemplateRendererWrapper soyTemplateRenderer;
    private final WebHookConfigurationDao webHookConfigurationDao;

    @Autowired
    RepositoryConfigServletHandler(SoyTemplateRendererWrapper soyTemplateRenderer, WebHookConfigurationDao webHookConfigurationDao) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.webHookConfigurationDao = webHookConfigurationDao;
    }

    void renderForGet(Repository repository, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<NameValuePair> queryParams = URLEncodedUtils.parse(RequestUtil.getURI(req), "UTF-8");
        if (queryParams.stream().anyMatch(nameValuePair -> nameValuePair.getName().equals("edit"))) {
            renderEdit(queryParams, repository, resp);
        } else {
            renderSettings(queryParams, repository, resp);
        }
    }

    private void renderSettings(List<NameValuePair> queryParams, Repository repository, HttpServletResponse response) throws IOException, ServletException {
        if (queryParams.stream().anyMatch(param -> param.getName().equals("delete"))) {
            queryParams.stream()
                    .filter(param -> "id".equals(param.getName()))
                    .findFirst()
                    .map(NameValuePair::getValue)
                    .map(webHookConfigurationDao::getWebHookConfiguration)
                    .filter(config -> config.getRepositoryId().equals(repository.getId()))
                    .ifPresent(webHookConfigurationDao::deleteWebhookConfiguration);
        }
        WebHookConfiguration[] webHookConfigurations = webHookConfigurationDao.getWebHookConfigurations(repository);
        render(response, REPOSITORY_SETTINGS_TEMPLATE, ImmutableMap.<String, Object>builder().put("repository", repository)
                .put("configurations", webHookConfigurations)
                .build());
    }

    private void renderEdit(List<NameValuePair> queryParams, Repository repository, HttpServletResponse response) throws IOException, ServletException {
        ImmutableMap.Builder<String, Object> properties = ImmutableMap.<String, Object>builder()
                .put("repository", repository);

        queryParams.stream()
                .filter(nameValuePair -> "id".equals(nameValuePair.getName()) || StringUtils.isNotBlank(nameValuePair.getValue()))
                .findFirst()
                .map(NameValuePair::getValue)
                .map(webHookConfigurationDao::getWebHookConfiguration)
                .filter(config -> config.getRepositoryId().equals(repository.getId()))
                .ifPresent(config -> properties.put("configuration", config));

        render(response, EDIT_TEMPLATE, properties.build());
    }

    private void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws IOException, ServletException {
        soyTemplateRenderer.render(resp, templateName, data);
    }

    void renderForPost(Repository repository, HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        RequestReader reader = new RequestReader(req);

        String title = reader.getString("title");
        String url = reader.getString("url");
        String committersToIgnore = reader.getString("committersToIgnore");
        String branchesToIgnore = reader.getString("branchesToIgnore");
        String id = reader.getString("id");
        boolean enabled = reader.getBoolean("enabled");

        boolean isTagCreated = reader.getBoolean("isTagCreated");
        boolean isBranchDeleted = reader.getBoolean("isBranchDeleted");
        boolean isBranchCreated = reader.getBoolean("isBranchCreated");
        boolean isRepoPush = reader.getBoolean("isRepoPush");
        boolean isPrDeclined = reader.getBoolean("isPrDeclined");
        boolean isPrRescoped = reader.getBoolean("isPrRescoped");
        boolean isPrMerged = reader.getBoolean("isPrMerged");
        boolean isPrReopened = reader.getBoolean("isPrReopened");
        boolean isPrUpdated = reader.getBoolean("isPrUpdated");
        boolean isPrCreated = reader.getBoolean("isPrCreated");
        boolean isPrCommented = reader.getBoolean("isPrCommented");
        boolean isBuildStatus = reader.getBoolean("isBuildStatus");

        WebHookConfiguration webHookConfiguration = webHookConfigurationDao
                .createOrUpdateWebHookConfiguration(repository, id, title, url, committersToIgnore, branchesToIgnore, enabled,
                        isTagCreated, isBranchDeleted, isBranchCreated, isRepoPush, isPrDeclined, isPrRescoped,
                        isPrMerged, isPrReopened, isPrUpdated, isPrCreated, isPrCommented, isBuildStatus);

        if (webHookConfiguration == null) { // TODO can it even happen? based on code in WebHooConfigurationDao it's not possible
            webHookConfiguration = new DummyWebHookConfiguration(repository.getId(), title, url, committersToIgnore, branchesToIgnore, enabled,
                    isTagCreated, isBranchDeleted, isBranchCreated, isRepoPush, isPrDeclined, isPrRescoped,
                    isPrMerged, isPrReopened, isPrUpdated, isPrCreated, isPrCommented, isBuildStatus);
            render(resp, EDIT_TEMPLATE, ImmutableMap.<String, Object>builder().put("repository", repository).put("configuration", webHookConfiguration).build());
        } else {
            WebHookConfiguration[] webHookConfigurations = webHookConfigurationDao.getWebHookConfigurations(repository);
            render(resp, REPOSITORY_SETTINGS_TEMPLATE, ImmutableMap.<String, Object>builder().put("repository", repository).put("configurations", webHookConfigurations).build());
        }
    }


}
