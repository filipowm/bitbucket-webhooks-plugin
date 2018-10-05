package nl.topicus.bitbucket.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;

public final class RequestUtil {

    private RequestUtil() {

    }

    public static URI getURI(HttpServletRequest request) throws ServletException {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        String url = queryString == null ? requestURL.toString() : requestURL.append('?')
                .append(queryString)
                .toString();
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new ServletException(e);
        }
    }
}
