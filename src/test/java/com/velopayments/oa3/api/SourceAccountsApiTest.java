package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

    @Slf4j
    @DisplayName("Source Account")
    @WebMvcTest()
    @ContextConfiguration(classes = VeloConfig.class)
    @ComponentScan(basePackages = {"com.velopayments.oa3.config"})
    public class SourceAccountsApiTest extends BaseApiTest {

        @Autowired
        SourceAccountsApi sourceAccountsApi;

        @DisplayName("V1")
        @Nested
        class FundingManagerV1Tests {
            @DisplayName("Test Get Source Accounts V1")
            @Test
            void getSourceAccountsV1Test() {
                ListSourceAccountResponseV3 response = sourceAccountsApi.getSourceAccountsV3(null, null, veloAPIProperties.getPayorIdUuid(), null, false, null, 1, 25, null);
                assertNotNull(response);
                assertThat(response.getContent().size()).isGreaterThan(0);
            }

            @DisplayName("Test Get Source Account V1")
            @Test
            void getSourceAccountTest() {
                UUID sourceAccountId = getSourceAccountUuid(UUID.fromString(veloAPIProperties.getPayorId()));

                SourceAccountResponseV3 sourceAccountResponse = sourceAccountsApi.getSourceAccountV3(sourceAccountId);

                assertNotNull(sourceAccountResponse);
            }

            @DisplayName("Test Notifications Request")
            @Test
            void setNotificationsRequestTest() {
                UUID sourceAccountId = getSourceAccountUuid(veloAPIProperties.getPayorIdUuid());

                SetNotificationsRequest request = new SetNotificationsRequest();
                request.setMinimumBalance(1000L);

                sourceAccountsApi.setNotificationsRequest(sourceAccountId, request);
            }
        }

        @DisplayName("V2")
        @Nested
        class FundingManagerV2Tests {
            @DisplayName("Test Get Source Accounts V2")
            @Test
            void getSourceAccountsV2Test() {
                ListSourceAccountResponseV2 response = sourceAccountsApi.getSourceAccountsV2(null, null, veloAPIProperties.getPayorIdUuid(), null,1, 25, null);
                assertNotNull(response);
                assertThat(response.getContent().size()).isGreaterThan(0);
            }

            @DisplayName("Test Get Source Account V2")
            @Test
            void getSourceAccountV2Test() {
                UUID sourceAccountId = getSourceAccountUuid(UUID.fromString(veloAPIProperties.getPayorId()));

                SourceAccountResponseV2 sourceAccountResponse = sourceAccountsApi.getSourceAccountV2(sourceAccountId);

                assertNotNull(sourceAccountResponse);
            }
        }

        private UUID getSourceAccountUuid(UUID payorIdUuid) {
            ListSourceAccountResponseV3 listSourceAccountResponse = sourceAccountsApi.getSourceAccountsV3(null, null, payorIdUuid, null, false, null, 1, 25, null);
            return listSourceAccountResponse.getContent().get(0).getId();
        }
    }
