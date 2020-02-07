package com.velopayments.oa3.api;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class PayorApiTest {

    @Autowired
    PayorsApi payorsApi;

    @Autowired
    VeloAPIProperties veloAPIProperties;

    @DisplayName("Test Get Payor V1")
    @Test
    void testGetPayorV1() {
        PayorV1 payorV1 = payorsApi.getPayorById(veloAPIProperties.getPayorIdUuid());

        assertNotNull(payorV1);
        assertNotNull(payorV1.getAddress());
        assertNotNull(payorV1.getPrimaryContactEmail());
        assertNotNull(payorV1.getLanguage());
    }

    @DisplayName("Test Get Payor V2")
    @Test
    void testGetPayorV2() {
        PayorV2 payorV2 = payorsApi.getPayorByIdV2(veloAPIProperties.getPayorIdUuid());

        assertNotNull(payorV2);
        assertNotNull(payorV2.getAddress());
        assertNotNull(payorV2.getPrimaryContactEmail());
        assertNotNull(payorV2.getLanguage());
    }

    @DisplayName("Test Get Branding")
    @Test
    void testGetBranding() {
        PayorBrandingResponse brandingResponse = payorsApi.payorGetBranding(veloAPIProperties.getPayorIdUuid());

        assertNotNull(brandingResponse);
        assertNotNull(brandingResponse.getPayorName());
    }

    @DisplayName("Test Get Payor Links by Descendents")
    @Test
    void testGetPayorLinksDescendents() {
        PayorLinksResponse payorLinksResponse = payorsApi.payorLinks(veloAPIProperties.getPayorIdUuid(), null, null);

        assertNotNull(payorLinksResponse);
    }

    @DisplayName("Test Get Payor Links py Parent")
    @Test
    void testGetPayorLinksParent() {
        PayorLinksResponse payorLinksResponse = payorsApi.payorLinks(null, veloAPIProperties.getPayorIdUuid(), null);

        assertNotNull(payorLinksResponse);
    }

    @DisplayName("Test Create Application")
    @Test
    void testCreateApplication() {

        String randomString = RandomStringUtils.randomAlphabetic(10);

        PayorCreateApplicationRequest payorCreateApplicationRequest = new PayorCreateApplicationRequest();
        payorCreateApplicationRequest.setDescription("SDK TestApp - " + randomString);
        payorCreateApplicationRequest.setName("SDK Test App Name - " + randomString);

        ResponseEntity<Void> createResponse = payorsApi.payorCreateApplicationRequestWithHttpInfo(veloAPIProperties.getPayorIdUuid(), payorCreateApplicationRequest);

        assertEquals(201, createResponse.getStatusCode().value());
    }

    @DisplayName("Test Reminder Email Opt-Out - True")
    @Test
    void testReminderEmailOptOutTrue() {
        PayorEmailOptOutRequest request = new PayorEmailOptOutRequest();
        request.setReminderEmailsOptOut(true);

        ResponseEntity<Void> responseEntity = payorsApi.payorEmailOptOutWithHttpInfo(veloAPIProperties.getPayorIdUuid(), request);

        assertEquals(202, responseEntity.getStatusCode().value());
    }

    @DisplayName("Test Reminder Email Opt-Out - False")
    @Test
    void testReminderEmailOptOutFalse() {
        PayorEmailOptOutRequest request = new PayorEmailOptOutRequest();
        request.setReminderEmailsOptOut(false);

        ResponseEntity<Void> responseEntity = payorsApi.payorEmailOptOutWithHttpInfo(veloAPIProperties.getPayorIdUuid(), request);

        assertEquals(202, responseEntity.getStatusCode().value());
    }

    @Disabled //currently failing
    @DisplayName("Test Create Application Key")
    @Test
    void testCreateAPIKey() {
        String randomString = RandomStringUtils.randomAlphabetic(10);

        PayorCreateApplicationRequest payorCreateApplicationRequest = new PayorCreateApplicationRequest();
        payorCreateApplicationRequest.setDescription("SDK Test Descrip - " + randomString);
        payorCreateApplicationRequest.setName("SDK Test App Name - " + randomString);

        ResponseEntity<Void> createResponse = payorsApi.payorCreateApplicationRequestWithHttpInfo(veloAPIProperties.getPayorIdUuid(), payorCreateApplicationRequest);

        assertEquals(201, createResponse.getStatusCode().value());

        URI location = createResponse.getHeaders().getLocation();

        String[] parts = StringUtils.splitByWholeSeparator(location.getPath(), "/");
        String appUuid = parts[4];

        PayorCreateApiKeyRequest createApiKeyRequest = new PayorCreateApiKeyRequest();
        createApiKeyRequest.setName("SDK KeyName - " + randomString );
        createApiKeyRequest.setDescription("Set by SDK Test");
        createApiKeyRequest.setRoles(Arrays.asList(PayorCreateApiKeyRequest.RolesEnum.ADMIN, PayorCreateApiKeyRequest.RolesEnum.SUPPORT));
        PayorCreateApiKeyResponse response = payorsApi.payorCreateApiKeyRequest(veloAPIProperties.getPayorIdUuid(), UUID.fromString(appUuid), createApiKeyRequest);

        assertNotNull(response);
        assertNotNull(response.getApiKey());
        assertNotNull(response.getApiSecret());
    }
}
