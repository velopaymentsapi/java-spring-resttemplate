package com.velopayments.oa3.config;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.api.*;
import com.velopayments.oa3.client.ApiClient;
import com.velopayments.oa3.client.ApiClientDecorator;
import com.velopayments.oa3.client.auth.HttpBasicAuth;
import com.velopayments.oa3.client.auth.HttpBearerAuth;
import com.velopayments.oa3.services.VeloApiTokenService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Configuration
@EnableCaching
@EnableScheduling
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
    VeloAPIProperties veloAPIProperties(Environment env){
        VeloAPIProperties veloAPIProperties = new VeloAPIProperties();

        String baseUrl = env.getProperty("velo.base.url");

        if (StringUtils.hasText(baseUrl)) {
            veloAPIProperties.setBaseUrl(baseUrl);
        } else if (StringUtils.hasText(System.getProperty(VELO_BASE_URL))) {
            veloAPIProperties.setBaseUrl(System.getProperty(VELO_BASE_URL));
        } else if (StringUtils.hasText(System.getenv(VELO_BASE_URL))) {
            veloAPIProperties.setBaseUrl(System.getenv(VELO_BASE_URL));
        }

        String apiPayorId = env.getProperty("velo.api.payorid");

        if (StringUtils.hasText(apiPayorId)){
            veloAPIProperties.setPayorId(apiPayorId);
        } else if (StringUtils.hasText(System.getProperty(VELO_API_PAYORID))) {
            veloAPIProperties.setPayorId(System.getProperty(VELO_API_PAYORID));
        } else if (StringUtils.hasText(System.getenv(VELO_API_PAYORID))) {
            veloAPIProperties.setPayorId(System.getenv(VELO_API_PAYORID));
        }

        String apiKey = env.getProperty("velo.api.apikey");

        if (StringUtils.hasText(apiKey)) {
            veloAPIProperties.setApiKey(UUID.fromString(apiKey));
        } else if (StringUtils.hasText(System.getProperty(VELO_API_APIKEY))) {
            veloAPIProperties.setApiKey(UUID.fromString(System.getProperty(VELO_API_APIKEY)));
        } else if (StringUtils.hasText(System.getenv(VELO_API_APIKEY))) {
            veloAPIProperties.setApiKey(UUID.fromString(System.getenv(VELO_API_APIKEY)));
        }

        String apiSecret = env.getProperty("velo.api.apisecret");

        if (StringUtils.hasText(apiSecret)) {
            veloAPIProperties.setApiSecret(UUID.fromString(apiSecret));
        } else if (StringUtils.hasText(System.getProperty(VELO_API_APISECRET))) {
            veloAPIProperties.setApiSecret(UUID.fromString(System.getProperty(VELO_API_APISECRET)));
        } else if (StringUtils.hasText(System.getenv(VELO_API_APISECRET))) {
            veloAPIProperties.setApiSecret(UUID.fromString(System.getenv(VELO_API_APISECRET)));
        }

        //verify required properties are set
        assert(StringUtils.hasText(veloAPIProperties.getBaseUrl()));
        assert(StringUtils.hasText(veloAPIProperties.getPayorId()));
        assert (veloAPIProperties.getApiKey() != null);
        assert (veloAPIProperties.getApiSecret() != null);
        assert(StringUtils.hasText(veloAPIProperties.getApiKey().toString()));
        assert(StringUtils.hasText(veloAPIProperties.getApiSecret().toString()));

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

        assert(StringUtils.hasText(veloAPIProperties.getPayorId()));
        assert (veloAPIProperties.getApiKey() != null);
        assert (veloAPIProperties.getApiSecret() != null);
        assert(StringUtils.hasText(veloAPIProperties.getApiKey().toString()));
        assert(StringUtils.hasText(veloAPIProperties.getApiSecret().toString()));

        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setUsername(veloAPIProperties.getApiKey().toString());
        apiClient.setPassword(veloAPIProperties.getApiSecret().toString());
        apiClient.setBasePath(veloAPIProperties.getBaseUrl());
        return apiClient;
    }

    @Bean
    public LoginApi loginApi(ApiClient apiClient){
        return new LoginApi(apiClient);
    }

    private ApiClient buildTokenApiClient(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService,
                                          VeloAPIProperties veloAPIProperties){
        ApiClient apiClient = new ApiClientDecorator(restTemplateBuilder.build(), veloApiTokenService);
        apiClient.setBasePath(veloAPIProperties.getBaseUrl());
        apiClient.setAccessToken(veloApiTokenService.getToken());
        return apiClient;
    }

    @Bean
    @Lazy
    public CountriesApi countriesApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService,
                                     VeloAPIProperties veloAPIProperties){
        return new CountriesApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }

    @Bean
    @Lazy
    public CurrenciesApi currenciesApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService,
                                       VeloAPIProperties veloAPIProperties){
        return new CurrenciesApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }

    @Bean
    @Lazy
    public FundingManagerApi fundingManagerApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService,
                                               VeloAPIProperties veloAPIProperties){
        return new FundingManagerApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }

    @Bean
    @Lazy
    public PayeeInvitationApi payeeInvitationApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService, VeloAPIProperties veloAPIProperties){
        return new PayeeInvitationApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }

    @Bean
    @Lazy
    public PayeesApi  payeesApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService,
                                VeloAPIProperties veloAPIProperties){
        return new PayeesApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }

    @Bean
    @Lazy
    public PaymentAuditServiceApi paymentAuditServiceApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService,
                                                         VeloAPIProperties veloAPIProperties){
        return new PaymentAuditServiceApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }


    @Bean
    @Lazy
    public PayorsApi payorsApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService, VeloAPIProperties veloAPIProperties){
        return new PayorsApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }

    @Bean
    @Lazy
    public PayoutServiceApi payoutServiceApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService,
                                             VeloAPIProperties veloAPIProperties){
        return new PayoutServiceApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }

    @Bean
    @Lazy
    public TokensApi tokensApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService, VeloAPIProperties veloAPIProperties){
        return new TokensApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }

    @Bean
    @Lazy
    public UsersApi usersApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService, VeloAPIProperties veloAPIProperties){
        return new UsersApi(buildTokenApiClient(restTemplateBuilder, veloApiTokenService, veloAPIProperties));
    }
}
