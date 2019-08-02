package com.velopayments.oa3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velopayments.oa3.client.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseApiTest {

    @Autowired
    ApiClient apiClient;

    @Autowired
    VeloAuthProperties veloAuthProperties;

    @Autowired
    ObjectMapper objectMapper;

}
