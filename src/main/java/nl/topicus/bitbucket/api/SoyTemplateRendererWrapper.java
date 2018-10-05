package nl.topicus.bitbucket.api;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Component
class SoyTemplateRendererWrapper {

    private final SoyTemplateRenderer templateRenderer;

    @Autowired
    SoyTemplateRendererWrapper(@ComponentImport SoyTemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    void render(HttpServletResponse response, String templateName, Map<String, Object> data) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            templateRenderer.render(response.getWriter(), "nl.topicus.bitbucket.bitbucket-webhooks:templates-soy", templateName, data);
        } catch (SoyException e) {
            Throwable cause = e.getCause();
            if (cause instanceof IOException) {
                throw (IOException) cause;
            }
            throw new ServletException(e);
        }
    }
}
