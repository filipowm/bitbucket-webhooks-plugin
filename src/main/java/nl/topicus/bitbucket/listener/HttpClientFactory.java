package nl.topicus.bitbucket.listener;

import nl.topicus.bitbucket.utils.PropertiesUtil;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

@Component
class HttpClientFactory {

    CloseableHttpClient create(String bitbucketVersion) {
        String userAgent = String.format("Bitbucket version: %s, Post webhook plugin version: %s", bitbucketVersion, PropertiesUtil.getVersion());
        return HttpClientBuilder
                .create()
                .useSystemProperties()
                .setUserAgent(userAgent)
                .build();
    }

}
