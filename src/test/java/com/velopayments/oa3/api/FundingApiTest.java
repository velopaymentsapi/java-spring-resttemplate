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
public class FundingApiTest extends BaseApiTest {

    @Autowired
    SourceAccountsApi sourceAccountsApi;

    @Autowired
    FundingApi fundingApi;

    @DisplayName("V1")
    @Nested
    class FundingManagerV1Tests {
        @DisplayName("Create ACH Funding Request V1")
        @Test
        void createAchFundingRequestTest() {
            UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            FundingRequestV3 fundingRequestV3 = new FundingRequestV3();
            fundingRequestV3.fundingAccountId(getFundingAccount());
            fundingRequestV3.amount(1000L);

            fundingApi.createFundingRequestV3(sourceAccountId, fundingRequestV3);
        }

        @DisplayName("Test Get Funding Accounts by Source Account - sensitive false")
        @Test
        void testGetFundingAccountsBySourceSenFalse() {
            UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            ListFundingAccountsResponseV2 fundingAccountsResponse = fundingApi.getFundingAccountsV2(null, null, null, null, null, 1, 25, "accountName:asc", false);

            assertNotNull(fundingAccountsResponse);
            assertThat(fundingAccountsResponse.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Funding Accounts by PayorId - sensitive false")
        @Test
        void testGetFundingAccountsByPayorIdeSenFalse() {
            ListFundingAccountsResponseV2 fundingAccountsResponse = fundingApi.getFundingAccountsV2(veloAPIProperties.getPayorIdUuid(), null, null, null, null, 1, 25, "accountName:asc", false);

            assertNotNull(fundingAccountsResponse);
            assertThat(fundingAccountsResponse.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Funding Accounts - sensitive true")
        @Test
        void testGetFundingAccountsSenTrue() {
            UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            ListFundingAccountsResponseV2 fundingAccountsResponse = fundingApi.getFundingAccountsV2(null, null, null, null, null, 1, 25, "accountName:asc", true);

            assertNotNull(fundingAccountsResponse);
            assertThat(fundingAccountsResponse.getContent().size()).isGreaterThan(0);
        }

        @DisplayName("Test Get Funding Account - sensitive false")
        @Test
        void testGetFundingAccountSenFalse() {
            UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            ListFundingAccountsResponseV2 fundingAccountsResponse = fundingApi.getFundingAccountsV2(null, null, null, null, null, 1, 25, "accountName:asc", false);

            assertNotNull(fundingAccountsResponse);

            if(fundingAccountsResponse.getContent() != null && fundingAccountsResponse.getContent().size() != 0){
                FundingAccountResponseV2 fundingAccountResponse = fundingAccountsResponse.getContent().get(0);
                Object response = fundingApi.getFundingAccountV2(fundingAccountResponse.getId(), false);
                assertNotNull(response);
            } else {
                log.warn("No Funding Accounts Found!!");
            }
        }

        @DisplayName("Test Get Funding Account - sensitive true")
        @Test
        void testGetFundingAccountSenTrue() {
            UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            ListFundingAccountsResponseV2 fundingAccountsResponse = fundingApi.getFundingAccountsV2(null, null, null, null, null, 1, 25, "accountName:asc", false);

            assertNotNull(fundingAccountsResponse);

            if(fundingAccountsResponse.getContent() != null && fundingAccountsResponse.getContent().size() != 0){
                FundingAccountResponseV2 fundingAccountResponse = fundingAccountsResponse.getContent().get(0);
                FundingAccountResponseV2 response = fundingApi.getFundingAccountV2(fundingAccountResponse.getId(), false);
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
                    fundingApi.listFundingAuditDeltas(veloAPIProperties.getPayorIdUuid(), dateTime, null, null);

            assertNotNull(response);
            assertThat(response.getContent().size()).isGreaterThan(0);
        }
    }

    @DisplayName("V2")
    @Nested
    class FundingManagerV3Tests {
        @DisplayName("Create ACH Funding Request V3")
        @Test
        void createFundingRequestTest() {
            UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

            FundingRequestV3 fundingRequestV3 = new FundingRequestV3();
            fundingRequestV3.amount(1000L);
            fundingRequestV3.setFundingAccountId(getFundingAccount());

            fundingApi.createFundingRequestV3(sourceAccountId, fundingRequestV3);
        }
    }

    private UUID getSourceAccountUuid(UUID payorIdUuid) {
        ListSourceAccountResponseV3 listSourceAccountResponse = sourceAccountsApi.getSourceAccountsV3(null, null, payorIdUuid, null, false, null, 1, 25, null);
        return listSourceAccountResponse.getContent().get(0).getId();
    }

    private UUID getFundingAccount() {
        //UUID sourceAccount = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

        ListFundingAccountsResponseV2 fundingAccountsResponseV2 = fundingApi.getFundingAccountsV2(veloAPIProperties.getPayorIdUuid(),
                null, null, null, null, null, null, null, null);

        return fundingAccountsResponseV2.getContent().get(0).getId();
    }
}
