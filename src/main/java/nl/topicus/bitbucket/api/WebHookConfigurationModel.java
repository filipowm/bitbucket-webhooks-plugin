package nl.topicus.bitbucket.api;

import nl.topicus.bitbucket.persistence.WebHookConfiguration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WebHookConfigurationModel {
    @XmlElement
    private Integer id;
    @XmlElement
    private String title;
    @XmlElement
    private String url;
    @XmlElement
    private String committersToIgnore;
    @XmlElement
    private String branchesToIgnore;
    @XmlElement
    private boolean enabled;

    WebHookConfigurationModel(WebHookConfiguration webHookConfiguration) {
        id = webHookConfiguration.getID();
        title = webHookConfiguration.getTitle();
        url = webHookConfiguration.getURL();
        committersToIgnore = webHookConfiguration.getCommittersToIgnore();
        branchesToIgnore = webHookConfiguration.getBranchesToIgnore();
        enabled = webHookConfiguration.isEnabled();
    }

    public WebHookConfigurationModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCommittersToIgnore() {
        return committersToIgnore;
    }

    public void setCommittersToIgnore(String committersToIgnore) {
        this.committersToIgnore = committersToIgnore;
    }

    public String getBranchesToIgnore() {
        return branchesToIgnore;
    }

    public void setBranchesToIgnore(String branchesToIgnore) {
        this.branchesToIgnore = branchesToIgnore;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "WebHookConfigurationModel{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", committersToIgnore='" + committersToIgnore + '\'' +
                ", branchesToIgnore='" + branchesToIgnore + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}
