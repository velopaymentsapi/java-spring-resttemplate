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
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Payment Audit")
@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class PaymentAuditServiceApiTest {

    @Autowired
    VeloAPIProperties veloAPIProperties;

    @Autowired
    PaymentAuditServiceApi paymentAuditServiceApi;


    @DisplayName("V1")
    @Nested
    class PaymentAuditV1 {

        @DisplayName("Test Payout Statistics")
        @Test
        void testGetPayoutStats() {
            GetPayoutStatistics getPayoutStatistics = paymentAuditServiceApi.getPayoutStatsV4(veloAPIProperties.getPayorIdUuid());

            assertThat(getPayoutStatistics).isNotNull();
            assertThat(getPayoutStatistics.getThisMonthPayoutsCount()).isGreaterThan(0);
        }

        @Disabled
        @DisplayName("Test List Payment Changes")
        @Test
        void testListPaymentChanges() {
            OffsetDateTime dateTime = OffsetDateTime.now().minusYears(2);

            PaymentDeltaResponse paymentDeltaResponse = paymentAuditServiceApi.listPaymentChangesV4(veloAPIProperties.getPayorIdUuid(), dateTime, null, null);

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
            GetPayoutsResponse getPayoutsResponseV3 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, null,
                    null, null, null, null, null, null, null, null, null);

            assertNotNull(getPayoutsResponseV3);
            assertThat(getPayoutsResponseV3.getContent().size()).isGreaterThan(0);
        }

        @Deprecated
        @DisplayName("Test Get Payments for Payout")
        @Test
        void testGetPaymentsForPayoutV3() {
            GetPayoutsResponse getPayoutsResponseV3 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, "COMPLETED",
                    null, null, null, null, null, null, null, null, null);

            assertNotNull(getPayoutsResponseV3);
            PayoutSummaryAudit payoutSummaryAuditV3 = getPayoutsResponseV3.getContent().get(0);

            GetPaymentsForPayoutResponseV4 getPaymentsForPayoutResponseV4 = paymentAuditServiceApi.getPaymentsForPayoutV4(payoutSummaryAuditV3.getPayoutId(), null, null, null,
                    null, null, null, null, null, null,
                    null, null, null, null, null, false);

            assertNotNull(getPaymentsForPayoutResponseV4);
            assertThat(getPaymentsForPayoutResponseV4.getContent().size()).isGreaterThan(0);
        }

        @Deprecated
        @DisplayName("Test Get List of Payments")
        @Test
        void testListPaymentsV3() {

            ListPaymentsResponseV4 listPaymentsResponse = paymentAuditServiceApi.listPaymentsAuditV4(null, veloAPIProperties.getPayorIdUuid(), null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null, null, false);

            assertNotNull(listPaymentsResponse);
            assertThat(listPaymentsResponse.getContent().size()).isGreaterThan(0);
        }

        @Deprecated
        @DisplayName("Test Get Payment")
        @Test
        void testGetPaymentV3() {
            ListPaymentsResponseV4 listPaymentsResponse = paymentAuditServiceApi.listPaymentsAuditV4(null, veloAPIProperties.getPayorIdUuid(), null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null, null, false);

            assertNotNull(listPaymentsResponse);

            PaymentResponseV4 paymentResponseV4 = listPaymentsResponse.getContent().get(0);

            PaymentResponseV4 paymentDetails = paymentAuditServiceApi.getPaymentDetailsV4(paymentResponseV4.getPaymentId(), false);

            assertNotNull(paymentDetails);
        }

        @Disabled("Need to write direct test - response is CSV")
        @DisplayName("Test Export Transactions")
        @Test
        void testExportTransactionsV3() {
            LocalDate startDate = LocalDate.now().minusMonths(2);
            LocalDate endDate = LocalDate.now();

            PayorAmlTransaction payorAmlTransactionV3 = paymentAuditServiceApi.exportTransactionsCSVV4(veloAPIProperties.getPayorIdUuid(),
                    startDate, endDate, null);

            assertNotNull(payorAmlTransactionV3);
        }

    }
    @DisplayName("V4")
    @Nested
    class PaymentAuditV4 {
        @DisplayName("Test Get Payouts for Payor")
        @Test
        void testGetPayoutsForPayorV4() {
            GetPayoutsResponse getPayoutsResponseV4 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, null,
                    null, null, null, null, null, null, null, null, null);

            assertNotNull(getPayoutsResponseV4);
            assertThat(getPayoutsResponseV4.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Payouts for Payor - REJECTED")
        @Test
        void testGetPayoutsForPayorV4Rejected() {
            GetPayoutsResponse getPayoutsResponseV4 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, "REJECTED",
                    null, null, null, null, null, null,1, 50, null);

            assertNotNull(getPayoutsResponseV4);
            assertThat(getPayoutsResponseV4.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Payouts for Payor - QUOTED")
        @Test
        void testGetPayoutsForPayorV4Quoted() {
            GetPayoutsResponse getPayoutsResponseV4 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, "QUOTED",
                    null, null, null, null, null, null,1, 50, null);

            assertNotNull(getPayoutsResponseV4);
            assertThat(getPayoutsResponseV4.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Payments for Payout")
        @Test
        void testGetPaymentsForPayoutV4() {
            GetPayoutsResponse getPayoutsResponseV4 = paymentAuditServiceApi.getPayoutsForPayorV4(veloAPIProperties.getPayorIdUuid(), null, "COMPLETED",
                    null, null, null, null, null, null, null, null, null);

            assertNotNull(getPayoutsResponseV4);

            PayoutSummaryAudit payoutSummaryAuditV4 = getPayoutsResponseV4.getContent().get(0);

            GetPaymentsForPayoutResponseV4 getPaymentsForPayoutResponseV4 = paymentAuditServiceApi.getPaymentsForPayoutV4(payoutSummaryAuditV4.getPayoutId(), null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null, null, false);

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
                    null, null, null, null,
                    null, null, null, null, null, null, false);

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
                    null, null, null, null,
                    null, null, null, null, null, null, false);

            assertNotNull(listPaymentsResponseV4);

            PaymentResponseV4 paymentResponseV4 = listPaymentsResponseV4.getContent().get(0);

            PaymentResponseV4 responseV4 = paymentAuditServiceApi.getPaymentDetailsV4(paymentResponseV4.getPaymentId(), false);

            assertNotNull(responseV4);
        }
    } //V4
}
