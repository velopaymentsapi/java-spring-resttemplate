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

        @DisplayName("List Payees")
        @Nested
        class ListPayees{





            //todo add v3 delete payee by id
        }

        @DisplayName("V3")
        @Nested
        class PayeeV4 {

            @DisplayName("List Payees")
            @Nested
            class ListPayees{
                @DisplayName("Test List Payees No Params")
                @Test
                void testListPayeesV4() {
                    PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                            null, null, null, null, null, null, null, null);

                    assertNotNull(response);
                }

                @DisplayName("Test List Payees - Onboarded Status")
                @Test
                void testListPayeesV4ByOnboarded() {
                    PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null, OnboardedStatus.ONBOARDED, null,
                            null, null, null, null, null, null, null, null);

                    assertNotNull(response);
                    assertThat(response.getContent().size()).isGreaterThan(0);
                }

                @DisplayName("Test List Payees V4 by OfacStatus")
                @Test
                void testListPayeesV3ByOfacStatus() {
                    PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), WatchlistStatus.PASSED, null, OnboardedStatus.ONBOARDED, null,
                            null, null, null, null, null, 10, null, null);

                    assertNotNull(response);
                }

                @Disabled
                @DisplayName("Test List Payees V4 by Email")
                @Test
                void testListPayeesV4ByEmail() {
                    PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, "john.thompson+payee1@velopayments.com",
                            null, null, null, null, null, 10, null, null);

                    assertNotNull(response);
                    assertThat(response.getContent().size()).isEqualTo(1);
                }

                @Disabled
                @DisplayName("Test List Payees V4 by Payee Country")
                @Test
                void testListPayeesV4ByPayeeCountry() {
                    PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                            null, null, null, "US", null, 10, null, null);

                    assertNotNull(response);
                    assertThat(response.getContent().size()).isGreaterThan(0);
                }

                @Disabled
                @DisplayName("Test List Payees V4 by Display Name")
                @Test
                void testListPayeesV4ByDisplayName() {
                    PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                            "Thompson, John", null, null, null, null, 10, null, null);

                    assertNotNull(response);
                    assertThat(response.getContent().size()).isGreaterThan(0);
                }

                @DisplayName("Test List Payees V4 by Payee Type")
                @Test
                void testListPayeesV4ByPayeeType() {
                    PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                            null, null, PayeeType2.INDIVIDUAL, null, null, 10, null, null);

                    assertNotNull(response);
                    assertThat(response.getContent().size()).isGreaterThan(0);
                }

                @DisplayName("Test Get Payee By ID V4")
                @Test
                void testGetPayeeByIdV4() {
                    PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null, null,null, null,
                            null, null, null, null, null, null, null, null);
                    UUID payeeId = response.getContent().get(0).getPayeeId();

                    PayeeDetailResponse payee = payeesApi.getPayeeByIdV3(payeeId, false);
                    assertNotNull(payee);

                    PayeeDetailResponse payeeSensitive = payeesApi.getPayeeByIdV3(payeeId, true);
                    assertNotNull(payeeSensitive);
                }

                //todo add v3 delete payee by id
            }

        }



        @DisplayName("Test Update Remote Id")
        @Test
        void testUpdateRemoteIdV3() {
            GetPayeeListResponse2 payeeResponseV3 = inviteNewPayee();
            PayeeDetailResponse payee = payeesApi.getPayeeByIdV3(payeeResponseV3.getPayeeId(), false);
            System.out.println(payee.toString());
            assertThat(payee.getPayorRefs().size()).isGreaterThan(0);

            // newRandom string
            String randomString = RandomStringUtils.randomAlphabetic(10);

            UpdateRemoteIdRequest updateRemoteIdRequestV3 = new UpdateRemoteIdRequest();
            updateRemoteIdRequestV3.setPayorId(UUID.fromString(veloAPIProperties.getPayorId()));
            updateRemoteIdRequestV3.setRemoteId(randomString);

            ResponseEntity<Void> responseEntity = payeesApi.v3PayeesPayeeIdRemoteIdUpdatePostWithHttpInfo(payeeResponseV3.getPayeeId(), updateRemoteIdRequestV3);

            assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);

            PayeeDetailResponse updatedPayee = payeesApi.getPayeeByIdV3(payeeResponseV3.getPayeeId(), false);
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

        @DisplayName("Test Get Invitation Status")
        @Test
        void testGetInvitationStatusByPayeeId() {
            GetPayeeListResponse2 payeeResponseV3 = inviteNewPayee();

            PagedPayeeInvitationStatusResponse2 pagedPayeeInvitationStatusResponseV3 = payeeInvitationApi.getPayeesInvitationStatusV4(veloAPIProperties.getPayorIdUuid(), payeeResponseV3.getPayeeId(), null, null, null);

            assertThat(pagedPayeeInvitationStatusResponseV3.getContent().size()).isEqualTo(1);
            assertThat(pagedPayeeInvitationStatusResponseV3.getContent().get(0).getInvitationStatus()).isEqualByComparingTo(PayeeInvitationStatusResponse2.InvitationStatusEnum.PENDING);
        }

        @DisplayName("Test List Payee Changes")
        @Test
        void testListPayeeChanges() {
            OffsetDateTime changedSince = OffsetDateTime.now().minusYears(1);

            PayeeDeltaResponse2 payeeDeltaResponse2 = payeesApi.listPayeeChangesV4(veloAPIProperties.getPayorIdUuid(), changedSince, null, null);

            assertThat(payeeDeltaResponse2).isNotNull();
            assertThat(payeeDeltaResponse2.getContent().size()).isGreaterThan(0);
        }
    }

    //invite a new payee, wait for payee to be available
    private GetPayeeListResponse2 inviteNewPayee() {
        PayeeInvitationApiTest payeeInvitationApiTest = new PayeeInvitationApiTest();
        payeeInvitationApiTest.veloAPIProperties = veloAPIProperties;
        CreatePayeesRequest2 createPayeesRequestV3 = payeeInvitationApiTest.buildCreatePayeeRequestV3();

        //create new payee
        payeeInvitationApi.v4CreatePayee((createPayeesRequestV3));

        //can be a short lag while service creates payee
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            PagedPayeeResponse2 tempResponse = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null,null, null, null,
                    null, createPayeesRequestV3.getPayees().get(0).getRemoteId(), null, null, null, null, null, null);
            assertThat(tempResponse.getContent().size()).isGreaterThan(0);
        });

        PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()), null,null, null, null,
                null, createPayeesRequestV3.getPayees().get(0).getRemoteId(), null, null, null, null, null, null);

        //created payee
        return response.getContent().get(0);
    }
}
