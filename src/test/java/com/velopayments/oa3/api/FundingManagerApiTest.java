package com.velopayments.oa3.api;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.GetFundingsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class FundingManagerApiTest {

    @Autowired
    FundingManagerApi fundingManagerApi;

    @Autowired
    VeloAPIProperties veloAPIProperties;

    @Test
    void testGetFundings() {
        GetFundingsResponse response = fundingManagerApi.getFundings(UUID.fromString(veloAPIProperties.getPayorId()), 1, 2, null);

        assertNotNull(response);
    }
}
