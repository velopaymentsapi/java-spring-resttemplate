package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.PaymentChannelRulesResponse;
import com.velopayments.oa3.model.SupportedCountriesResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class CountriesApiTest extends BaseApiTest {

    @Autowired
    CountriesApi countriesApi;

    @Test
    void testListCountries() {

        SupportedCountriesResponse response = countriesApi.listSupportedCountries();

        assertNotNull(response);
    }

    @Test
    void v1PaymentChannelRulesGetTest() {

        PaymentChannelRulesResponse response = countriesApi.v1PaymentChannelRulesGet();

        assertNotNull(response);
    }
}
