package com.velopayments.oa3.api;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Payee")
@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class PayeesApiTest {

    @Autowired
    PayeesApi payeesApi;

    @Autowired
    PayeeInvitationApi payeeInvitationApi;
    
    @Autowired
    VeloAPIProperties veloAPIProperties;

    @DisplayName("V3")
    @Nested
    class PayeeV3 {


        @DisplayName("Test Update Remote Id")
        @Test
        void testUpdateRemoteIdV3() {
            GetPayeeListResponseV4 payeeResponse = inviteNewPayee();
            PayeeDetailResponseV3 payee = payeesApi.getPayeeByIdV3(payeeResponse.getPayeeId(), false);
            System.out.println(payee.toString());
            assertThat(payee.getPayorRefs().size()).isGreaterThan(0);

            // newRandom string
            String randomString = RandomStringUtils.randomAlphabetic(10);

            UpdateRemoteIdRequestV3 updateRemoteIdRequestV3 = new UpdateRemoteIdRequestV3();
            updateRemoteIdRequestV3.setPayorId(UUID.fromString(veloAPIProperties.getPayorId()));
            updateRemoteIdRequestV3.setRemoteId(randomString);

            ResponseEntity<Void> responseEntity = payeesApi.v4PayeesPayeeIdRemoteIdUpdatePostWithHttpInfo(payeeResponse.getPayeeId(), updateRemoteIdRequestV3);

            assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);

            PayeeDetailResponseV3 updatedPayee = payeesApi.getPayeeByIdV3(payeeResponse.getPayeeId(), false);
            assertThat(updatedPayee.getPayorRefs().size()).isGreaterThan(0);
            updatedPayee.getPayorRefs().forEach(ref -> {
                System.out.println(ref.getPayorId());
                System.out.println(veloAPIProperties.getPayorIdUuid());

                if(ref.getPayorId().equals(veloAPIProperties.getPayorIdUuid())){
                    assertThat(ref.getRemoteId()).isEqualTo(randomString);
                } else {
                    assertThat(1).isEqualTo(3);
                }
            });
        }
    }

    @DisplayName("V4")
    @Nested
    class PayeeV4 {

        @DisplayName("List Payees")
        @Nested
        class ListPayees{
            @DisplayName("Test List Payees No Params")
            @Test
            void testListPayeesV4() {
                PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                        null, null, null, null, null, null, null, null);

                assertNotNull(response);
            }

            @DisplayName("Test List Payees - Onboarded Status")
            @Test
            void testListPayeesV4ByOnboarded() {
                PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null, "ONBOARDED", null,
                        null, null, null, null, null, null, null, null);

                assertNotNull(response);
                assertThat(response.getContent().size()).isGreaterThan(0);
            }

            @DisplayName("Test List Payees V4 by OfacStatus")
            @Test
            void testListPayeesV3ByOfacStatus() {
                PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), "PASSED", null, "ONBOARDED", null,
                        null, null, null, null, null, 10, null, null);

                assertNotNull(response);
            }

            @Disabled
            @DisplayName("Test List Payees V4 by Email")
            @Test
            void testListPayeesV4ByEmail() {
                PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, "john.thompson+payee1@velopayments.com",
                        null, null, null, null, null, 10, null, null);

                assertNotNull(response);
                assertThat(response.getContent().size()).isEqualTo(1);
            }

            @Disabled
            @DisplayName("Test List Payees V4 by Payee Country")
            @Test
            void testListPayeesV4ByPayeeCountry() {
                PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                        null, null, null, "US", null, 10, null, null);

                assertNotNull(response);
                assertThat(response.getContent().size()).isGreaterThan(0);
            }

            @Disabled
            @DisplayName("Test List Payees V4 by Display Name")
            @Test
            void testListPayeesV4ByDisplayName() {
                PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                        "Thompson, John", null, null, null, null, 10, null, null);

                assertNotNull(response);
                assertThat(response.getContent().size()).isGreaterThan(0);
            }

            @DisplayName("Test List Payees V4 by Payee Type")
            @Test
            void testListPayeesV4ByPayeeType() {
                PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                        null, null, "INDIVIDUAL", null, null, 10, null, null);

                assertNotNull(response);
                assertThat(response.getContent().size()).isGreaterThan(0);
            }

            @DisplayName("Test Get Payee By ID V4")
            @Test
            void testGetPayeeByIdV4() {
                PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                        null, null, null, null, null, null, null, null);
                UUID payeeId = response.getContent().get(0).getPayeeId();

                PayeeDetailResponseV3 payee = payeesApi.getPayeeByIdV3(payeeId, false);
                assertNotNull(payee);

                PayeeDetailResponseV3 payeeSensitive = payeesApi.getPayeeByIdV3(payeeId, true);
                assertNotNull(payeeSensitive);
            }

            @DisplayName("Test List Payee Changes")
            @Test
            void testListPayeeChanges() {
                OffsetDateTime changedSince = OffsetDateTime.now().minusYears(1);

                PayeeDeltaResponseV4 payeeDeltaResponse2 = payeesApi.listPayeeChangesV4(veloAPIProperties.getPayorIdUuid(), changedSince, null, null);

                assertThat(payeeDeltaResponse2).isNotNull();
                assertThat(payeeDeltaResponse2.getContent().size()).isGreaterThan(0);
            }

            @DisplayName("Test Get Invitation Status")
            @Test
            void testGetInvitationStatusByPayeeId() {
                GetPayeeListResponseV4 payeeResponse = inviteNewPayee();

                PagedPayeeInvitationStatusResponseV4 pagedPayeeInvitationStatusResponse = payeeInvitationApi.getPayeesInvitationStatusV4(veloAPIProperties.getPayorIdUuid(), payeeResponse.getPayeeId(), null, null, null);

                assertThat(pagedPayeeInvitationStatusResponse.getContent().size()).isEqualTo(1);
                assertThat(pagedPayeeInvitationStatusResponse.getContent().get(0).getInvitationStatus()).isEqualTo("PENDING");
            }

            //todo add v4 delete payee by id
        }
    }

    //invite a new payee, wait for payee to be available
    private GetPayeeListResponseV4 inviteNewPayee() {
        PayeeInvitationApiTest payeeInvitationApiTest = new PayeeInvitationApiTest();
        payeeInvitationApiTest.veloAPIProperties = veloAPIProperties;
        CreatePayeesRequestV4 createPayeesRequest = payeeInvitationApiTest.buildCreatePayeeRequestV4();

        //create new payee
        payeeInvitationApi.v4CreatePayee((createPayeesRequest));

        //can be a short lag while service creates payee
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            PagedPayeeResponseV4 tempResponse = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null,null, null, null,
                    null, createPayeesRequest.getPayees().get(0).getRemoteId(), null, null, null, null, null, null);
            assertThat(tempResponse.getContent().size()).isGreaterThan(0);
        });

        PagedPayeeResponseV4 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null,null, null, null,
                null, createPayeesRequest.getPayees().get(0).getRemoteId(), null, null, null, null, null, null);

        //created payee
        return response.getContent().get(0);
    }
}
