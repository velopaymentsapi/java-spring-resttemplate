package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DisplayName("Funding Manager")
@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class FundingManagerApiTest extends BaseApiTest {

    @Autowired
    FundingManagerApi fundingManagerApi;

    @DisplayName("V1")
    @Nested
    class FundingManagerV1Tests {
        @DisplayName("Test Get Source Accounts V1")
        @Test
        void getSourceAccountsV1Test() {
            ListSourceAccountResponse response = fundingManagerApi.getSourceAccounts(null, veloAPIProperties.getPayorIdUuid(), 1, 25, null);
            assertNotNull(response);
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Source Account V1")
        @Test
        void getSourceAccountTest() {
            UUID sourceAccountId = getSourceAccountUuid(UUID.fromString(veloAPIProperties.getPayorId()));

            SourceAccountResponse sourceAccountResponse = fundingManagerApi.getSourceAccount(sourceAccountId);

            assertNotNull(sourceAccountResponse);
        }

        @DisplayName("Create ACH Funding Request V1")
        @Test
        void createAchFundingRequestTest() {
            UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            FundingRequestV1 fundingRequestV1 = new FundingRequestV1();
            fundingRequestV1.amount(1000L);

            fundingManagerApi.createAchFundingRequest(sourceAccountId, fundingRequestV1);
        }

        @DisplayName("Test Notifications Request")
        @Test
        void setNotificationsRequestTest() {
            UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            SetNotificationsRequest request = new SetNotificationsRequest();
            request.setMinimumBalance(1000L);

            fundingManagerApi.setNotificationsRequest(sourceAccountId, request);
        }

        @DisplayName("Test Get Funding Accounts by Source Account - sensitive false")
        @Test
        void testGetFundingAccountsBySourceSenFalse() {
            UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            ListFundingAccountsResponse fundingAccountsResponse = fundingManagerApi.getFundingAccounts(null, sourceAccount, null, null, null, false);

            assertNotNull(fundingAccountsResponse);
            assertThat(fundingAccountsResponse.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Funding Accounts by PayorId - sensitive false")
        @Test
        void testGetFundingAccountsByPayorIdeSenFalse() {
            ListFundingAccountsResponse fundingAccountsResponse = fundingManagerApi.getFundingAccounts(veloAPIProperties.getPayorIdUuid(), null, null, null, null, false);

            assertNotNull(fundingAccountsResponse);
            assertThat(fundingAccountsResponse.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Funding Accounts - sensitive true")
        @Test
        void testGetFundingAccountsSenTrue() {
            UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            ListFundingAccountsResponse fundingAccountsResponse = fundingManagerApi.getFundingAccounts(null, sourceAccount, null, null, null, true);

            assertNotNull(fundingAccountsResponse);
            assertThat(fundingAccountsResponse.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Funding Account - sensitive false")
        @Test
        void testGetFundingAccountSenFalse() {
            UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            ListFundingAccountsResponse fundingAccountsResponse = fundingManagerApi.getFundingAccounts(null, sourceAccount, null, null, null, false);

            assertNotNull(fundingAccountsResponse);

            if(fundingAccountsResponse.getContent() != null && fundingAccountsResponse.getContent().size() != 0){
                FundingAccountResponse fundingAccountResponse = fundingAccountsResponse.getContent().get(0);
                FundingAccountResponse response = fundingManagerApi.getFundingAccount(fundingAccountResponse.getId(), false);
                assertNotNull(response);
            } else {
                log.warn("No Funding Accounts Found!!");
            }
        }

        @DisplayName("Test Get Funding Account - sensitive true")
        @Test
        void testGetFundingAccountSenTrue() {
            UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            ListFundingAccountsResponse fundingAccountsResponse = fundingManagerApi.getFundingAccounts(null, sourceAccount, null, null, null, false);

            assertNotNull(fundingAccountsResponse);

            if(fundingAccountsResponse.getContent() != null && fundingAccountsResponse.getContent().size() != 0){
                FundingAccountResponse fundingAccountResponse = fundingAccountsResponse.getContent().get(0);
                FundingAccountResponse response = fundingManagerApi.getFundingAccount(fundingAccountResponse.getId(), false);
                assertNotNull(response);
            } else {
                log.warn("No Funding Accounts Found!!");
            }
        }

        @Disabled("OA3 spec incorrect for response type See MVP-9121")
        @DisplayName("Test Get Funding Audit Delta")
        @Test
        void testGetFundingAuditDelta() {

            OffsetDateTime dateTime = OffsetDateTime.now().minusYears(2);

            PageResourceFundingPayorStatusAuditResponseFundingPayorStatusAuditResponse response =
                    fundingManagerApi.listFundingAuditDeltas(veloAPIProperties.getPayorIdUuid(), dateTime, null, null);

            assertNotNull(response);
            assertThat(response.getContent().size()).isGreaterThan(0);
        }
    }

    @DisplayName("V2")
    @Nested
    class FundingManagerV2Tests {
        @DisplayName("Test Get Source Accounts V2")
        @Test
        void getSourceAccountsV2Test() {
            ListSourceAccountResponseV2 response = fundingManagerApi.getSourceAccountsV2(null, null, veloAPIProperties.getPayorIdUuid(), null,1, 25, null);
            assertNotNull(response);
            assertThat(response.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Source Account V2")
        @Test
        void getSourceAccountV2Test() {
            UUID sourceAccountId = getSourceAccountUuid(UUID.fromString(veloAPIProperties.getPayorId()));

            SourceAccountResponseV2 sourceAccountResponse = fundingManagerApi.getSourceAccountV2(sourceAccountId);

            assertNotNull(sourceAccountResponse);
        }

        @DisplayName("Create ACH Funding Request V2")
        @Test
        void createFundingRequestTest() {
            UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            FundingRequestV2 fundingRequestV2 = new FundingRequestV2();
            fundingRequestV2.amount(1000L);

            fundingManagerApi.createFundingRequest(sourceAccountId, fundingRequestV2);
        }
    }

    private UUID getSourceAccountUuid(UUID payorIdUuid) {
        ListSourceAccountResponse listSourceAccountResponse = fundingManagerApi.getSourceAccounts(null, payorIdUuid, 1, 25, null);
        return listSourceAccountResponse.getContent().get(0).getId();
    }
}
