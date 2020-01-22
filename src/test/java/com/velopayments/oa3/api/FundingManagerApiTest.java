package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class FundingManagerApiTest extends BaseApiTest {

    @Autowired
    FundingManagerApi fundingManagerApi;

    @Test
    void testGetFundings() {
        GetFundingsResponse response = fundingManagerApi.getFundingsV1(veloAPIProperties.getPayorIdUuid(), 1, 2, null);

        assertNotNull(response);
    }

    @Test
    void getSourceAccountsTest() {
        ListSourceAccountResponseV2 response = fundingManagerApi.getSourceAccountsV2(null, null, veloAPIProperties.getPayorIdUuid(), null,1, 25, null);
        assertNotNull(response);
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Test
    void getSourceAccountsV2Test() {
        ListSourceAccountResponse response = fundingManagerApi.getSourceAccounts(null, veloAPIProperties.getPayorIdUuid(), 1, 25, null);
        assertNotNull(response);
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Test
    void getSourceAccountTest() {
        UUID sourceAccountId = getSourceAccountUuid(UUID.fromString(veloAPIProperties.getPayorId()));

        SourceAccountResponse sourceAccountResponse = fundingManagerApi.getSourceAccount(sourceAccountId);

        assertNotNull(sourceAccountResponse);
    }

    @Test
    void getSourceAccountV2Test() {
        UUID sourceAccountId = getSourceAccountUuid(UUID.fromString(veloAPIProperties.getPayorId()));

        SourceAccountResponseV2 sourceAccountResponse = fundingManagerApi.getSourceAccountV2(sourceAccountId);

        assertNotNull(sourceAccountResponse);
    }

    @Test
    void createAchFundingRequestTest() {
        UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

        FundingRequestV1 fundingRequestV1 = new FundingRequestV1();
        fundingRequestV1.amount(1000L);

        fundingManagerApi.createAchFundingRequest(sourceAccountId, fundingRequestV1);
    }

    @Test
    void createFundingRequestTest() {
        UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

        FundingRequestV2 fundingRequestV2 = new FundingRequestV2();
        fundingRequestV2.amount(1000L);

        fundingManagerApi.createFundingRequest(sourceAccountId, fundingRequestV2);
    }

    @Test
    void setNotificationsRequestTest() {
        UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

        SetNotificationsRequest request = new SetNotificationsRequest();
        request.setMinimumBalance(1000L);

        fundingManagerApi.setNotificationsRequest(sourceAccountId, request);

    }

    private UUID getSourceAccountUuid(UUID payorIdUuid) {
        ListSourceAccountResponse listSourceAccountResponse = fundingManagerApi.getSourceAccounts(null, payorIdUuid, 1, 25, null);
        return listSourceAccountResponse.getContent().get(0).getId();
    }
}
