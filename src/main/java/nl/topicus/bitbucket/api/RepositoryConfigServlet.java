package nl.topicus.bitbucket.api;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableMap;
import nl.topicus.bitbucket.persistence.DummyWebHookConfiguration;
import nl.topicus.bitbucket.persistence.WebHookConfiguration;
import nl.topicus.bitbucket.persistence.WebHookConfigurationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
class RepositoryConfigServlet extends HttpServlet {
    private final SoyTemplateRendererWrapper soyTemplateRenderer;
    private final RepositoryService repositoryService;
    private final WebHookConfigurationDao webHookConfigurationDao;
    private final RepositoryConfigServletHandler repositoryConfigServletHandler;

    @Autowired
    RepositoryConfigServlet(SoyTemplateRendererWrapper soyTemplateRenderer,
                            @ComponentImport RepositoryService repositoryService,
                            WebHookConfigurationDao webHookConfigurationDao,
                            RepositoryConfigServletHandler repositoryConfigServletHandler) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.repositoryService = repositoryService;
        this.webHookConfigurationDao = webHookConfigurationDao;
        this.repositoryConfigServletHandler = repositoryConfigServletHandler;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Repository repository = getRepository(req);
        if (repository == null) {
            return;
        }
        repositoryConfigServletHandler.renderForGet(repository, req, resp);
    }

    private Repository getRepository(HttpServletRequest req) {
        // Get repoSlug from path
        String pathInfo = req.getPathInfo();
        String[] components = pathInfo.split("/");
        return components.length < 3 ? null : repositoryService.getBySlug(components[1], components[2]);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Repository repository = getRepository(req);
        if (repository == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String title = req.getParameter("title");
        String url = req.getParameter("url");
        String committersToIgnore = req.getParameter("committersToIgnore");
        String branchesToIgnore = req.getParameter("branchesToIgnore");
        String id = req.getParameter("id");
        boolean enabled = "on".equalsIgnoreCase(req.getParameter("enabled"));

        boolean isTagCreated = "on".equalsIgnoreCase(req.getParameter("isTagCreated"));
        boolean isBranchDeleted = "on".equalsIgnoreCase(req.getParameter("isBranchDeleted"));
        boolean isBranchCreated = "on".equalsIgnoreCase(req.getParameter("isBranchCreated"));
        boolean isRepoPush = "on".equalsIgnoreCase(req.getParameter("isRepoPush"));
        boolean isPrDeclined = "on".equalsIgnoreCase(req.getParameter("isPrDeclined"));
        boolean isPrRescoped = "on".equalsIgnoreCase(req.getParameter("isPrRescoped"));
        boolean isPrMerged = "on".equalsIgnoreCase(req.getParameter("isPrMerged"));
        boolean isPrReopened = "on".equalsIgnoreCase(req.getParameter("isPrReopened"));
        boolean isPrUpdated = "on".equalsIgnoreCase(req.getParameter("isPrUpdated"));
        boolean isPrCreated = "on".equalsIgnoreCase(req.getParameter("isPrCreated"));
        boolean isPrCommented = "on".equalsIgnoreCase(req.getParameter("isPrCommented"));
        boolean isBuildStatus = "on".equalsIgnoreCase(req.getParameter("isBuildStatus"));

        WebHookConfiguration webHookConfiguration = webHookConfigurationDao
                .createOrUpdateWebHookConfiguration(repository, id, title, url, committersToIgnore, branchesToIgnore, enabled,
                        isTagCreated, isBranchDeleted, isBranchCreated, isRepoPush, isPrDeclined, isPrRescoped,
                        isPrMerged, isPrReopened, isPrUpdated, isPrCreated, isPrCommented, isBuildStatus);
        if (webHookConfiguration == null) {
            webHookConfiguration = new DummyWebHookConfiguration(repository.getId(), title, url, committersToIgnore, branchesToIgnore, enabled,
                    isTagCreated, isBranchDeleted, isBranchCreated, isRepoPush, isPrDeclined, isPrRescoped,
                    isPrMerged, isPrReopened, isPrUpdated, isPrCreated, isPrCommented, isBuildStatus);
            String template = "nl.topicus.templates.edit";
            render(resp, template, ImmutableMap.<String, Object>builder().put("repository", repository).put("configuration", webHookConfiguration).build());
        } else {
            WebHookConfiguration[] webHookConfigurations = webHookConfigurationDao.getWebHookConfigurations(repository);
            String template = "nl.topicus.templates.repositorySettings";
            render(resp, template, ImmutableMap.<String, Object>builder().put("repository", repository).put("configurations", webHookConfigurations).build());
        }
    }

    private void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws IOException, ServletException {
        soyTemplateRenderer.render(resp, templateName, data);
    }

}