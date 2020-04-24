package com.velopayments.oa3.api;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@DisplayName("Payment Audit")
@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class PaymentAuditServiceApiTest {

    @Autowired
    VeloAPIProperties veloAPIProperties;

    @Autowired
    PaymentAuditServiceApi paymentAuditServiceApi;

    @Autowired //todo - need to move these into Payment Audit
    PayoutHistoryApi payoutHistoryApi;

    @DisplayName("V1")
    @Nested
    class PaymentAuditV1 {

        @DisplayName("Test Payout Statistics")
        @Test
        void testGetPayoutStats() {
            GetPayoutStatistics getPayoutStatistics = payoutHistoryApi.getPayoutStatsV1(veloAPIProperties.getPayorIdUuid());

            assertThat(getPayoutStatistics).isNotNull();
            assertThat(getPayoutStatistics.getThisMonthPayoutsCount()).isGreaterThan(0);
        }

        @DisplayName("Test Get Fundings for Payor")
        @Test
        void testGetFundingsForPayor() {

            GetFundingsResponse getFundingsResponse = paymentAuditServiceApi.getFundingsV1(veloAPIProperties.getPayorIdUuid(), null, null, null);

            assertNotNull(getFundingsResponse);
            assertThat(getFundingsResponse.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Fundings for Payor With Paging")
        @Test
        void testGetFundingsForPayorWithPaging() {

            GetFundingsResponse getFundingsResponse = paymentAuditServiceApi.getFundingsV1(veloAPIProperties.getPayorIdUuid(), 2, 50, null);

            assertNotNull(getFundingsResponse);
            assertThat(getFundingsResponse.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test List Payment Changes")
        @Test
        void testListPaymentChanges() {
            OffsetDateTime dateTime = OffsetDateTime.now().minusYears(2);

            PaymentDeltaResponse paymentDeltaResponse = paymentAuditServiceApi.listPaymentChanges(veloAPIProperties.getPayorIdUuid(), dateTime, null, null);

            assertNotNull(paymentDeltaResponse);
            assertThat(paymentDeltaResponse.getContent().size()).isGreaterThan(0);
        }
    }

    @DisplayName("V3")
    @Nested
    class PaymentAuditV3 {
        @Deprecated
        @DisplayName("Test Get Payouts for Payors")
        @Test
        void testGetPayoursForPayorV3() {
            GetPayoutsResponseV3 getPayoutsResponseV3 = paymentAuditServiceApi.getPayoutsForPayorV3(veloAPIProperties.getPayorIdUuid(), null, null,
                    null, null, null, null, null);

            assertNotNull(getPayoutsResponseV3);
            assertThat(getPayoutsResponseV3.getContent().size()).isGreaterThan(0);
        }

        @Deprecated
        @DisplayName("Test Get Payments for Payout")
        @Test
        void testGetPaymentsForPayoutV3() {
            GetPayoutsResponseV3 getPayoutsResponseV3 = paymentAuditServiceApi.getPayoutsForPayorV3(veloAPIProperties.getPayorIdUuid(), null, "COMPLETED",
                    null, null, null, null, null);

            assertNotNull(getPayoutsResponseV3);
            PayoutSummaryAuditV3 payoutSummaryAuditV3 = getPayoutsResponseV3.getContent().get(0);

            GetPaymentsForPayoutResponseV3 getPaymentsForPayoutResponseV3 = paymentAuditServiceApi.getPaymentsForPayout(payoutSummaryAuditV3.getPayoutId(), null, null, null,
                    null, null, null, null, null, null,
                    null, null, null);

            assertNotNull(getPaymentsForPayoutResponseV3);
            assertThat(getPaymentsForPayoutResponseV3.getContent().size()).isGreaterThan(0);
        }

        @Deprecated
        @DisplayName("Test Get List of Payments")
        @Test
        void testListPaymentsV3() {

            ListPaymentsResponse listPaymentsResponse = paymentAuditServiceApi.listPaymentsAudit(null, veloAPIProperties.getPayorIdUuid(), null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null);

            assertNotNull(listPaymentsResponse);
            assertThat(listPaymentsResponse.getContent().size()).isGreaterThan(0);
        }

        @Deprecated
        @DisplayName("Test Get Payment")
        @Test
        void testGetPaymentV3() {
            ListPaymentsResponse listPaymentsResponse = paymentAuditServiceApi.listPaymentsAudit(null, veloAPIProperties.getPayorIdUuid(), null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null);

            assertNotNull(listPaymentsResponse);

            PaymentResponseV3 paymentResponseV3 = listPaymentsResponse.getContent().get(0);

            PaymentResponseV3 paymentDetails = paymentAuditServiceApi.getPaymentDetails(paymentResponseV3.getPaymentId(), false);

            assertNotNull(paymentDetails);
        }

        @Disabled("Need to write direct test - response is CSV")
        @DisplayName("Test Export Transactions")
        @Test
        void testExportTransactionsV3() {
            LocalDate startDate = LocalDate.now().minusMonths(2);
            LocalDate endDate = LocalDate.now();

            PayorAmlTransactionV3 payorAmlTransactionV3 = paymentAuditServiceApi.exportTransactionsCSVV3(veloAPIProperties.getPayorIdUuid(),
                    startDate, endDate);

            assertNotNull(payorAmlTransactionV3);
        }

    }
    @DisplayName("V4")
    @Nested
    class PaymentAuditV4 {
        @DisplayName("Test Get Payouts for Payor")
        @Test
        void testGetPayoutsForPayorV4() {
            GetPayoutsResponseV4 getPayoutsResponseV4 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, null,
                    null, null, null, null, null, null);

            assertNotNull(getPayoutsResponseV4);
            assertThat(getPayoutsResponseV4.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Payouts for Payor - REJECTED")
        @Test
        void testGetPayoutsForPayorV4Rejected() {
            GetPayoutsResponseV4 getPayoutsResponseV4 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, "REJECTED",
                    null, null, null, 1, 50, null);

            assertNotNull(getPayoutsResponseV4);
            assertThat(getPayoutsResponseV4.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Payouts for Payor - QUOTED")
        @Test
        void testGetPayoutsForPayorV4Quoted() {
            GetPayoutsResponseV4 getPayoutsResponseV4 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, "QUOTED",
                    null, null, null, 1, 50, null);

            assertNotNull(getPayoutsResponseV4);
            assertThat(getPayoutsResponseV4.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Payments for Payout")
        @Test
        void testGetPaymentsForPayoutV4() {
            GetPayoutsResponseV4 getPayoutsResponseV4 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, "COMPLETED",
                    null, null, null, null, null, null);

            assertNotNull(getPayoutsResponseV4);

            PayoutSummaryAuditV4 payoutSummaryAuditV4 = getPayoutsResponseV4.getContent().get(0);

            GetPaymentsForPayoutResponseV4 getPaymentsForPayoutResponseV4 = paymentAuditServiceApi.getPaymentsForPayoutV4(payoutSummaryAuditV4.getPayoutId(), null, null, null,
                    null, null, null, null, null,
                    null, null, null, null);

            assertNotNull(getPaymentsForPayoutResponseV4);
            assertThat(getPaymentsForPayoutResponseV4.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get List of Payments")
        @Test
        void testGetListOfPaymentsV4() {
            ListPaymentsResponseV4 listPaymentsResponseV4 = paymentAuditServiceApi.listPaymentsAuditV4(null, veloAPIProperties.getPayorIdUuid(), null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null);

            assertNotNull(listPaymentsResponseV4);
            assertThat(listPaymentsResponseV4.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Payment")
        @Test
        void testGetPaymentV4() {
            ListPaymentsResponseV4 listPaymentsResponseV4 = paymentAuditServiceApi.listPaymentsAuditV4(null, veloAPIProperties.getPayorIdUuid(), null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null,
                    null, null, null, null);

            assertNotNull(listPaymentsResponseV4);

            PaymentResponseV4 paymentResponseV4 = listPaymentsResponseV4.getContent().get(0);

            PaymentResponseV4 responseV4 = paymentAuditServiceApi.getPaymentDetailsV4(paymentResponseV4.getPaymentId(), false);

            assertNotNull(responseV4);
        }
    } //V4
}
