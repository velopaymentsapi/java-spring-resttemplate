package com.velopayments.oa3.config;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.VeloAuthProperties;
import com.velopayments.oa3.api.*;
import com.velopayments.oa3.client.ApiClient;
import com.velopayments.oa3.services.VeloApiTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;

import java.util.UUID;

@Configuration
@EnableCaching
@ComponentScan(basePackages = {"com.velopayments.oa3"},
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ApiClient.class)})
public class VeloConfig {

    @Bean
    VeloAPIProperties veloAPIProperties(@Value("${velo.base.url}") String baseUrl,
                                        @Value("${velo.api.payorid}") String payorId){
        return new VeloAPIProperties(baseUrl, payorId);
    }

    @Bean
    VeloAuthProperties veloAuthProperties(@Value("${velo.api.payorid}") String payorId,
                                          @Value("${velo.api.apikey}") String apiKey,
                                          @Value("${velo.api.apisecret}") String apiSecret) {
        return VeloAuthProperties
                .builder()
                .payorId(UUID.fromString(payorId))
                .apiKey(UUID.fromString(apiKey))
                .apiSecretKey(UUID.fromString(apiSecret))
                .build();
    }

    @Bean
    public Cache cache(){
        return new ConcurrentMapCache("veloAuthTokenCache");
    }

    @Bean
    public ConcurrentMapCacheManager concurrentMapCacheManager(){
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(VeloRestTemplateCustomizer veloRestTemplateCustomizer){
        return new RestTemplateBuilder(veloRestTemplateCustomizer);
    }

    @Bean("authApiClient")
    public ApiClient authApiClient(RestTemplateBuilder restTemplateBuilder, VeloAuthProperties veloAuthProperties){
        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setUsername(veloAuthProperties.getApiKey().toString());
        apiClient.setPassword(veloAuthProperties.getApiSecretKey().toString());
        return apiClient;
    }

    @Bean
    public LoginApi loginApi(ApiClient apiClient){
        return new LoginApi(apiClient);
    }

    private ApiClient buildTokenApiClient(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setAccessToken(veloApiTokenService.getToken());
        return apiClient;
    }

    @Bean
    @Lazy
    public CountriesApi countriesApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new CountriesApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public CurrenciesApi currenciesApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new CurrenciesApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public FundingManagerApi fundingManagerApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new FundingManagerApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public GetPayoutApi getPayoutApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new GetPayoutApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public InstructPayoutApi instructPayoutApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new InstructPayoutApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public PayeeInvitationApi payeeInvitationApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new PayeeInvitationApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public PayeesApi  payeesApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new PayeesApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public PaymentAuditServiceApi paymentAuditServiceApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new PaymentAuditServiceApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }


    @Bean
    @Lazy
    public PayorsApi payorsApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new PayorsApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public PayoutHistoryApi payoutHistoryApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new PayoutHistoryApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public QuotePayoutApi quotePayoutApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new QuotePayoutApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public SubmitPayoutApi submitPayoutApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new SubmitPayoutApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public TokensApi tokensApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new TokensApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public UsersApi usersApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new UsersApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }

    @Bean
    @Lazy
    public WithdrawPayoutApi withdrawPayoutApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        return new WithdrawPayoutApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService));
    }
}
