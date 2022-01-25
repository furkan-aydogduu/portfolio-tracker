package com.frkn.crypto.tracker.configuration;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("ssl")
public class SSLProperties {

    private String protocol;

    private String truststorepath;

    private String truststorepassword;

    public SSLProperties() {
    }

    public SSLProperties(String protocol, String trustStorePath, String trustStorePassword) {
        this.protocol = protocol;
        this.truststorepath = trustStorePath;
        this.truststorepassword = trustStorePassword;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTruststorepath() {
        return truststorepath;
    }

    public void setTruststorepath(String truststorepath) {
        this.truststorepath = truststorepath;
    }

    public String getTruststorepassword() {
        return truststorepassword;
    }

    public void setTruststorepassword(String truststorepassword) {
        this.truststorepassword = truststorepassword;
    }
}
