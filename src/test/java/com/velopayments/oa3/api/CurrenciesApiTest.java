package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.SupportedCurrencyResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class CurrenciesApiTest extends BaseApiTest {

    @Autowired
    CurrenciesApi currenciesApi;

    @Test
    void listSupportedCurrenciesTest() {
        SupportedCurrencyResponse response = currenciesApi.listSupportedCurrencies();

        assertNotNull(response);
    }
}
