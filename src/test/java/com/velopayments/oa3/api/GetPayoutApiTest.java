package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.GetPayoutsResponseV3;
import com.velopayments.oa3.model.PayoutSummaryAuditV3;
import com.velopayments.oa3.model.PayoutSummaryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class GetPayoutApiTest extends BaseApiTest {

    @Autowired
    GetPayoutApi getPayoutApi;

    @Autowired
    PaymentAuditServiceApi paymentAuditServiceApi;

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

        PayoutSummaryResponse payoutSummaryResponse = getPayoutApi.v3PayoutsPayoutIdGet(payoutSummaryAuditV3.getPayoutId());

        assertNotNull(payoutSummaryResponse);
    }
}
