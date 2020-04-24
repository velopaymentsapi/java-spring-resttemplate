package com.velopayments.oa3.api;


import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import static org.junit.Assert.assertNotNull;

@Slf4j
@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class PayoutTests {

    @Autowired
    VeloAPIProperties veloAPIProperties;

    @Autowired
    PayeesApi payeesApi;

    @Autowired
    SubmitPayoutApi submitPayoutApi;

    @Autowired
    GetPayoutApi getPayoutApi;

    @Autowired
    QuotePayoutApi quotePayoutApi;

    @Autowired
    InstructPayoutApi instructPayoutApi;

    @Autowired
    WithdrawPayoutApi withdrawPayoutApi;

    @Test
    void testSubmitPayout() {

        URI location = submitPayout();

        assertNotNull(location);
        assertNotNull(getUUIDFromPayoutLocation(location));
    }

    @Test
    void testGetPayout()  {
        UUID payoutId = getUUIDFromPayoutLocation(submitPayout());

        PayoutSummaryResponse summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        assertNotNull(summaryResponse);
        System.out.println(summaryResponse.getStatus());
    }

    @Test
    void testWithDrawlPayout() {
        URI location = submitPayout();
        UUID payoutId = getUUIDFromPayoutLocation(location);

        PayoutSummaryResponse summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        withdrawPayoutApi.v3PayoutsPayoutIdDelete(payoutId);
    }

    @Test
    void testQuotePayout() {
        URI location = submitPayout();
        UUID payoutId = getUUIDFromPayoutLocation(location);

        PayoutSummaryResponse summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        QuoteResponse quoteResponse = quotePayoutApi.v3PayoutsPayoutIdQuotePost(payoutId);

        assertNotNull(quoteResponse);
    }

    @Test
    void testInstructPayout() {
        URI location = submitPayout();
        UUID payoutId = getUUIDFromPayoutLocation(location);

        PayoutSummaryResponse summaryResponse = awaitPayoutStatus(payoutId.toString(), "ACCEPTED");

        QuoteResponse quoteResponse = quotePayoutApi.v3PayoutsPayoutIdQuotePost(payoutId);

        ResponseEntity<Void> instructResponse = instructPayoutApi.v3PayoutsPayoutIdPostWithHttpInfo(payoutId);

        assertNotNull(instructResponse);
        assertThat(instructResponse.getStatusCode().value()).isEqualTo(202);
    }

    PayoutSummaryResponse awaitPayoutStatus(String payoutId, String status){
        await().atMost(15, TimeUnit.SECONDS).pollInterval(500, TimeUnit.MILLISECONDS).untilAsserted(() -> {
            PayoutSummaryResponse summaryResponse = getPayoutApi.v3PayoutsPayoutIdGet(UUID.fromString(payoutId));

            log.debug("Payout Status is: " + summaryResponse.getStatus());

            assertThat(summaryResponse.getStatus()).isEqualTo(status);
        });
        return getPayoutApi.v3PayoutsPayoutIdGet(UUID.fromString(payoutId));
    }


    private URI submitPayout(){
        CreatePayoutRequest createPayoutRequest = new CreatePayoutRequest();
        createPayoutRequest.setPayoutMemo("Java SDK Test");
        createPayoutRequest.setPayments(createPaymentInstructions(getOnboardedPayees()));

        ResponseEntity<Void> responseEntity = submitPayoutApi.submitPayoutWithHttpInfo(createPayoutRequest);

        return responseEntity.getHeaders().getLocation();
    }

    private UUID getUUIDFromPayoutLocation(URI uri){
        String[] parts = StringUtils.splitByWholeSeparator(uri.getPath(), "/");
        String appUuid = parts[2];
        return UUID.fromString(appUuid);
    }

    private List<PaymentInstruction> createPaymentInstructions(List<PayeeResponse2> payeeResponseV3s){
        if(payeeResponseV3s == null){
            return new ArrayList<>();
        }

        List<PaymentInstruction> paymentInstructions = new ArrayList<>(payeeResponseV3s.size());

        payeeResponseV3s.forEach(payeeResponseV3 -> {
            PaymentInstruction paymentInstruction = new PaymentInstruction();
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

        PagedPayeeResponse2 response = payeesApi.listPayeesV3(UUID.fromString(veloAPIProperties.getPayorId()),null, OnboardedStatus.ONBOARDED, null,
                null, "john.thompson+payee1", null, null, null, 10, null);

        return response.getContent();
    }
}
