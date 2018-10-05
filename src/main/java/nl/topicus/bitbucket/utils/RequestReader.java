package nl.topicus.bitbucket.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestReader {

    private final HttpServletRequest request;
    private final String booleanTestString;

    public RequestReader(HttpServletRequest request, String booleanTestString) {
        this.request = request;
        this.booleanTestString = booleanTestString;
    }

    public RequestReader(HttpServletRequest request) {
        this(request, "on");
    }

    public boolean getBoolean(String name) {
        String value = getString(name);
        return booleanTestString.equalsIgnoreCase(value);
    }

    public String getString(String name) {
        return request.getParameter(name);
    }
}
