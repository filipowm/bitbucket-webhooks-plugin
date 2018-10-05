package nl.topicus.bitbucket.api;

import com.google.common.base.MoreObjects;
import nl.topicus.bitbucket.persistence.WebHookConfiguration;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
class WebHookConfigurationModel {
    @XmlElement
    private final Integer id;
    @XmlElement
    private final String title;
    @XmlElement
    private final String url;
    @XmlElement
    private final String committersToIgnore;
    @XmlElement
    private final String branchesToIgnore;
    @XmlElement
    private final boolean enabled;

    WebHookConfigurationModel(WebHookConfiguration webHookConfiguration) {
        id = webHookConfiguration.getID();
        title = webHookConfiguration.getTitle();
        url = webHookConfiguration.getURL();
        committersToIgnore = webHookConfiguration.getCommittersToIgnore();
        branchesToIgnore = webHookConfiguration.getBranchesToIgnore();
        enabled = webHookConfiguration.isEnabled();
    }

    Integer getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    String getUrl() {
        return url;
    }

    String getCommittersToIgnore() {
        return committersToIgnore;
    }

    String getBranchesToIgnore() {
        return branchesToIgnore;
    }

    boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("title", title)
                .add("url", url)
                .add("committersToIgnore", committersToIgnore)
                .add("branchesToIgnore", branchesToIgnore)
                .add("enabled", enabled)
                .toString();
    }
}
