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


    @DisplayName("V3")
    @Nested
    class PayeeInviteV3Tests {

        @DisplayName("Test Query Batch Status")
        @Test
        void queryBatchStatus() {
            ResponseEntity<CreatePayeesCSVResponseV4> response = payeeInvitationApi.v4CreatePayeeWithHttpInfo(buildCreatePayeeRequestV4());
            assertEquals(201, response.getStatusCode().value());

            URI location = response.getHeaders().getLocation();

            String[] parts = StringUtils.splitByWholeSeparator(location.getPath(), "/");
            String appUuid = parts[3];

            QueryBatchResponseV3 queryBatchResponse = payeeInvitationApi.queryBatchStatusV3(UUID.fromString(appUuid));
            assertNotNull(queryBatchResponse);
        }
    }

    @DisplayName("V4")
    @Nested
    class PayeeInviteV4Tests {

        @DisplayName("Test Get Payee Invitation Status")
        @Test
        void getPayeesInvitationStatusTest() {

            PagedPayeeInvitationStatusResponseV4 response = payeeInvitationApi.getPayeesInvitationStatusV4(UUID.fromString(veloAPIProperties.getPayorId()),null,null,1,100);

            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @Test
        void getPayeesInvitationStatusV3() {

            PagedPayeeInvitationStatusResponseV4 response = payeeInvitationApi.getPayeesInvitationStatusV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null, null, null);
            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @Test
        void getPayeeInvitationStatusV4ByPayeeId() {
            PagedPayeeResponseV4 pagedPayeeResponseV4 = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                    null, null, null, null, null, null, null, null);

            //get first payee id
            UUID payeeId = pagedPayeeResponseV4.getContent().get(0).getPayeeId();

            PagedPayeeInvitationStatusResponseV4 response =  payeeInvitationApi.getPayeesInvitationStatusV4(UUID.fromString(veloAPIProperties.getPayorId()), payeeId, null, null, null);

            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @Test
        void getPayeeInvitationStatusByStatus() {
            PagedPayeeInvitationStatusResponseV4 response = payeeInvitationApi.getPayeesInvitationStatusV4(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatusV4.PENDING, null, null);
            assertNotNull(response);
            assertNotNull(response.getContent());
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @Test
        void resendPayeeInviteTestV4() {
            PagedPayeeInvitationStatusResponseV4 pagedPayeeResponseV4 = payeeInvitationApi.getPayeesInvitationStatusV4(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatusV4.PENDING, null, null);

            //get first payee id
            UUID payeeId = pagedPayeeResponseV4.getContent().get(0).getPayeeId();

            InvitePayeeRequestV4 invitePayeeRequestV4 = new InvitePayeeRequestV4();
            invitePayeeRequestV4.setPayorId(UUID.fromString(veloAPIProperties.getPayorId()));

            payeeInvitationApi.resendPayeeInviteV4(payeeId, invitePayeeRequestV4);
        }

        @Test
        void createPayeeV4Test() {
            ResponseEntity<CreatePayeesCSVResponseV4> response = payeeInvitationApi.v4CreatePayeeWithHttpInfo(buildCreatePayeeRequestV4());

            assertNotNull(response);
            assertNotNull(response.getHeaders().getLocation());
        }

        @Test
        void createPayee3TestVerifyHttp() {
            ResponseEntity<CreatePayeesCSVResponseV4> response = payeeInvitationApi.v4CreatePayeeWithHttpInfo(buildCreatePayeeRequestV4());

            assertEquals(201, response.getStatusCode().value());
        }

        @DisplayName("Test Resend Payee Invitation")
        @Test
        void resendPayeeInvitation() {
            PagedPayeeInvitationStatusResponseV4 pagedPayeeResponse = payeeInvitationApi.getPayeesInvitationStatusV4(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatusV4.PENDING, null, null);

            //get first payee id
            UUID payeeId = pagedPayeeResponse.getContent().get(0).getPayeeId();

            ResponseEntity<Void> response = payeeInvitationApi.resendPayeeInviteV4WithHttpInfo(payeeId, InvitePayeeRequestV4.builder().build().builder().payorId(veloAPIProperties.getPayorIdUuid()).build());
            assertNotNull(response);
            assertThat(response.getStatusCode().value()).isEqualTo(200);
        }
    }

    //to do remove?
    CreatePayeesRequestV3 buildCreatePayeeRequestV3(){
        //random string to keep email unique
        String randomString = RandomStringUtils.randomAlphabetic(10);

        CreatePayeesRequestV3 createPayeesRequestV3 = new CreatePayeesRequestV3();
        createPayeesRequestV3.setPayorId(veloAPIProperties.getPayorId());
        CreatePayeeV3 createPayeeV3 = new CreatePayeeV3();
        createPayeeV3.setEmail("john.thompson+" + randomString + "@velopayments.com");
        createPayeeV3.setRemoteId(randomString);
        createPayeeV3.setType(PayeeType2.INDIVIDUAL);

        CreatePayeeAddressV3 createPayeeAddress = new CreatePayeeAddressV3();
        createPayeeAddress.setLine1("123 Main St");
        createPayeeAddress.setCity("St Petersburg");
        createPayeeAddress.setCountry(CreatePayeeAddressV3.CountryEnum.US);
        createPayeeAddress.setZipOrPostcode("33701");
        createPayeeV3.setAddress(createPayeeAddress);

        CreateIndividualV3 createIndividual = new CreateIndividualV3();
        CreateIndividualV3Name individualName = new CreateIndividualV3Name();
        individualName.setFirstName("SDKTest");
        individualName.setLastName(randomString);
        createIndividual.setName(individualName);
        createIndividual.dateOfBirth(LocalDate.now().minusYears(21));
        createIndividual.nationalIdentification("123121234");

        createPayeeV3.setIndividual(createIndividual);

        createPayeesRequestV3.addPayeesItem(createPayeeV3);
        return createPayeesRequestV3;
    }

    CreatePayeesRequestV4 buildCreatePayeeRequestV4(){
        //random string to keep email unique
        String randomString = RandomStringUtils.randomAlphabetic(10);

        CreatePayeesRequestV4 createPayeesRequestV4 = new CreatePayeesRequestV4();
        createPayeesRequestV4.setPayorId(veloAPIProperties.getPayorId());
        CreatePayeeV4 createPayeeV4 = new CreatePayeeV4();
        createPayeeV4.setEmail("john.thompson+" + randomString + "@velopayments.com");
        createPayeeV4.setRemoteId(randomString);
        createPayeeV4.setType(PayeeType2.INDIVIDUAL);

        CreatePayeeAddressV4 createPayeeAddress = new CreatePayeeAddressV4();
        createPayeeAddress.setLine1("123 Main St");
        createPayeeAddress.setCity("St Petersburg");
        createPayeeAddress.setCountry("US");
        createPayeeAddress.setZipOrPostcode("33701");
        createPayeeV4.setAddress(createPayeeAddress);

        CreateIndividualV4 createIndividual = new CreateIndividualV4();
        CreateIndividualV3Name individualName = new CreateIndividualV3Name();
        individualName.setFirstName("SDKTest");
        individualName.setLastName(randomString);
        createIndividual.setName(individualName);
        createIndividual.dateOfBirth(LocalDate.now().minusYears(21));
        createIndividual.nationalIdentification("123121234");

        createPayeeV4.setIndividual(createIndividual);

        createPayeesRequestV4.addPayeesItem(createPayeeV4);
        return createPayeesRequestV4;
    }
}
