package nl.topicus.bitbucket.api;

import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
class RepositoryConfigServlet extends HttpServlet {
    private final RepositoryService repositoryService;
    private final RepositoryConfigServletHandler repositoryConfigServletHandler;

    @Autowired
    RepositoryConfigServlet(@ComponentImport RepositoryService repositoryService,
                            RepositoryConfigServletHandler repositoryConfigServletHandler) {
        this.repositoryService = repositoryService;
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
        repositoryConfigServletHandler.renderForPost(repository, req, resp);
    }

}