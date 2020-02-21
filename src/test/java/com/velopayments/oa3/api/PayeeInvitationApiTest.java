package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    @Test
    void getPayeesInvitationStatusTest() {

        InvitationStatusResponse response = payeeInvitationApi.getPayeesInvitationStatusV1(UUID.fromString(veloAPIProperties.getPayorId()));

        assertNotNull(response);
        assertNotNull(response.getPayeeInvitationStatuses());
        assertThat(response.getPayeeInvitationStatuses().size()).isGreaterThan(0);
    }

    @Test
    void getPayeesInvitationStatusV2Test() {
        PagedPayeeInvitationStatusResponseV2 response = payeeInvitationApi.getPayeesInvitationStatusV2(UUID.fromString(veloAPIProperties.getPayorId()), null, null, null, null);
        assertNotNull(response);
        assertNotNull(response.getContent());
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Test
    void getPayeesInvitationStatusV2TestByPayeeId() {
        PagedPayeeResponse pagedPayeeResponse = payeesApi.listPayeesV1(UUID.fromString(veloAPIProperties.getPayorId()), null, null, null,
                null, null, null, null, null, null, null);

        //get first payee id
        UUID payeeId = pagedPayeeResponse.getContent().get(0).getPayeeId();

        PagedPayeeInvitationStatusResponseV2 response = payeeInvitationApi.getPayeesInvitationStatusV2(UUID.fromString(veloAPIProperties.getPayorId()), payeeId, null, null, null);
        assertNotNull(response);
        assertNotNull(response.getContent());
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Test
    void getPayeesInvitationStatusV2TestByInvitationStatus() {
        PagedPayeeInvitationStatusResponseV2 response = payeeInvitationApi.getPayeesInvitationStatusV2(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatus.PENDING, null, null);
        assertNotNull(response);
        assertNotNull(response.getContent());
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Test
    void getPayeesInvitationStatusV3() {
        PagedPayeeInvitationStatusResponseV3 response = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, null, null, null);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Test
    void getPayeeInvitationStatusV3ByPayeeId() {
        PagedPayeeResponseV3 pagedPayeeResponseV3 = payeesApi.listPayeesV3(UUID.fromString(veloAPIProperties.getPayorId()), null, null, null,
                null, null, null, null, null, null, null);

        //get first payee id
        UUID payeeId = pagedPayeeResponseV3.getContent().get(0).getPayeeId();

        PagedPayeeInvitationStatusResponseV3 response =  payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), payeeId, null, null, null);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Test
    void getPayeeInvitationStatusByStatus() {
        PagedPayeeInvitationStatusResponseV3 response = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatus.PENDING, null, null);
        assertNotNull(response);
        assertNotNull(response.getContent());
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Test
    void resendPayeeInviteTestV1() {
        PagedPayeeInvitationStatusResponseV3 pagedPayeeResponseV3 = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatus.PENDING, null, null);

        //get first payee id
        UUID payeeId = pagedPayeeResponseV3.getContent().get(0).getPayeeId();

        InvitePayeeRequest invitePayeeRequest = new InvitePayeeRequest();
        invitePayeeRequest.setPayorId(UUID.fromString(veloAPIProperties.getPayorId()));

        payeeInvitationApi.resendPayeeInviteV1(payeeId, invitePayeeRequest);

    }

    @Test
    void resendPayeeInviteTestV3() {
        PagedPayeeInvitationStatusResponseV3 pagedPayeeResponseV3 = payeeInvitationApi.getPayeesInvitationStatusV3(UUID.fromString(veloAPIProperties.getPayorId()), null, InvitationStatus.PENDING, null, null);

        //get first payee id
        UUID payeeId = pagedPayeeResponseV3.getContent().get(0).getPayeeId();

        InvitePayeeRequestV3 invitePayeeRequestV3 = new InvitePayeeRequestV3();
        invitePayeeRequestV3.setPayorId(UUID.fromString(veloAPIProperties.getPayorId()));

        payeeInvitationApi.resendPayeeInviteV3(payeeId, invitePayeeRequestV3);

    }

    @Test
    void v2CreatePayeeTest() {
        //random string to keep email unique
        String randomString = RandomStringUtils.randomAlphabetic(10);

        CreatePayeesRequestV2 createPayeesRequest = new CreatePayeesRequestV2();
        createPayeesRequest.setPayorId(veloAPIProperties.getPayorId());
        CreatePayeeV2 createPayee = new CreatePayeeV2();
        createPayee.setEmail("john.thompson+" + randomString + "@velopayments.com");
        createPayee.setRemoteId(randomString);
        createPayee.setType(PayeeTypeV2.INDIVIDUAL);
        CreatePayeeAddressV2 createPayeeAddress = new CreatePayeeAddressV2();
        createPayeeAddress.setLine1("123 Main St");
        createPayeeAddress.setCity("St Petersburg");
        createPayeeAddress.setCountry("US");
        createPayeeAddress.setZipOrPostcode("33701");

        createPayee.setAddress(createPayeeAddress);

        createPayeesRequest.addPayeesItem(createPayee);

        CreateIndividualV2 createIndividual = new CreateIndividualV2();
        CreateIndividualNameV2 individualName = new CreateIndividualNameV2();
        individualName.setFirstName("SDKTest");
        individualName.setLastName(randomString);
        createIndividual.setName(individualName);
        createIndividual.dateOfBirth(LocalDate.now().minusYears(21));
        createIndividual.nationalIdentification("123121234");

        createPayee.setIndividual(createIndividual);

        ResponseEntity<CreatePayeesCSVResponseV2> response = payeeInvitationApi.v2CreatePayeeWithHttpInfo(createPayeesRequest);

        assertNotNull(response);
        assertNotNull(response.getHeaders().getLocation());
    }

    @Disabled
    @Test
    void v2QueryBatchStatusTest() {
    }

    @Test
    void createPayee3Test() {
          ResponseEntity<CreatePayeesCSVResponseV3> response = payeeInvitationApi.v3CreatePayeeWithHttpInfo(buildCreatePayeeRequestV3());

          assertNotNull(response);
          assertNotNull(response.getHeaders().getLocation());

    }

    @Test
    void createPayee3TestVerifyHttp() {
        ResponseEntity<CreatePayeesCSVResponseV3> response = payeeInvitationApi.v3CreatePayeeWithHttpInfo(buildCreatePayeeRequestV3());

        assertEquals(201, response.getStatusCode().value());
    }

    CreatePayeesRequestV3 buildCreatePayeeRequestV3(){
        //random string to keep email unique
        String randomString = RandomStringUtils.randomAlphabetic(10);

        CreatePayeesRequestV3 createPayeesRequestV3 = new CreatePayeesRequestV3();
        createPayeesRequestV3.setPayorId(veloAPIProperties.getPayorId());
        CreatePayeeV3 createPayeeV3 = new CreatePayeeV3();
        createPayeeV3.setEmail("john.thompson+" + randomString + "@velopayments.com");
        createPayeeV3.setRemoteId(randomString);
        createPayeeV3.setType(PayeeTypeV3.INDIVIDUAL);

        CreatePayeeAddressV3 createPayeeAddress = new CreatePayeeAddressV3();
        createPayeeAddress.setLine1("123 Main St");
        createPayeeAddress.setCity("St Petersburg");
        createPayeeAddress.setCountry("US");
        createPayeeAddress.setZipOrPostcode("33701");
        createPayeeV3.setAddress(createPayeeAddress);

        CreateIndividualV3 createIndividual = new CreateIndividualV3();
        CreateIndividualNameV3 individualName = new CreateIndividualNameV3();
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
