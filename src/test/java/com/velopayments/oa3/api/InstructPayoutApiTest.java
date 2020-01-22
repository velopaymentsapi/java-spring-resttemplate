package com.velopayments.oa3.api;

import com.velopayments.oa3.config.VeloConfig;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class InstructPayoutApiTest {

    @Autowired
    InstructPayoutApi instructPayoutApi;

    @Disabled
    @Test
    void testInstructBadPayout() {

        //should fail
        instructPayoutApi.v3PayoutsPayoutIdPost(UUID.randomUUID());
    }
}
