package com.velopayments.oa3.api;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class PayoutServiceApiTest {

    @Autowired
    PayoutServiceApi payoutServiceApi;

    @Autowired
    PaymentAuditServiceApi paymentAuditServiceApi;

    @Autowired
    PayeesApi payeesApi;

    @Autowired
    VeloAPIProperties veloAPIProperties;

    @Test
    void getPayoutsForPayorTest() {

        GetPayoutsResponse getPayoutsResponse= paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(),
                null, null, null, null, null, null, null,
                null, null, null, null);

        assertNotNull(getPayoutsResponse);
    }

    @Test
    void getPayoutTest() {

        GetPayoutsResponse getPayoutsResponse = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(),
                null, null, null, null, null, null, null,
                null, null, null, null);

        PayoutSummaryAudit payoutSummaryAuditV3 = getPayoutsResponse.getContent().get(0);

        PayoutSummaryResponseV3 payoutSummaryResponseV3 = payoutServiceApi.getPayoutSummaryV3(payoutSummaryAuditV3.getPayoutId());

        assertNotNull(payoutSummaryResponseV3);
    }

    @Disabled
    @Test
    void testInstructBadPayout() {

        //should fail
     //   instructPayoutApi.v3PayoutsPayoutIdPost(UUID.randomUUID());
    }

    @Test
    void testSubmitPayout() {

        URI location = submitPayout();

        assertNotNull(location);
        assertNotNull(getUUIDFromPayoutLocation(location));
    }

    @Test
    void testGetPayout()  {
        UUID payoutId = getUUIDFromPayoutLocation(submitPayout());

        PayoutSummaryResponseV3 summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        assertNotNull(summaryResponse);
        System.out.println(summaryResponse.getStatus());
    }

    @Test
    void testWithDrawlPayout() {
        URI location = submitPayout();
        UUID payoutId = getUUIDFromPayoutLocation(location);

        PayoutSummaryResponseV3 summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        payoutServiceApi.withdrawPayoutV3(payoutId);
    }

    @Test
    void testQuotePayout() {
        URI location = submitPayout();
        UUID payoutId = getUUIDFromPayoutLocation(location);

        PayoutSummaryResponseV3 summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        QuoteResponseV3 quoteResponse = payoutServiceApi.createQuoteForPayoutV3(payoutId);

        assertNotNull(quoteResponse);
    }

    @Test
    void testInstructPayout() {
        URI location = submitPayout();
        UUID payoutId = getUUIDFromPayoutLocation(location);

        PayoutSummaryResponseV3 summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        QuoteResponseV3 quoteResponse = payoutServiceApi.createQuoteForPayoutV3(payoutId);

        InstructPayoutRequest instructPayoutRequest = InstructPayoutRequest.builder()
                .build();

        ResponseEntity<Void> instructResponse = payoutServiceApi.instructPayoutV3WithHttpInfo(payoutId, instructPayoutRequest);

        assertNotNull(instructResponse);
        assertThat(instructResponse.getStatusCode().value()).isEqualTo(202);
    }

    PayoutSummaryResponseV3 awaitPayoutStatus(String payoutId, String status){
        await().atMost(15, TimeUnit.SECONDS).pollInterval(500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            PayoutSummaryResponseV3 summaryResponse = payoutServiceApi.getPayoutSummaryV3(UUID.fromString(payoutId));

            log.debug("Payout Status is: " + summaryResponse.getStatus());

            assertThat(summaryResponse.getStatus()).isEqualTo(status);
        });
        return payoutServiceApi.getPayoutSummaryV3(UUID.fromString(payoutId));
    }


    private URI submitPayout(){
        CreatePayoutRequestV3 createPayoutRequest = new CreatePayoutRequestV3();
        createPayoutRequest.setPayoutMemo("Java SDK Test");
        createPayoutRequest.setPayments(createPaymentInstructions(getOnboardedPayees()));

        ResponseEntity<Void> responseEntity = payoutServiceApi.submitPayoutV3WithHttpInfo(createPayoutRequest);

        return responseEntity.getHeaders().getLocation();
    }

    private UUID getUUIDFromPayoutLocation(URI uri){
        String[] parts = StringUtils.splitByWholeSeparator(uri.getPath(), "/");
        String appUuid = parts[2];
        return UUID.fromString(appUuid);
    }

    private List<PaymentInstructionV3> createPaymentInstructions(List<GetPayeeListResponse2> payeeResponseV3s){
        if(payeeResponseV3s == null){
            return new ArrayList<>();
        }

        List<PaymentInstructionV3> paymentInstructions = new ArrayList<>(payeeResponseV3s.size());

        payeeResponseV3s.forEach(payeeResponseV3 -> {
            PaymentInstructionV3 paymentInstruction = new PaymentInstructionV3();
            paymentInstruction.setRemoteId(payeeResponseV3.getPayorRefs().get(0).getRemoteId());
            paymentInstruction.setAmount(1000L);
            paymentInstruction.setCurrency("USD");
            paymentInstruction.setSourceAccountName("USD_Velo");
            paymentInstruction.setPaymentMemo("Java SDK Test");
            paymentInstruction.setPayorPaymentId(UUID.randomUUID().toString());

            paymentInstructions.add(paymentInstruction);
        });

        return paymentInstructions;
    }

    private List<GetPayeeListResponse2> getOnboardedPayees(){

        PagedPayeeResponse2 response = payeesApi.listPayeesV4(UUID.fromString(veloAPIProperties.getPayorId()),null, null, OnboardedStatus.ONBOARDED, null,
                null, null, null, null, null, null, null, null);

        return response.getContent();
    }

}
