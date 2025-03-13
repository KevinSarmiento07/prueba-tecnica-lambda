package com.kass.services.implementation;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

//@Service
public class ParameterStoreService {
    
    private final SsmClient ssmClient;
    
    public ParameterStoreService() {
        this.ssmClient = SsmClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
    
    public String getParameter(String name) {
        GetParameterRequest request = GetParameterRequest.builder()
                .name(name)
                .withDecryption(true)
                .build();
        
        GetParameterResponse response = ssmClient.getParameter(request);
        
        
        return response.parameter().value();
    }
}
