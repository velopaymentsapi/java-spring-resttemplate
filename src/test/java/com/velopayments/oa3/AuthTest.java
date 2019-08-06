package com.velopayments.oa3;

import com.velopayments.oa3.api.CountriesApi;
import com.velopayments.oa3.client.ApiClient;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.SupportedCountriesResponse;
import com.velopayments.oa3.services.VeloApiTokenService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class AuthTest extends BaseApiTest{

   // @Autowired
   // AuthApi authApi;

    @Autowired
    RestTemplateBuilder restTemplateBuilder;

    @Autowired
    VeloApiTokenService veloApiTokenService;

    @Test
    void testGetAuth()  {

        String token = veloApiTokenService.getToken();

        assertNotNull(token);

        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setAccessToken(token);

        CountriesApi countriesApi = new CountriesApi(apiClient);

        SupportedCountriesResponse supportedCountriesResponse = countriesApi.listSupportedCountries();

        assertNotNull(supportedCountriesResponse);

        System.out.println(supportedCountriesResponse.getCountries());
    }

    @Disabled
    @Test
    void testGetAuthSecondToken()  {

        String token = veloApiTokenService.getToken();

        assertNotNull(token);

        ApiClient apiClient = new ApiClient(restTemplateBuilder.build());
        apiClient.setAccessToken(token);

        CountriesApi countriesApi = new CountriesApi(apiClient);

        SupportedCountriesResponse supportedCountriesResponse = countriesApi.listSupportedCountries();

        assertNotNull(supportedCountriesResponse);

        veloApiTokenService.getToken(); // get second token

        //make call with 1st token
        System.out.println("Calling with first token");
        supportedCountriesResponse = countriesApi.listSupportedCountries(); //goes boom
    }
}
