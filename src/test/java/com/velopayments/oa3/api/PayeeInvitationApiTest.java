package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Payee Invitation")
@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class PayeeInvitationApiTest extends BaseApiTest {

    @Autowired
    PayeeInvitationApi payeeInvitationApi;

    @Autowired
    VeloAPIProperties veloAPIProperties;

    @Autowired
    PayeesApi payeesApi;

    @DisplayName("V1")
    @Nested
    class PayeeInviteV1Tests {

        @DisplayName("Test Get Payee Invitation Status")
        @Test
        void getPayeesInvitationStatusTest() {

            PagedPayeeInvitationStatusResponse2 response = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()),null,null,1,100);

            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Resend Payee Invitation")
        @Test
        void resendPayeeInvitation() {
            PagedPayeeInvitationStatusResponse2 pagedPayeeResponseV3 = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatus.PENDING, null, null);

            //get first payee id
            UUID payeeId = pagedPayeeResponseV3.getContent().get(0).getPayeeId();

            ResponseEntity<Void> response = payeeInvitationApi.resendPayeeInviteV3WithHttpInfo(payeeId, InvitePayeeRequest2.builder().payorId(veloAPIProperties.getPayorIdUuid()).build());
            assertNotNull(response);
            assertThat(response.getStatusCode().value()).isEqualTo(200);
        }
    }

    @DisplayName("V2")
    @Nested
    class PayeeInviteV2Tests {

        @DisplayName("Test Get Payee Invitation Status")
        @Test
        void getPayeesInvitationStatusV2Test() {
            PagedPayeeInvitationStatusResponse2 response = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, null, null, null);
            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Payee Invitation Status - by PENDING Status")
        @Test
        void getPayeesInvitationStatusV2TestByInvitationStatus() {
            PagedPayeeInvitationStatusResponse2 response = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatus.PENDING, null, null);

            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Create Payee")
        @Test
        void v2CreatePayeeTest() {
            ResponseEntity<CreatePayeesCSVResponse2> response = payeeInvitationApi.v3CreatePayeeWithHttpInfo(generateCreatePayeeRequestV2());
            assertNotNull(response);
            assertNotNull(response.getHeaders().getLocation());
        }
    }

    CreatePayeesRequest2 generateCreatePayeeRequestV2() {
        //random string to keep email unique
        String randomString = RandomStringUtils.randomAlphabetic(10);

        CreatePayeesRequest2 createPayeesRequest = new CreatePayeesRequest2();
        createPayeesRequest.setPayorId(veloAPIProperties.getPayorId());
        CreatePayee2 createPayee = new CreatePayee2();
        createPayee.setEmail("john.thompson+" + randomString + "@velopayments.com");
        createPayee.setRemoteId(randomString);
        createPayee.setType(PayeeType.INDIVIDUAL);
        CreatePayeeAddress2 createPayeeAddress = new CreatePayeeAddress2();
        createPayeeAddress.setLine1("123 Main St");
        createPayeeAddress.setCity("St Petersburg");
        createPayeeAddress.setCountry(CreatePayeeAddress2.CountryEnum.US);
        createPayeeAddress.setZipOrPostcode("33701");

        createPayee.setAddress(createPayeeAddress);

        createPayeesRequest.addPayeesItem(createPayee);

        CreateIndividual2 createIndividual = new CreateIndividual2();
        CreateIndividual2Name individualName = new CreateIndividual2Name();
        individualName.setFirstName("SDKTest");
        individualName.setLastName(randomString);
        createIndividual.setName(individualName);
        createIndividual.dateOfBirth(LocalDate.now().minusYears(21));
        createIndividual.nationalIdentification("123121234");

        createPayee.setIndividual(createIndividual);

        return createPayeesRequest;
    }

    @DisplayName("V3")
    @Nested
    class PayeeInviteV3Tests {

        @Test
        void getPayeesInvitationStatusV3() {
            PagedPayeeInvitationStatusResponse2 response = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, null, null, null);

            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @Test
        void getPayeeInvitationStatusV3ByPayeeId() {
            PagedPayeeResponse2 pagedPayeeResponseV3 = payeesApi.listPayeesV3(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                    null, null, null, null, null, null, null);

            //get first payee id
            UUID payeeId = pagedPayeeResponseV3.getContent().get(0).getPayeeId();

            PagedPayeeInvitationStatusResponse2 response =  payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), payeeId, null, null, null);

            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @Test
        void getPayeeInvitationStatusByStatus() {
            PagedPayeeInvitationStatusResponse2 response = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatus.PENDING, null, null);
            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }


        @Test
        void resendPayeeInviteTestV3() {
            PagedPayeeInvitationStatusResponse2 pagedPayeeResponseV3 = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatus.PENDING, null, null);

            //get first payee id
            UUID payeeId = pagedPayeeResponseV3.getContent().get(0).getPayeeId();

            InvitePayeeRequest2 invitePayeeRequestV3 = new InvitePayeeRequest2();
            invitePayeeRequestV3.setPayorId(UUID.fromString(veloAPIProperties.getPayorId()));

            payeeInvitationApi.resendPayeeInviteV3(payeeId, invitePayeeRequestV3);
        }

        @Test
        void createPayee3Test() {
            ResponseEntity<CreatePayeesCSVResponse2> response = payeeInvitationApi.v3CreatePayeeWithHttpInfo(buildCreatePayeeRequestV3());

            assertNotNull(response);
            assertNotNull(response.getHeaders().getLocation());
        }

        @Test
        void createPayee3TestVerifyHttp() {
            ResponseEntity<CreatePayeesCSVResponse2> response = payeeInvitationApi.v3CreatePayeeWithHttpInfo(buildCreatePayeeRequestV3());

            assertEquals(201, response.getStatusCode().value());
        }

        @DisplayName("Test Query Batch Status")
        @Test
        void queryBatchStatus() {
            ResponseEntity<CreatePayeesCSVResponse2> response = payeeInvitationApi.v3CreatePayeeWithHttpInfo(buildCreatePayeeRequestV3());
            assertEquals(201, response.getStatusCode().value());

            URI location = response.getHeaders().getLocation();

            String[] parts = StringUtils.splitByWholeSeparator(location.getPath(), "/");
            String appUuid = parts[3];

            QueryBatchResponse2 queryBatchResponse = payeeInvitationApi.queryBatchStatusV3(UUID.fromString(appUuid));
            assertNotNull(queryBatchResponse);
        }
    }

    CreatePayeesRequest2 buildCreatePayeeRequestV3(){
        //random string to keep email unique
        String randomString = RandomStringUtils.randomAlphabetic(10);

        CreatePayeesRequest2 createPayeesRequestV3 = new CreatePayeesRequest2();
        createPayeesRequestV3.setPayorId(veloAPIProperties.getPayorId());
        CreatePayee2 createPayeeV3 = new CreatePayee2();
        createPayeeV3.setEmail("john.thompson+" + randomString + "@velopayments.com");
        createPayeeV3.setRemoteId(randomString);
        createPayeeV3.setType(PayeeType.INDIVIDUAL);

        CreatePayeeAddress2 createPayeeAddress = new CreatePayeeAddress2();
        createPayeeAddress.setLine1("123 Main St");
        createPayeeAddress.setCity("St Petersburg");
        createPayeeAddress.setCountry(CreatePayeeAddress2.CountryEnum.US);
        createPayeeAddress.setZipOrPostcode("33701");
        createPayeeV3.setAddress(createPayeeAddress);

        CreateIndividual2 createIndividual = new CreateIndividual2();
        CreateIndividual2Name individualName = new CreateIndividual2Name();
        individualName.setFirstName("SDKTest");
        individualName.setLastName(randomString);
        createIndividual.setName(individualName);
        createIndividual.dateOfBirth(LocalDate.now().minusYears(21));
        createIndividual.nationalIdentification("123121234");

        createPayeeV3.setIndividual(createIndividual);

        createPayeesRequestV3.addPayeesItem(createPayeeV3);
        return createPayeesRequestV3;
    }
}
