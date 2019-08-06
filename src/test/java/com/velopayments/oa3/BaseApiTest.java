package com.velopayments.oa3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velopayments.oa3.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseApiTest {

    @Autowired
    public ApiClient apiClient;

    @Autowired
    public VeloAuthProperties veloAuthProperties;

    @Autowired
    public VeloAPIProperties veloAPIProperties;

    @Autowired
    public ObjectMapper objectMapper;

}
