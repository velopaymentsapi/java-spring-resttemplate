package com.velopayments.oa3.api;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
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

        GetPayoutsResponseV3 getPayoutsResponseV3 = paymentAuditServiceApi.getPayoutsForPayorV3(veloAPIProperties.getPayorIdUuid(),
                null, null, null, null, null, null, null);

        assertNotNull(getPayoutsResponseV3);
    }

    @Test
    void getPayoutTest() {

        GetPayoutsResponseV3 getPayoutsResponseV3 = paymentAuditServiceApi.getPayoutsForPayorV3(veloAPIProperties.getPayorIdUuid(),
                null, null, null, null, null, null, null);

        PayoutSummaryAuditV3 payoutSummaryAuditV3 = getPayoutsResponseV3.getContent().get(0);

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

        Assert.assertNotNull(location);
        Assert.assertNotNull(getUUIDFromPayoutLocation(location));
    }

    @Test
    void testGetPayout()  {
        UUID payoutId = getUUIDFromPayoutLocation(submitPayout());

        PayoutSummaryResponseV3 summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        Assert.assertNotNull(summaryResponse);
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

        Assert.assertNotNull(quoteResponse);
    }

    @Disabled //todo fix
    @Test
    void testInstructPayout() {
        URI location = submitPayout();
        UUID payoutId = getUUIDFromPayoutLocation(location);

        PayoutSummaryResponseV3 summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        QuoteResponseV3 quoteResponse = payoutServiceApi.createQuoteForPayoutV3(payoutId);

        ResponseEntity<Void> instructResponse = payoutServiceApi.instructPayoutV3WithHttpInfo(payoutId);

        Assert.assertNotNull(instructResponse);
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

    private List<PaymentInstructionV3> createPaymentInstructions(List<PayeeResponse2> payeeResponseV3s){
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

    private List<PayeeResponse2> getOnboardedPayees(){

        PagedPayeeResponse2 response = payeesApi.listPayeesV3(UUID.fromString(veloAPIProperties.getPayorId()),null, null, OnboardedStatus.ONBOARDED, null,
                null, "john.thompson+payee1", null, null, null, 10, null);

        return response.getContent();
    }

}
