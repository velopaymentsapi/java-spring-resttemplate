package com.velopayments.oa3.config;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.api.*;
import com.velopayments.oa3.client.ApiClient;
import com.velopayments.oa3.client.ApiClientDecorator;
import com.velopayments.oa3.services.VeloApiTokenService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Configuration
@EnableCaching
@ComponentScan(basePackages = {"com.velopayments.oa3"},
    excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ApiClient.class)})
public class VeloConfig {

    public static final String VELO_API_APIKEY = "VELO_API_APIKEY";
    public static final String VELO_API_APISECRET = "VELO_API_APISECRET";
    public static final String VELO_API_PAYORID = "VELO_API_PAYORID";
    public static final String VELO_BASE_URL = "VELO_BASE_URL";

    /**
     * Set these configuration values in system or environment variables.
     *
     * @return
     */
    @Bean
    VeloAPIProperties veloAPIProperties(){
        VeloAPIProperties veloAPIProperties = new VeloAPIProperties();

        if (!StringUtils.isEmpty(System.getProperty(VELO_BASE_URL))) {
            veloAPIProperties.setBaseUrl(System.getProperty(VELO_BASE_URL));
        } else if (!StringUtils.isEmpty(System.getenv(VELO_BASE_URL))) {
            veloAPIProperties.setBaseUrl(System.getenv(VELO_BASE_URL));
        }

        if (!StringUtils.isEmpty(System.getProperty(VELO_API_PAYORID))) {
            veloAPIProperties.setPayorId(System.getProperty(VELO_API_PAYORID));
        } else if (!StringUtils.isEmpty(System.getenv(VELO_API_PAYORID))) {
            veloAPIProperties.setPayorId(System.getenv(VELO_API_PAYORID));
        }

        if (!StringUtils.isEmpty(System.getProperty(VELO_API_APIKEY))) {
            veloAPIProperties.setApiKey(UUID.fromString(System.getProperty(VELO_API_APIKEY)));
        } else if (!StringUtils.isEmpty(UUID.fromString(System.getenv(VELO_API_APIKEY)))) {
            veloAPIProperties.setApiKey(UUID.fromString(System.getenv(VELO_API_APIKEY)));
        }

        if (!StringUtils.isEmpty(System.getProperty(VELO_API_APISECRET))) {
            veloAPIProperties.setApiSecret(UUID.fromString(System.getProperty(VELO_API_APISECRET)));
        } else if (!StringUtils.isEmpty(UUID.fromString(System.getenv(VELO_API_APISECRET)))) {
            veloAPIProperties.setApiSecret(UUID.fromString(System.getenv(VELO_API_APISECRET)));
        }

        return veloAPIProperties;
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
    public ApiClient authApiClient(RestTemplateBuilder restTemplateBuilder, VeloAPIProperties veloAPIProperties){
        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setUsername(veloAPIProperties.getApiKey().toString());
        apiClient.setPassword(veloAPIProperties.getApiSecret().toString());
        return apiClient;
    }

    @Bean
    public LoginApi loginApi(ApiClient apiClient){
        return new LoginApi(apiClient);
    }

    private ApiClient buildTokenApiClient(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        ApiClient apiClient = new ApiClientDecorator(restTemplateBuilder.build(), veloApiTokenService);
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
