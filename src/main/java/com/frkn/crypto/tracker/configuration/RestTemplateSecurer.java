package com.frkn.crypto.tracker.configuration;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.security.KeyStore;

@ConditionalOnBean(SSLProperties.class)
@Configuration
public class RestTemplateSecurer implements RestTemplateCustomizer {

    @Autowired
    private SSLProperties properties;

    @Override
    public void customize(RestTemplate restTemplate) {

        SSLContext sslContext;
        try {
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

            // get user password and file input stream
            char[] password = properties.getTruststorepassword().toCharArray();

            try (FileInputStream fis = new FileInputStream(properties.getTruststorepath())) {
                ks.load(fis, password);
            }

            sslContext = SSLContextBuilder.create()
                    .loadKeyMaterial(ks, properties.getTruststorepassword().toCharArray())
                    .setProtocol(properties.getProtocol())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("SSL Error", e);
        }

        final HttpClient httpClient = HttpClientBuilder.create()
                .setSSLContext(sslContext)
                .build();

        final ClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        restTemplate.setRequestFactory(requestFactory);
    }
}
