package org.acme.backend.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("rest.db")
public class RestDbProperties {
    
    private String endpoint = "endpoint";

    public String getEndpoint(){
        return endpoint;
    }

    public void setEndpoint(String endpoint){
        this.endpoint = endpoint;
    }
}