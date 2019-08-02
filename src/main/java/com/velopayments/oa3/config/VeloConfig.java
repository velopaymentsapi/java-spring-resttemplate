package com.velopayments.oa3.config;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.VeloAuthProperties;
import com.velopayments.oa3.api.AuthApi;
import com.velopayments.oa3.api.CountriesApi;
import com.velopayments.oa3.api.CurrenciesApi;
import com.velopayments.oa3.api.FundingManagerApi;
import com.velopayments.oa3.client.ApiClient;
import com.velopayments.oa3.services.VeloApiTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.*;

import java.util.UUID;

@Configuration
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
    public AuthApi authApi(ApiClient authApiClient){
        return new AuthApi(authApiClient);
    }

    @Bean
    @Lazy
    public CountriesApi countriesApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setAccessToken(veloApiTokenService.getToken());
        return new CountriesApi(apiClient);
    }

    @Bean
    @Lazy
    public CurrenciesApi currenciesApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setAccessToken(veloApiTokenService.getToken());
        return new CurrenciesApi(apiClient);
    }

    @Bean
    @Lazy
    public FundingManagerApi fundingManagerApi(RestTemplateBuilder restTemplateBuilder, VeloApiTokenService veloApiTokenService){
        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setAccessToken(veloApiTokenService.getToken());
        return new FundingManagerApi(apiClient);
    }
}
